package de.galonga.ordnungsamtonline.clients

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import androidx.annotation.RequiresApi
import de.galonga.ordnungsamtonline.MainActivity


class HybridWebChromeClient constructor(private val mainActivity: MainActivity) : WebChromeClient() {

    // private var uploadMessage = ValueCallback<Uri>()

    private var outputFileUri: Uri? = null

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        callback?.invoke(origin, true, false)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setReceivedValue(requestCode: Int, resultCode: Int, data: Intent) {
        //     uploadMessage?.let {
        when (requestCode) {
            MainActivity.REQUEST_CODE_CAMERA -> {//value = this.GetReceivedValueBelowLollipop(resultCode, data)
            }
            MainActivity.REQUEST_CODE_FILE_CHOOSER -> {
                val value = getReceivedValueLollipopAndAbove(resultCode, data)
                //               uploadMessage.onReceiveValue(value)
                // uploadMessage = null
            }

        }
    }

    // Token: 0x06000009 RID: 9 RVA: 0x0000214C File Offset: 0x0000034C
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getReceivedValueLollipopAndAbove(resultCode: Int, data: Intent): Uri? {
        var value = WebChromeClient.FileChooserParams.parseResult(resultCode, data)

        if (isCameraResult(resultCode, value)) {

        }

        return value?.firstOrNull()
    }

    private fun isCameraResult(resultCode: Int, value: Array<Uri>?): Boolean {
        return value == null && resultCode == Activity.RESULT_OK
    }
}