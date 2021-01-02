package pl.kajetansuchanski.demos.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = Fragment()
        supportFragmentManager.beginTransaction().add(fragment, null).commit()

        button_authenticate.setOnClickListener {
            BiometricAuth.prompt(fragment) { status, result ->

            }
        }
    }
}