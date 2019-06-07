package de.galonga.ordnungsamtonline.clients

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import de.galonga.ordnungsamtonline.MainActivity
import de.galonga.ordnungsamtonline.services.PermissionService

class HybridWebViewClient constructor(private val mainActivity: MainActivity) : WebViewClient() {
    companion object {
        const val KEY_NEAR = "naehe"
        const val KEY_MAP = "karte"
        const val KEY_PHOTO = "foto"
    }

    private val permissionService = PermissionService()

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        url?.let {
            if ((url.startsWith("http:") || url.startsWith("https:")) && !url.startsWith(MainActivity.BASE_URL)) {
                val intent = Intent("android.intent.action.VIEW", Uri.parse(url))
                mainActivity.startActivity(intent)

                return true
            }

            if (url.startsWith("mailto:")) {
                val intent2 = Intent("android.intent.action.SEND")
                intent2.type = "application/octet-stream"
                intent2.putExtra("android.intent.extra.EMAIL", url.substring(7))
                mainActivity.startActivity(intent2)

                return true
            }
            view?.loadUrl(url)

            return true
        }

        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun onPageFinished(view: WebView?, url: String?) {

        url?.let {
            val pagePath = url.substringAfter("#")
            if (pagePath.length >= 0) {
                when (pagePath) {
                    KEY_NEAR -> permissionService.requestGeoPermission(mainActivity)
                    KEY_MAP -> permissionService.requestGeoPermission(mainActivity)
                    KEY_PHOTO -> permissionService.requestCameraPermission(mainActivity)
                }
            }
        }
    }
}
