package com.example.wydatki.ui

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.OutputStream

class AndroidFilePicker(private val activity: ComponentActivity) : FilePicker {
    
    private var exportContent: String? = null
    private var onExportResult: ((Boolean, String) -> Unit)? = null
    
    private var onImportResult: ((String?) -> Unit)? = null

    private val createDocumentLauncher = activity.registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            try {
                activity.contentResolver.openOutputStream(uri)?.use { 
                    it.write(exportContent?.toByteArray() ?: byteArrayOf())
                }
                onExportResult?.invoke(true, "Kopia została zapisana.")
            } catch (e: Exception) {
                onExportResult?.invoke(false, "Nie udało się zapisać kopii.")
            }
        } else {
            onExportResult?.invoke(false, "Anulowano zapis.")
        }
    }

    private val openDocumentLauncher = activity.registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                val json = activity.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                onImportResult?.invoke(json)
            } catch (e: Exception) {
                onImportResult?.invoke(null)
            }
        } else {
            onImportResult?.invoke(null)
        }
    }

    override fun exportJson(content: String, fileName: String, onResult: (Boolean, String) -> Unit) {
        exportContent = content
        onExportResult = onResult
        createDocumentLauncher.launch(fileName)
    }

    override fun importJson(onResult: (String?) -> Unit) {
        onImportResult = onResult
        openDocumentLauncher.launch(arrayOf("application/json", "text/*"))
    }
}
