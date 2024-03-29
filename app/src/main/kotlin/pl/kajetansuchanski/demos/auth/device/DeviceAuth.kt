package pl.kajetansuchanski.demos.auth.device

import android.app.KeyguardManager
import androidx.fragment.app.Fragment
import pl.kajetansuchanski.demos.auth.R
import pl.kajetansuchanski.demos.auth.RequestCode

object DeviceAuth {
    fun <FragmentType> prompt(
        keyguardManager: KeyguardManager,
        fragment: FragmentType,
        onResult: OnDeviceAuthResult
    ) where FragmentType : Fragment, FragmentType : DeviceAuthFragmentInterface {
        val authIntent = keyguardManager.createConfirmDeviceCredentialIntent(
            fragment.getString(R.string.device_credential_prompt_title),
            fragment.getString(R.string.device_credential_prompt_description)
        )

        if (authIntent != null) {
            fragment.onDeviceAuthResult = onResult
            fragment.startActivityForResult(authIntent, RequestCode.DEVICE_CREDENTIALS)
            return
        }

        onResult(true)
    }
}