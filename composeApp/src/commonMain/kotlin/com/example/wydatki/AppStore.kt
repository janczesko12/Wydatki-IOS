package com.example.wydatki

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.wydatki.data.CloudPullResult
import com.example.wydatki.data.FirebaseSync
import com.example.wydatki.data.Storage
import com.example.wydatki.models.AppData
import com.example.wydatki.models.Category
import com.example.wydatki.models.Expense
import com.example.wydatki.models.Income
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AppStore(private val storage: Storage) {
    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val storeScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Serializes cloud pull/push operations so an older request cannot finish
    // after a newer one and overwrite it.
    private val cloudSyncMutex = Mutex()

    // Incremented for every local data mutation. A pull started at version N
    // must not replace local data if the user changed anything while it ran.
    private var localMutationVersion = 0L

    var categories by mutableStateOf(loadCategories())
        private set
    var expenses by mutableStateOf(loadExpenses())
        private set
    var incomes by mutableStateOf(loadIncomes())
        private set
    var cloudStatus by mutableStateOf("Chmura: przygotowywanie…")
        private set
    var securityPin by mutableStateOf(storage.getString("security_pin"))
        private set
    var biometricEnabled by mutableStateOf(storage.getBoolean("biometric_enabled", false))
        private set

    init {
        if (FirebaseSync.isLoggedIn) {
            cloudPull()
        } else {
            updateCloudStatus("Chmura: zaloguj się")
        }
    }

    fun addExpense(x: Expense) {
        expenses = expenses + x
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun updateExpense(x: Expense) {
        expenses = expenses.map { if (it.id == x.id) x else it }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun deleteExpense(id: String) {
        expenses = expenses.filterNot { it.id == id }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun addIncome(x: Income) {
        incomes = incomes + x
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun updateIncome(x: Income) {
        incomes = incomes.map { if (it.id == x.id) x else it }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun deleteIncome(id: String) {
        incomes = incomes.filterNot { it.id == id }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun addCategory(x: Category) {
        categories = categories + x
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun updateCategory(x: Category) {
        categories = categories.map { if (it.id == x.id) x else it }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun deleteCategory(id: String) {
        if (expenses.any { it.categoryId == id }) return
        categories = categories.filterNot { it.id == id }
        markLocalMutation()
        saveAll()
        cloudPush()
    }

    fun replaceAll(c: List<Category>, e: List<Expense>, i: List<Income>) {
        categories = c
        expenses = e
        incomes = i
        markLocalMutation()
        saveAll()
    }

    fun updateCloudStatus(value: String) {
        cloudStatus = value
    }

    fun setPin(pin: String?) {
        securityPin = pin
        if (pin != null) {
            storage.putString("security_pin", pin)
        } else {
            storage.remove("security_pin")
        }
    }

    fun setBiometric(enabled: Boolean) {
        biometricEnabled = enabled
        storage.putBoolean("biometric_enabled", enabled)
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        storeScope.launch {
            try {
                FirebaseSync.login(email, password)
                updateCloudStatus("Chmura: połączona")
                cloudPull()
                onResult(true, "Zalogowano")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd logowania")
            }
        }
    }

    fun register(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        storeScope.launch {
            try {
                FirebaseSync.register(email, password)
                updateCloudStatus("Chmura: połączona")

                // A newly created account has no document yet. Serialize the
                // current local state through the same push queue.
                cloudPush()
                onResult(true, "Konto utworzone")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Błąd rejestracji")
            }
        }
    }

    fun logout() {
        storeScope.launch {
            try {
                FirebaseSync.logout()
                updateCloudStatus("Chmura: wylogowano")
            } catch (e: Exception) {
                updateCloudStatus("Chmura: błąd wylogowania")
            }
        }
    }

    fun cloudPush() {
        if (!FirebaseSync.isLoggedIn) return

        storeScope.launch {
            cloudSyncMutex.withLock {
                try {
                    // Snapshot is created only after acquiring the mutex. If an
                    // earlier push was still running, this exports the newest
                    // local state instead of a stale snapshot.
                    val snapshot = exportJson()
                    FirebaseSync.push(snapshot)
                    updateCloudStatus("Chmura: zapisano")
                } catch (e: Exception) {
                    updateCloudStatus("Chmura: błąd zapisu")
                }
            }
        }
    }

    fun cloudPull() {
        if (!FirebaseSync.isLoggedIn) return

        storeScope.launch {
            cloudSyncMutex.withLock {
                val versionAtStart = localMutationVersion

                when (val result = FirebaseSync.pull()) {
                    is CloudPullResult.Data -> {
                        if (localMutationVersion != versionAtStart) {
                            // Local data changed while the pull was in flight.
                            // Never overwrite those fresh changes with the
                            // older cloud snapshot. Push the current state.
                            try {
                                FirebaseSync.push(exportJson())
                                updateCloudStatus("Chmura: zapisano nowsze dane")
                            } catch (e: Exception) {
                                updateCloudStatus("Chmura: błąd zapisu")
                            }
                        } else if (importJsonFromCloud(result.json)) {
                            updateCloudStatus("Chmura: zsynchronizowano")
                        }
                    }

                    CloudPullResult.DocumentMissing -> {
                        // Only a confirmed missing document is allowed to
                        // initialize the cloud from local data.
                        try {
                            FirebaseSync.push(exportJson())
                            updateCloudStatus("Chmura: zapisano")
                        } catch (e: Exception) {
                            updateCloudStatus("Chmura: błąd zapisu")
                        }
                    }

                    is CloudPullResult.Error -> {
                        // A network/Firebase error is NOT the same thing as an
                        // empty cloud. Never overwrite the cloud in this case.
                        updateCloudStatus("Chmura: błąd odczytu")
                    }
                }
            }
        }
    }

    fun exportJson(): String {
        val data = AppData(
            categories = categories,
            expenses = expenses,
            incomes = incomes
        )
        return json.encodeToString(data)
    }

    fun importJson(jsonString: String): Boolean {
        return try {
            val data = json.decodeFromString<AppData>(jsonString)
            replaceAll(data.categories, data.expenses, data.incomes)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun importJsonFromCloud(raw: String): Boolean {
        return try {
            val data = json.decodeFromString<AppData>(raw)
            // A cloud pull is not a local mutation, so do not increment the
            // local version here.
            categories = data.categories
            expenses = data.expenses
            incomes = data.incomes
            saveAll()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun markLocalMutation() {
        localMutationVersion++
    }

    private fun saveAll() {
        storage.putString("categories", json.encodeToString(categories))
        storage.putString("expenses", json.encodeToString(expenses))
        storage.putString("incomes", json.encodeToString(incomes))
    }

    private fun loadCategories(): List<Category> {
        val raw = storage.getString("categories") ?: return defaultCategories()
        return try {
            json.decodeFromString<List<Category>>(raw)
        } catch (e: Exception) {
            defaultCategories()
        }
    }

    private fun loadExpenses(): List<Expense> {
        val raw = storage.getString("expenses") ?: return emptyList()
        return try {
            json.decodeFromString<List<Expense>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun loadIncomes(): List<Income> {
        val raw = storage.getString("incomes") ?: return emptyList()
        return try {
            json.decodeFromString<List<Income>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun defaultCategories() = listOf(
        Category("oplaty", "Opłaty Dom", "🏠", 0),
        Category("gaz", "Gaz za cały dom", "🔥", 1),
        Category("jedzenie", "Jedzenie", "🛒", 2),
        Category("auto", "Auto", "🚗", 3),
        Category("uroda", "Uroda", "💄", 4),
        Category("rozrywka", "Rozrywka", "🎮", 5),
        Category("szkola", "Szkoła", "🎓", 6),
        Category("dom", "Art. do domu", "🏡", 7),
        Category("franek", "Ciuchy Franek", "👕", 8),
        Category("janek", "Ciuchy Janek", "👕", 9),
        Category("karolina", "Ciuchy Karolina", "👗", 10),
        Category("praca", "Praca", "💼", 11),
        Category("prezenty", "Prezenty", "🎁", 12),
        Category("zdrowie", "Zdrowie", "❤️", 13),
        Category("kredyt", "Rata kredytu", "🏦", 14),
        Category("inne", "Inne", "📦", 15)
    )
}
