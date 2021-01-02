package pl.kajetansuchanski.demos.auth

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val fragment = Fragment()
        supportFragmentManager.beginTransaction().add(fragment, null).commitNow()

        BiometricAuth.prompt(fragment) { status, result ->

        }
    }
}