package de.galonga.ordnungsamtonline.clients

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import de.galonga.ordnungsamtonline.MainActivity

class HybridWebChromeClient constructor(private val mainActivity: MainActivity) : WebChromeClient() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 200
    }

    // private var uploadMessage = ValueCallback<Uri>()

    private var outputFileUri: Uri? = null

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
        requestPermission()
        turnGPSOn()

        callback?.invoke(origin, true, false)
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(mainActivity, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show()
        } else {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    private fun turnGPSOn() {
        val provider = Settings.Secure.getString(mainActivity.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)

        if (!provider.contains("gps")) {
            //if gps is disabled
            val poke = Intent()
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider")
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            mainActivity.sendBroadcast(poke)
        }
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