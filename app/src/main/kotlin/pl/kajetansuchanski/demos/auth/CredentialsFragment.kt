package pl.kajetansuchanski.demos.auth

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment

class CredentialsFragment : Fragment(), DeviceAuthFragmentInterface {
    override var onDeviceAuthResult: OnDeviceAuthResult? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCode.DEVICE_CREDENTIALS -> {
                val onDeviceAuthResult = onDeviceAuthResult ?: return
                if (resultCode == Activity.RESULT_OK) {
                    onDeviceAuthResult(true)
                } else {
                    onDeviceAuthResult(false)
                }
            }
        }
    }
}