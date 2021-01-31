package pl.kajetansuchanski.demos.auth

import android.app.KeyguardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.kwezal.kandy.logs.logE
import kotlinx.android.synthetic.main.activity_main.*
import pl.kajetansuchanski.demos.auth.biometric.BiometricAuth
import pl.kajetansuchanski.demos.auth.device.DeviceAuth
import javax.crypto.Cipher

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private lateinit var credentialsFragment: CredentialsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        credentialsFragment = CredentialsFragment()
        supportFragmentManager.beginTransaction().add(credentialsFragment, null).commit()

        button_authenticate.setOnClickListener {
            tryAuthenticate()
        }
    }

    private fun tryAuthenticate() {
        val keyguardManager = getSystemService<KeyguardManager>()!!
        val isSecure = with(keyguardManager) {
            isDeviceSecure
        }

        if (!isSecure) {
            Toast.makeText(this, R.string.secured_device_required, Toast.LENGTH_LONG).show()
            return
        }

        DeviceAuth.prompt(keyguardManager, credentialsFragment) { authorized ->
            if (authorized) {
                BiometricAuth.prompt(credentialsFragment, Cipher.DECRYPT_MODE) { status, result ->
                    when (status) {
                        BiometricAuth.Status.SUCCESS -> {

                        }
                        else -> {
                            // General error handling
                            logE { "Biometric auth failed: status=$status" }
                        }
                    }
                }
            } else {
                logE { "Device auth failed" }
                Toast.makeText(this, R.string.authentication_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }
}