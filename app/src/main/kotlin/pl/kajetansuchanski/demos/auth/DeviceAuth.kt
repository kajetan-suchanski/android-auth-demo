package pl.kajetansuchanski.demos.auth

import android.app.KeyguardManager
import android.os.Build
import androidx.fragment.app.Fragment

object DeviceAuth {
    fun <FragmentType> prompt(
        keyguardManager: KeyguardManager,
        fragment: FragmentType,
        onResult: OnDeviceAuthResult
    ) where FragmentType : Fragment, FragmentType : DeviceAuthFragmentInterface {
        if (Build.VERSION.SDK_INT >= 21) {
            val authIntent = keyguardManager.createConfirmDeviceCredentialIntent(
                fragment.getString(R.string.device_credential_prompt_title),
                fragment.getString(R.string.device_credential_prompt_description)
            )

            if (authIntent != null) {
                fragment.onDeviceAuthResult = onResult
                fragment.startActivityForResult(authIntent, RequestCode.DEVICE_CREDENTIALS)
                return
            }
        }

        onResult(true)
    }
}