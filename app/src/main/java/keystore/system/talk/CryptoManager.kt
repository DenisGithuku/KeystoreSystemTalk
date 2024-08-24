package keystore.system.talk

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec


class CryptoManager {

    // Get a reference to the keystore api
    private val keystore = KeyStore.getInstance(KeystoreType).apply {
        load(null)
    }

    // Generate key to encrypt and decrypt secret
    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    keystoreAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false)
                    .setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }

    // Fetch key and create new one if does not exist
    private fun getKey(): SecretKey {
        val existingKey = keystore.getEntry(keystoreAlias, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    // Create an encryption cipher (secret)
    private val encryptionCypher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, createKey())
    }

    // Create an initialization vector -> initial state of our decryption (randomized sequence of bytes)
    private fun getDecryptCipherForIv(iv: ByteArray): Cipher {
        return Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    companion object {
        private const val KeystoreType = "AndroidKeystore"
        private const val keystoreAlias = "secret"
        private val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    }

    fun encrypt(byteArray: ByteArray, outputStream: OutputStream): ByteArray {
        val encryptedBytes = encryptionCypher.doFinal(byteArray)

        /*
        Put the encrypted bytes in a stream so that when we need to decrypt our cipher
        we need the iv value
         */
        outputStream.use {
            it.write(encryptionCypher.iv.size)
            it.write(encryptionCypher.iv)
            it.write(encryptedBytes.size)
            it.write(encryptedBytes)
        }
        return encryptedBytes
    }

    fun decrypt(inputStream: InputStream): ByteArray {
        return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptedBytesSize = it.read()
            val encryptedBytes = ByteArray(encryptedBytesSize)
            it.read(encryptedBytes)

            getDecryptCipherForIv(iv).doFinal(encryptedBytes)
        }
    }
}