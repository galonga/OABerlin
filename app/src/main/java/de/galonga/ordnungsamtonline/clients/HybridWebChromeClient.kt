package de.galonga.ordnungsamtonline.clients

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.annotation.RequiresApi
import de.galonga.ordnungsamtonline.MainActivity
import de.galonga.ordnungsamtonline.R
import de.galonga.ordnungsamtonline.services.PermissionService
import java.io.File
import java.util.UUID

class HybridWebChromeClient constructor(private val mainActivity: MainActivity) : WebChromeClient() {
    private val permissionService = PermissionService()
    private var outputFileUri: Uri
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    init {
        outputFileUri = Uri.fromFile(createImageFileForCamera())
    }

    override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
        permissionService.requestGeoPermission(mainActivity)
        turnGPSOn()

        callback?.invoke(origin, true, false)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        uploadMessage = filePathCallback
        mainActivity.startActivityForResult(createIntentChooser(), MainActivity.REQUEST_CODE_CAMERA)

        return true
    }

    fun setReceivedValue(requestCode: Int, resultCode: Int, data: Intent?) {
        uploadMessage?.let {
            when (requestCode) {
                MainActivity.REQUEST_CODE_CAMERA -> {
                    getReceivedValueBelowLollipop(resultCode, data)?.let { uri ->
                        it.onReceiveValue(arrayOf(uri))
                    }
                }
                MainActivity.REQUEST_CODE_FILE_CHOOSER -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        it.onReceiveValue(getReceivedValueLollipopAndAbove(resultCode, data))
                    } else {
                        getReceivedValueBelowLollipop(resultCode, data)?.let { uri ->
                            it.onReceiveValue(arrayOf(uri))
                        }
                    }
                }
                else -> {
                    //ignore
                }
            }
        }

        uploadMessage = null
    }

    private fun createIntentChooser(): Intent {
        return Intent.createChooser(getAllCameraAndGalleryIntents(), "File Chooser")
    }

    private fun getAllCameraAndGalleryIntents(): Intent? {
        val intentList = arrayListOf<Intent>()
        val imageCaptueIntent = Intent("android.media.action.IMAGE_CAPTURE")

        mainActivity.packageManager.queryIntentActivities(imageCaptueIntent, 0).forEach { resolveInfo ->
            val subIntent = Intent(imageCaptueIntent)
            subIntent.component = ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name)
            subIntent.setPackage(resolveInfo.activityInfo.packageName)
            subIntent.putExtra("output", outputFileUri)
            intentList.add(subIntent)
        }

        val imageIntent = Intent().apply {
            type = "image/*"
            action = "android.intent.action.GET_CONTENT"
        }

        val chooserIntent = Intent.createChooser(imageIntent, mainActivity.getString(R.string.res_choose))
        chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", intentList.toArray())

        return chooserIntent
    }

    private fun createImageFileForCamera(): File {
        var file = mainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file?.let {
            if (!file.exists()) {
                file.mkdir()
            }
        } ?: run {
            file = mainActivity.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_WORLD_READABLE)
            if (!file!!.exists()) {
                file.mkdir()
            }
        }

        return File(file, "img_" + UUID.randomUUID().toString().substring(7) + ".jpg")
    }

    private fun turnGPSOn() {
        val provider =
            Settings.Secure.getString(mainActivity.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)

        if (!provider.contains("gps")) {
            //if gps is disabled
            val poke = Intent()
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider")
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
            poke.data = Uri.parse("3")
            mainActivity.sendBroadcast(poke)
        }
    }

    private fun getReceivedValueBelowLollipop(resultCode: Int, data: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }

        if (isCameraResult(resultCode, data?.data)) {
            return outputFileUri
        }

        return data?.data
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getReceivedValueLollipopAndAbove(resultCode: Int, data: Intent?): Array<Uri>? {
        var value = WebChromeClient.FileChooserParams.parseResult(resultCode, data)

        if (isCameraResult(resultCode, value)) {
            value = arrayOf(outputFileUri)
        }

        return value
    }

    private fun isCameraResult(resultCode: Int, value: Any?): Boolean {
        return value == null && resultCode == Activity.RESULT_OK
    }
}
