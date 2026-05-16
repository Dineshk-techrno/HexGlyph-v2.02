package com.hexglyph.a1.core.steganography

/**
 * Writes individual bits into the LSB of the blue channel of an
 * ARGB_8888 pixel buffer, following a pre-computed [shuffledOrder].
 *
 * Usage:
 *   val writer = BitStreamWriter(pixels, shuffledOrder)
 *   writer.writeBytes(header)
 *   writer.writeBytes(payload)
 */
class BitStreamWriter(
    private val pixels: IntArray,
    private val shuffledOrder: IntArray
) {
    private var bitIndex = 0

    /** Total embeddable bits available in this buffer. */
    val capacity: Int get() = shuffledOrder.size

    /**
     * Embeds all bytes from [data] into the pixel buffer.
     *
     * @throws IllegalStateException if the buffer does not have enough capacity.
     */
    fun writeBytes(data: ByteArray) {
        val totalBits = data.size * 8
        check(bitIndex + totalBits <= capacity) {
            "Payload too large: needs ${bitIndex + totalBits} bits, capacity $capacity."
        }

        for (byteIdx in data.indices) {
            val byte = data[byteIdx].toInt()
            for (bitPos in 7 downTo 0) {
                val bit        = (byte ushr bitPos) and 1
                val pixelIndex = shuffledOrder[bitIndex]
                val color      = pixels[pixelIndex]

                pixels[pixelIndex] = (color and 0xFFFFFF00.toInt()) or
                        ((color and 0xFF and 0xFE) or bit)
                bitIndex++
            }
        }
    }

    /** Returns how many bits have been written so far. */
    fun bitsWritten(): Int = bitIndex
}
