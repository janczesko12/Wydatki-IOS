package com.example.wydatki.ui

import platform.UIKit.*
import platform.Foundation.*
import platform.UniformTypeIdentifiers.*
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
class IosFilePicker : FilePicker {

    // Trzymamy silną referencję do delegata, aby nie został usunięty przez GC,
    // ponieważ UIDocumentPickerViewController trzyma go słabo (weak).
    private var currentDelegate: NSObject? = null

    override fun exportJson(content: String, fileName: String, onResult: (Boolean, String) -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            val finalFileName = if (fileName.lowercase().endsWith(".json")) fileName else "$fileName.json"
            val tempDir = NSTemporaryDirectory()
            val fileURL = NSURL.fileURLWithPath(tempDir).URLByAppendingPathComponent(finalFileName)
            
            if (fileURL == null) {
                onResult(false, "Błąd tworzenia ścieżki pliku.")
                return@dispatch_async
            }

            val nsData = (content as NSString).dataUsingEncoding(NSUTF8StringEncoding)
            if (nsData == null) {
                onResult(false, "Błąd przygotowania danych.")
                return@dispatch_async
            }

            if (!nsData.writeToURL(fileURL, true)) {
                onResult(false, "Błąd zapisu pliku tymczasowego.")
                return@dispatch_async
            }

            val picker = UIDocumentPickerViewController(
                forExportingURLs = listOf(fileURL),
                asCopy = true
            )
            
            val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                    currentDelegate = null
                    onResult(true, "Plik wyeksportowany pomyślnie.")
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    currentDelegate = null
                    onResult(false, "Eksport anulowany.")
                }
            }
            
            currentDelegate = delegate
            picker.delegate = delegate
            present(picker)
        }
    }

    override fun importJson(onResult: (String?) -> Unit) {
        dispatch_async(dispatch_get_main_queue()) {
            // UTTypeJSON wymaga iOS 14+
            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = listOf(UTTypeJSON),
                asCopy = true
            )
            
            val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(controller: UIDocumentPickerViewController, didPickDocumentsAtURLs: List<*>) {
                    currentDelegate = null
                    val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                    if (url == null) {
                        onResult(null)
                        return
                    }

                    val isAccessing = url.startAccessingSecurityScopedResource()
                    try {
                        val content = NSString.stringWithContentsOfURL(url, encoding = NSUTF8StringEncoding, error = null)
                        onResult(content as? String)
                    } catch (e: Exception) {
                        onResult(null)
                    } finally {
                        if (isAccessing) url.stopAccessingSecurityScopedResource()
                    }
                }

                override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                    currentDelegate = null
                    onResult(null)
                }
            }
            
            currentDelegate = delegate
            picker.delegate = delegate
            present(picker)
        }
    }

    private fun present(controller: UIViewController) {
        val topController = getTopViewController()
        if (topController != null) {
            topController.presentViewController(controller, animated = true, completion = null)
        }
    }

    private fun getTopViewController(): UIViewController? {
        val window = UIApplication.sharedApplication.connectedScenes
            .mapNotNull { it as? UIWindowScene }
            .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
            ?.windows
            ?.mapNotNull { it as? UIWindow }
            ?.firstOrNull { it.isKeyWindow() }
            ?: UIApplication.sharedApplication.keyWindow

        var topController = window?.rootViewController
        while (topController?.presentedViewController != null) {
            topController = topController.presentedViewController
        }
        return topController
    }
}
