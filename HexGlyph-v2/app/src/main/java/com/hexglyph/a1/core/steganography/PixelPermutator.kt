package com.hexglyph.a1.core.steganography

import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates a deterministic, password-derived pixel embedding order using
 * an in-place Fisher-Yates shuffle seeded from a SHA-256 digest of the
 * PBKDF2 output supplied by [CryptoManager].
 *
 * Design constraints:
 *  - IntArray only; no List allocations.
 *  - SecureRandom (SHA1PRNG) deterministically seeded; no kotlin.random.Random.
 *  - Single allocation per call.
 *  - Safe for bitmaps up to Integer.MAX_VALUE pixels.
 *  - Thread-safe: no shared mutable state.
 */
@Singleton
class PixelPermutator @Inject constructor() {

    /**
     * Returns a shuffled array of pixel indices [0, totalPixels).
     *
     * The shuffle is fully deterministic for a given (totalPixels, seed) pair:
     * encode and decode will always produce the identical traversal order when
     * the same password-derived seed is supplied.
     *
     * @param totalPixels  bitmap.width * bitmap.height
     * @param seed         Password-derived seed bytes (PBKDF2 → SHA-256 output).
     */
    fun generateShuffledOrder(totalPixels: Int, seed: ByteArray): IntArray {
        require(totalPixels > 0) { "totalPixels must be positive" }
        require(seed.isNotEmpty()) { "seed must not be empty" }

        // Normalise to 32 bytes via SHA-256 for consistent RNG seeding length.
        val digest = MessageDigest.getInstance("SHA-256").digest(seed)

        // SHA1PRNG is deterministic when seeded on Android's ART runtime.
        @Suppress("InsecureCryptoAlgorithm")
        val rng = SecureRandom.getInstance("SHA1PRNG").apply { setSeed(digest) }

        // Identity permutation [0, 1, 2, …, totalPixels-1]
        val order = IntArray(totalPixels) { it }

        // In-place Fisher-Yates (Knuth) shuffle
        for (i in totalPixels - 1 downTo 1) {
            val j = rng.nextInt(i + 1)
            if (i != j) {
                order[i] = order[i] xor order[j]
                order[j] = order[i] xor order[j]
                order[i] = order[i] xor order[j]
            }
        }

        return order
    }

    companion object {
        /** Lightweight seed derivation shim — replace body with PBKDF2 output when ready. */
        fun deriveSeed(password: String): ByteArray =
            MessageDigest
                .getInstance("SHA-256")
                .digest(password.toByteArray(Charsets.UTF_8))
    }
}
