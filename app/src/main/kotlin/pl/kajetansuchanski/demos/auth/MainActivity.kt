package pl.kajetansuchanski.demos.auth

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_main.*

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
            if (Build.VERSION.SDK_INT >= 23) {
                isDeviceSecure
            } else {
                isKeyguardSecure
            }
        }

        if (!isSecure) {
            Toast.makeText(this, R.string.secured_device_required, Toast.LENGTH_LONG).show()
            return
        }

        DeviceAuth.prompt(keyguardManager, credentialsFragment) { authorized ->
            if (authorized) {
                BiometricAuth.prompt(credentialsFragment) { status, result ->

                }
            }
        }
    }
}