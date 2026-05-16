package com.hexglyph.a1.core.steganography

import java.util.zip.CRC32
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Validates CRC32 checksums on extracted payloads to detect corruption.
 */
@Singleton
class Crc32Verifier @Inject constructor() {

    /**
     * Returns true if the CRC32 of [data] matches [expectedCrc32].
     */
    fun verify(data: ByteArray, expectedCrc32: Long): Boolean {
        val crc = CRC32()
        crc.update(data)
        return crc.value == expectedCrc32
    }

    /**
     * Computes and returns the CRC32 of [data] as an unsigned Long.
     */
    fun compute(data: ByteArray): Long {
        val crc = CRC32()
        crc.update(data)
        return crc.value
    }
}
