package com.example.wydatki.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.max

data class ReleaseInfo(val tag: String, val apkUrl: String)

object GithubUpdater {
    private const val GITHUB_OWNER = "jancz" // Example, should be from BuildConfig in real app
    private const val GITHUB_REPO = "wydatki"
    private const val CURRENT_VERSION = "1.0.0"

    suspend fun check(): ReleaseInfo? = withContext(Dispatchers.IO) {
        try {
            val c = URL("https://api.github.com/repos/${GITHUB_OWNER}/${GITHUB_REPO}/releases/latest").openConnection() as HttpURLConnection
            c.connectTimeout = 7000
            c.readTimeout = 7000
            c.setRequestProperty("Accept", "application/vnd.github+json")
            val o = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val tag = o.getString("tag_name").removePrefix("v")
            if (!isNewer(tag, CURRENT_VERSION)) return@withContext null
            val assets = o.optJSONArray("assets") ?: return@withContext null
            for (i in 0 until assets.length()) {
                val a = assets.getJSONObject(i)
                if (a.getString("name").endsWith(".apk", true)) return@withContext ReleaseInfo(
                    tag,
                    a.getString("browser_download_url")
                )
            }
            null
        } catch (_: Exception) {
            null
        }
    }

    private fun isNewer(a: String, b: String): Boolean {
        fun parts(v: String) = v.split(".").map { it.filter(Char::isDigit).toIntOrNull() ?: 0 }
            .let { it + List(max(0, 3 - it.size)) { 0 } }
        val x = parts(a)
        val y = parts(b)
        for (i in 0..2) if (x[i] != y[i]) return x[i] > y[i]
        return false
    }

    suspend fun downloadAndInstall(
        context: Context,
        release: ReleaseInfo,
        onProgress: (Int) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val dir = File(context.cacheDir, "updates").apply { mkdirs() }
            val file = File(dir, "Wydatki-${release.tag}.apk")
            val connection = URL(release.apkUrl).openConnection() as HttpURLConnection
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            connection.connect()
            val total = connection.contentLengthLong
            var downloaded = 0L
            connection.inputStream.use { input ->
                file.outputStream().use { output ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count == -1) break
                        output.write(buffer, 0, count)
                        downloaded += count
                        if (total > 0) {
                            val progress = ((downloaded * 100L) / total).toInt().coerceIn(0, 100)
                            withContext(Dispatchers.Main) { onProgress(progress) }
                        }
                    }
                }
            }
            withContext(Dispatchers.Main) { onProgress(100) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !context.packageManager.canRequestPackageInstalls()) {
                withContext(Dispatchers.Main) {
                    context.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:${context.packageName}"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }
                return@withContext true
            }
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            withContext(Dispatchers.Main) { context.startActivity(intent) }
            true
        } catch (_: Exception) { false }
    }
}
