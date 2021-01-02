package pl.kajetansuchanski.demos.auth

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

private typealias OnAuthResult = (status: BiometricAuth.Status, result: BiometricPrompt.AuthenticationResult?) -> Unit

object BiometricAuth {
    enum class Status {
        SUCCESS, FAILURE, LOCKOUT, LOCKOUT_PERMANENT, NONE_ENROLLED, NO_HARDWARE
    }

    fun prompt(
        fragment: Fragment,
        context: Context = fragment.requireContext(),
        onResult: OnAuthResult
    ) {
        when (BiometricManager.from(context).canAuthenticate()) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> onResult(Status.NONE_ENROLLED, null)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> onResult(Status.NO_HARDWARE, null)
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(context)
                val biometricPrompt =
                    BiometricPrompt(fragment, executor, AuthCallback(context, onResult))

                val promptInfo = getPromptInfo(context)

                biometricPrompt.authenticate(promptInfo)
            }
            else -> onResult(Status.FAILURE, null)
        }
    }

    private fun getPromptInfo(context: Context): BiometricPrompt.PromptInfo {
        val features = getAuthFeatures(context)
        val texts = when (features.size) {
            1 -> {
                when (features[0]) {
                    PackageManager.FEATURE_FINGERPRINT -> arrayOf(
                        R.string.biometric_prompt_title_fingerprint,
                        R.string.biometric_prompt_subtitle_fingerprint,
                        R.string.biometric_prompt_negative_fingerprint
                    )
                    PackageManager.FEATURE_FACE -> arrayOf(
                        R.string.biometric_prompt_title_face,
                        R.string.biometric_prompt_subtitle_face,
                        R.string.biometric_prompt_negative_face
                    )
                    PackageManager.FEATURE_IRIS -> arrayOf(
                        R.string.biometric_prompt_title_iris,
                        R.string.biometric_prompt_subtitle_iris,
                        R.string.biometric_prompt_negative_iris
                    )
                    else -> arrayOf(
                        R.string.biometric_prompt_title_generic,
                        R.string.biometric_prompt_subtitle_generic,
                        R.string.biometric_prompt_negative_generic
                    )
                }
            }
            else -> arrayOf(
                R.string.biometric_prompt_title_generic,
                R.string.biometric_prompt_subtitle_generic,
                R.string.biometric_prompt_negative_generic
            )
        }.map { resId -> context.getString(resId) }

        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(texts[0])
            .setSubtitle(texts[1])
            .setNegativeButtonText(texts[2])
            .setDeviceCredentialAllowed(false)
            .build()
    }

    private fun getAuthFeatures(context: Context) = mutableListOf<String>().also { features ->
        with(context.packageManager) {
            fun addIfPresent(feature: String) {
                if (hasSystemFeature(feature)) {
                    features.add(feature)
                }
            }

            if (Build.VERSION.SDK_INT >= 23) {
                addIfPresent(PackageManager.FEATURE_FINGERPRINT)
                if (Build.VERSION.SDK_INT >= 29) {
                    addIfPresent(PackageManager.FEATURE_FACE)
                    addIfPresent(PackageManager.FEATURE_IRIS)
                }
            }
        }
    }

    private class AuthCallback(
        private val context: Context,
        private val onResult: OnAuthResult
    ) : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(
            errorCode: Int,
            errString: CharSequence
        ) {
            val text = if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                context.getString(R.string.biometric_prompt_error_negative)
            } else {
                errString
            }

            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            when (errorCode) {
                BiometricPrompt.ERROR_LOCKOUT -> onResult(Status.LOCKOUT, null)
                BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> onResult(Status.LOCKOUT_PERMANENT, null)
                else -> onResult(Status.FAILURE, null)
            }
        }

        override fun onAuthenticationSucceeded(
            result: BiometricPrompt.AuthenticationResult
        ) {
            onResult(Status.SUCCESS, result)
        }

        override fun onAuthenticationFailed() {
            Toast.makeText(
                context, context.getString(R.string.authentication_failed),
                Toast.LENGTH_SHORT
            ).show()
            onResult(Status.FAILURE, null)
        }
    }
}