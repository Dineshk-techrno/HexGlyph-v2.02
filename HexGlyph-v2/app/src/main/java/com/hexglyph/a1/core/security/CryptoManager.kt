package com.hexglyph.a1.core.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles all symmetric encryption for HexGlyph.
 *
 * Algorithm: AES/GCM/NoPadding
 * Key derivation: PBKDF2WithHmacSHA256
 *   - Iterations : 120,000
 *   - Key length : 256-bit
 *   - Salt       : 16 bytes (random per operation)
 * IV             : 12 bytes (random per operation)
 * Auth tag       : 128-bit (GCM default)
 *
 * Output layout: [salt 16B][iv 12B][ciphertext + 16B GCM tag]
 */
@Singleton
class CryptoManager @Inject constructor() {

    companion object {
        private const val KEY_ALGORITHM    = "AES"
        private const val CIPHER_TRANSFORM = "AES/GCM/NoPadding"
        private const val KDF_ALGORITHM    = "PBKDF2WithHmacSHA256"

        private const val SALT_LENGTH_BYTES = 16
        private const val IV_LENGTH_BYTES   = 12
        private const val KEY_LENGTH_BITS   = 256
        private const val GCM_TAG_BITS      = 128
        private const val KDF_ITERATIONS    = 120_000
    }

    private val secureRandom = SecureRandom()

    /**
     * Encrypts [plaintext] with a key derived from [password].
     *
     * @return salt + iv + ciphertext (self-contained; no external state needed for decrypt)
     */
    fun encrypt(plaintext: ByteArray, password: CharArray): ByteArray {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { secureRandom.nextBytes(it) }
        val iv   = ByteArray(IV_LENGTH_BYTES).also   { secureRandom.nextBytes(it) }

        val key = deriveKey(password, salt)
        try {
            val cipher = Cipher.getInstance(CIPHER_TRANSFORM)
            cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
            val ciphertext = cipher.doFinal(plaintext)

            // Layout: [salt][iv][ciphertext+tag]
            return salt + iv + ciphertext
        } finally {
            key.encoded.secureWipe()
        }
    }

    /**
     * Decrypts data produced by [encrypt].
     *
     * @throws javax.crypto.AEADBadTagException if password is wrong or data is corrupted.
     */
    fun decrypt(data: ByteArray, password: CharArray): ByteArray {
        require(data.size > SALT_LENGTH_BYTES + IV_LENGTH_BYTES) {
            "Encrypted data too short to be valid."
        }

        val salt       = data.copyOfRange(0, SALT_LENGTH_BYTES)
        val iv         = data.copyOfRange(SALT_LENGTH_BYTES, SALT_LENGTH_BYTES + IV_LENGTH_BYTES)
        val ciphertext = data.copyOfRange(SALT_LENGTH_BYTES + IV_LENGTH_BYTES, data.size)

        val key = deriveKey(password, salt)
        try {
            val cipher = Cipher.getInstance(CIPHER_TRANSFORM)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_BITS, iv))
            return cipher.doFinal(ciphertext)
        } finally {
            key.encoded.secureWipe()
            salt.secureWipe()
            iv.secureWipe()
        }
    }

    // -------------------------------------------------------------------------

    private fun deriveKey(password: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(password, salt, KDF_ITERATIONS, KEY_LENGTH_BITS)
        val raw  = SecretKeyFactory
            .getInstance(KDF_ALGORITHM)
            .generateSecret(spec)
            .encoded
        spec.clearPassword()
        return SecretKeySpec(raw, KEY_ALGORITHM)
    }
}
