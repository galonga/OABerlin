package de.galonga.ordnungsamtonline.services

import android.Manifest
import android.widget.Toast
import androidx.core.app.ActivityCompat
import de.galonga.ordnungsamtonline.MainActivity

class PermissionService {
    companion object {
        const val GEO_PERMISSION_REQUEST_CODE = 200
        const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    fun requestGeoPermission(mainActivity: MainActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                mainActivity,
                "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                GEO_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun requestCameraPermission(mainActivity: MainActivity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(mainActivity, Manifest.permission.CAMERA)) {
            Toast.makeText(
                mainActivity,
                "Camera permission allows us to make photos. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }
}
