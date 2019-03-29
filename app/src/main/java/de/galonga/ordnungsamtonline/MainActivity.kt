package de.galonga.ordnungsamtonline

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import de.galonga.ordnungsamtonline.clients.HybridWebChromeClient
import de.galonga.ordnungsamtonline.clients.HybridWebViewClient


class MainActivity : AppCompatActivity() {
    companion object {
        const val BASE_URL = "https://ordnungsamt.berlin.de/frontend.mobile/"
        // Token: 0x04000007 RID: 7
        const val REQUEST_CODE_CAMERA = 15
        // Token: 0x04000008 RID: 8
        const val REQUEST_CODE_FILE_CHOOSER = 255
    }

    private lateinit var hybridWebChromeClient: HybridWebChromeClient
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.contentWebView)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setGeolocationEnabled(true)
        // webView.settings.setGeolocationDatabasePath(System.Environment.GetFolderPath(System.Environment.SpecialFolder.MyDocuments));
        webView.settings.allowContentAccess = true

        webView.setInitialScale(100)

        CookieSyncManager.createInstance(this)
        CookieManager.getInstance().removeAllCookie()
        webView.webViewClient = HybridWebViewClient(this)
        hybridWebChromeClient = HybridWebChromeClient(this)
        webView.webChromeClient = hybridWebChromeClient
        webView.loadUrl(BASE_URL)
    }

    override fun onBackPressed() {
        if (!webView.url.contains("#start") && webView.canGoBack()) {
            webView.goBack()

            return
        }

        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data?.let {
            if (requestCode == REQUEST_CODE_CAMERA || requestCode == REQUEST_CODE_FILE_CHOOSER) {
                // hybridWebChromeClient.setReceivedValue(requestCode, resultCode, data)

                return
            }
        }
    }
}
