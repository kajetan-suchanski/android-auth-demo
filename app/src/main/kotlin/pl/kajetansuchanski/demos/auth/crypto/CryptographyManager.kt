package pl.kajetansuchanski.demos.auth.crypto

import android.annotation.SuppressLint
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val CIPHER_ALGORITHM = "AES"
private const val CIPHER_MODE = "GCM"
private const val CIPHER_PADDING = "NoPadding"
private const val CIPHER_TRANSFORMATION = "$CIPHER_ALGORITHM/$CIPHER_MODE/$CIPHER_PADDING"

private const val KEY_LENGTH = 256
private const val KEY_PROVIDER = "AndroidKeyStore"

private const val IV_LENGTH_BYTES = 16
private const val IV_LENGTH_BITS = IV_LENGTH_BYTES * 8

class CryptographyManager(private val context: Context, private val keyAlias: String) {
    /**
     * @param operationMode [Cipher.ENCRYPT_MODE] or [Cipher.DECRYPT_MODE]
     */
    fun getCipher(operationMode: Int): Cipher =
        Cipher.getInstance(CIPHER_TRANSFORMATION).apply {
            val isEnc = operationMode == Cipher.ENCRYPT_MODE
            init(
                operationMode,
                getSecretKey(),
                if (isEnc) null else getInitializationVector()
            )

            if (isEnc) {
                saveInitializationVector(getIvFile(), iv)
            }
        }

    private fun getSecretKey(): SecretKey = KeyStore.getInstance(KEY_PROVIDER).run {
        load(null)
        return@run getKey(keyAlias, null) as? SecretKey ?: generateKey()
    }

    private inline fun generateKey() = getKeyGenerator().generateKey()

    private inline fun getKeyGenerator() =
        KeyGenerator.getInstance(CIPHER_ALGORITHM, KEY_PROVIDER).apply {
            init(getKeyGenSpec())
        }

    @SuppressLint("WrongConstant")
    private inline fun getKeyGenSpec() = KeyGenParameterSpec.Builder(
        keyAlias,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(CIPHER_MODE)
        .setEncryptionPaddings(CIPHER_PADDING)
        .setKeySize(KEY_LENGTH)
        .setUserAuthenticationRequired(true)
        .build()

    private fun getInitializationVector(): AlgorithmParameterSpec {
        val ivFile = getIvFile()
        val iv = if (ivFile.exists()) {
            ivFile.readBytes()
        } else {
            generateInitializationVector().also { iv ->
                saveInitializationVector(ivFile, iv)
            }
        }

        return GCMParameterSpec(IV_LENGTH_BITS, iv)
    }

    private fun generateInitializationVector(): ByteArray {
        val iv = ByteArray(IV_LENGTH_BYTES)
        SecureRandom().nextBytes(iv)
        return iv
    }

    private inline fun saveInitializationVector(file: File, iv: ByteArray) {
        file.writeBytes(iv)
    }

    private inline fun getIvFile() = File(context.filesDir, "$keyAlias.iv")
}