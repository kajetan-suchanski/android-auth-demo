package pl.kajetansuchanski.demos.auth

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kwezal.kandy.logs.logE
import kotlinx.android.synthetic.main.fragment_secret.*
import pl.kajetansuchanski.demos.auth.biometric.BiometricAuth
import pl.kajetansuchanski.demos.auth.crypto.decodeBase64
import pl.kajetansuchanski.demos.auth.crypto.encodeBase64
import java.io.File
import javax.crypto.Cipher

private typealias OnReadSecretResult = (success: Boolean, result: String) -> Unit

private const val SECRET_FILE_NAME = "secret.enc"

class SecretFragment : Fragment(R.layout.fragment_secret) {
    var biometricDecryptor: Cipher? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edit_secret.isEnabled = false
        readSecret { success, result ->
            if (success) {
                edit_secret.setText(result)
            }
            edit_secret.isEnabled = true
        }

        button_encrypt.setOnClickListener {
            trySave(edit_secret.text.toString())
        }
    }

    private fun readSecret(onResult: OnReadSecretResult) {
        try {
            val secretFile = getSecretFile()
            if (!secretFile.exists()) {
                onResult(true, "")
            } else {
                val bytes = secretFile.readBytes()
                if (bytes.isEmpty()) {
                    onResult(true, "")
                    return
                }

                val cryptogram = bytes.decodeBase64()
                val biometricCipher = biometricDecryptor
                    ?: throw IllegalStateException("No cipher passed to fragment")
                val decrypted = decrypt(cryptogram, biometricCipher)
                onResult(true, String(decrypted))
            }
        } catch (e: Exception) {
            logE(tr = { e }) { "" }
            onResult(false, "")
        }
    }

    private inline fun decrypt(cryptogram: ByteArray, biometricCipher: Cipher) =
        biometricCipher.doFinal(cryptogram)

    private fun trySave(plaintext: String) {
        val ctx = requireContext()
        if (plaintext.isEmpty()) {
            Toast.makeText(
                ctx,
                R.string.secret_message_cannot_be_empty,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        BiometricAuth.prompt(this, Cipher.ENCRYPT_MODE) { status, result ->
            when (status) {
                BiometricAuth.Status.SUCCESS -> {
                    val result = result
                        ?: throw IllegalStateException("Biometric authentication result is null")
                    val cryptoObj = result.cryptoObject
                        ?: throw IllegalStateException("Biometric cryptographic object is null")
                    val cipher = cryptoObj.cipher
                        ?: throw IllegalStateException("Biometric cipher is null")

                    val success = encryptAndSave(getSecretFile(), plaintext, cipher)
                    val message =
                        if (success) {
                            R.string.encryption_success
                        } else {
                            R.string.encryption_failure
                        }
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // General error handling
                    logE { "Biometric auth failed: status=$status" }
                    Toast.makeText(
                        ctx,
                        R.string.authentication_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun encryptAndSave(file: File, plaintext: String, cipher: Cipher) =
        try {
            file.apply {
                val encrypted = cipher.doFinal(plaintext.toByteArray())
                writeBytes(encrypted.encodeBase64())
            }

            true
        } catch (e: Exception) {
            false
        }

    private inline fun getSecretFile() = File(requireContext().filesDir, SECRET_FILE_NAME)

    companion object {
        fun withCiphers(biometricCipher: Cipher) = SecretFragment().apply {
            this.biometricDecryptor = biometricCipher
        }
    }
}