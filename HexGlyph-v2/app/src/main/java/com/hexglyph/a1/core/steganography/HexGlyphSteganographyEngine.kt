package com.hexglyph.a1.core.steganography

import android.graphics.Bitmap
import com.hexglyph.a1.core.security.CryptoManager
import com.hexglyph.a1.core.security.secureWipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Steganography sub-components — all in same package, explicit for clarity
// BitStreamWriter, BitStreamReader, PixelPermutator, PayloadHeader, Crc32Verifier
// are in com.hexglyph.a1.core.steganography — no import needed (same package)

/**
 * HexGlyph steganography engine — production-grade LSB embedding.
 *
 * Encode pipeline:
 *   plaintext
 *   → AES/GCM encrypt (CryptoManager)
 *   → PayloadHeader (magic + version + flags + length + CRC32)
 *   → Fisher-Yates permuted pixel order (PixelPermutator)
 *   → BitStreamWriter embeds [header || ciphertext] into blue-channel LSBs
 *   → setPixels() writes back
 *
 * Decode pipeline (exact reverse):
 *   getPixels()
 *   → same permuted order
 *   → BitStreamReader extracts header bytes
 *   → validate magic + CRC32 (Crc32Verifier)
 *   → extract ciphertext bytes
 *   → AES/GCM decrypt
 *   → plaintext
 *
 * Invariants:
 *  - IntArray pixel buffer via getPixels / setPixels only
 *  - Bitmap.Config.ARGB_8888
 *  - No getPixel / setPixel loops
 *  - No external steganography libraries
 *  - Coroutines on Dispatchers.Default
 *  - Sensitive buffers wiped after use
 */
@Singleton
class HexGlyphSteganographyEngine @Inject constructor(
    private val cryptoManager:  CryptoManager,
    private val pixelPermutator: PixelPermutator,
    private val crc32Verifier:   Crc32Verifier
) {

    /**
     * Embeds [plaintext] into a copy of [bitmap], encrypted with [password].
     *
     * @return A new mutable ARGB_8888 bitmap with the payload hidden inside.
     * @throws IllegalStateException if the image is too small for the payload.
     */
    suspend fun encode(
        bitmap:    Bitmap,
        plaintext: ByteArray,
        password:  CharArray
    ): Bitmap = withContext(Dispatchers.Default) {

        var ciphertext: ByteArray? = null
        try {
            // 1. Encrypt
            ciphertext = cryptoManager.encrypt(plaintext, password)

            // 2. Build header
            val header        = PayloadHeader.forPayload(ciphertext, encrypted = true)
            val headerBytes   = PayloadHeader.encode(header)

            val totalBytes    = headerBytes.size + ciphertext.size
            val totalBits     = totalBytes * 8

            // 3. Prepare pixel buffer
            val mutable      = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val totalPixels  = mutable.width * mutable.height

            check(totalBits <= totalPixels) {
                "Image too small: needs $totalPixels px, have ${totalBits} bits ($totalBytes bytes)."
            }

            val pixels = IntArray(totalPixels)
            mutable.getPixels(pixels, 0, mutable.width, 0, 0, mutable.width, mutable.height)

            // 4. Permuted embedding order
            val seed         = PixelPermutator.deriveSeed(String(password))
            val shuffledOrder = pixelPermutator.generateShuffledOrder(totalPixels, seed)
            seed.secureWipe()

            // 5. Write header then ciphertext
            val writer = BitStreamWriter(pixels, shuffledOrder)
            writer.writeBytes(headerBytes)
            writer.writeBytes(ciphertext)

            // 6. Flush back
            mutable.setPixels(pixels, 0, mutable.width, 0, 0, mutable.width, mutable.height)
            mutable

        } finally {
            ciphertext?.secureWipe()
        }
    }

    /**
     * Extracts and decrypts the payload hidden in [bitmap].
     *
     * @return Decrypted plaintext bytes.
     * @throws IllegalArgumentException if magic bytes are invalid.
     * @throws javax.crypto.AEADBadTagException if password is wrong or data corrupted.
     */
    suspend fun decode(
        bitmap:   Bitmap,
        password: CharArray
    ): ByteArray = withContext(Dispatchers.Default) {

        var ciphertext: ByteArray? = null
        var plaintext:  ByteArray? = null

        try {
            val totalPixels = bitmap.width * bitmap.height
            val pixels = IntArray(totalPixels)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            // 1. Regenerate identical permuted order
            val seed          = PixelPermutator.deriveSeed(String(password))
            val shuffledOrder = pixelPermutator.generateShuffledOrder(totalPixels, seed)
            seed.secureWipe()

            val reader = BitStreamReader(pixels, shuffledOrder)

            // 2. Read and validate header
            val headerBytes = reader.readBytes(PayloadHeader.HEADER_SIZE)
            val header      = PayloadHeader.decode(headerBytes)

            // 3. Extract ciphertext
            ciphertext = reader.readBytes(header.payloadLen)

            // 4. Verify CRC32
            check(crc32Verifier.verify(ciphertext, header.crc32)) {
                "CRC32 mismatch — image may be corrupted or modified."
            }

            // 5. Decrypt
            plaintext = cryptoManager.decrypt(ciphertext, password)
            plaintext

        } finally {
            ciphertext?.secureWipe()
            // plaintext is returned — caller is responsible for wiping after use
        }
    }

    /**
     * Returns the maximum payload size (in bytes) that fits inside [bitmap]
     * after accounting for header overhead.
     */
    fun maxPayloadBytes(bitmap: Bitmap): Int {
        val totalPixels = bitmap.width * bitmap.height
        return (totalPixels / 8) - PayloadHeader.HEADER_SIZE
    }
}
