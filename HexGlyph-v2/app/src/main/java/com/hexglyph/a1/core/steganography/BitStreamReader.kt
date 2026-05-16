package com.hexglyph.a1.core.steganography

/**
 * Reads individual bits from the LSB of the blue channel of an
 * ARGB_8888 pixel buffer, following the same [shuffledOrder] used
 * during encoding.
 */
class BitStreamReader(
    private val pixels: IntArray,
    private val shuffledOrder: IntArray
) {
    private var bitIndex = 0

    /**
     * Reads exactly [byteCount] bytes from the pixel buffer.
     *
     * @throws IllegalStateException if there are not enough bits remaining.
     */
    fun readBytes(byteCount: Int): ByteArray {
        val totalBits = byteCount * 8
        check(bitIndex + totalBits <= shuffledOrder.size) {
            "Read beyond buffer capacity."
        }

        val result = ByteArray(byteCount)
        for (byteIdx in 0 until byteCount) {
            var value = 0
            for (bitPos in 7 downTo 0) {
                val pixelIndex = shuffledOrder[bitIndex]
                val bit        = pixels[pixelIndex] and 1
                value = value or (bit shl bitPos)
                bitIndex++
            }
            result[byteIdx] = value.toByte()
        }
        return result
    }

    /** Returns how many bits have been read so far. */
    fun bitsRead(): Int = bitIndex
}
