package com.hexglyph.a1.core.steganography

import java.nio.ByteBuffer
import java.util.zip.CRC32

/**
 * Binary header prepended to every embedded payload.
 *
 * Layout (16 bytes total):
 *
 *  Offset  Size  Field
 *  ------  ----  -----
 *    0       4   MAGIC        "HGA1" as UTF-8 bytes
 *    4       1   VERSION      Protocol version (current = 1)
 *    5       1   FLAGS        Bit 0 = encrypted; Bit 1 = compressed (reserved)
 *    6       4   PAYLOAD_LEN  Unencrypted payload length in bytes (big-endian)
 *   10       4   CRC32        CRC32 of the raw payload bytes
 *   14       2   RESERVED     Zero-padded; reserved for future use
 *
 * Total header size: HEADER_SIZE = 16 bytes
 */
data class PayloadHeader(
    val version:    Byte,
    val flags:      Byte,
    val payloadLen: Int,
    val crc32:      Long
) {
    companion object {
        val MAGIC       = byteArrayOf(0x48, 0x47, 0x41, 0x31) // "HGA1"
        const val HEADER_SIZE   = 16
        const val VERSION_CURRENT: Byte = 1
        const val FLAG_ENCRYPTED: Byte  = 0x01
        const val FLAG_RESERVED : Byte  = 0x02

        /**
         * Serialises a [PayloadHeader] to [HEADER_SIZE] bytes.
         */
        fun encode(header: PayloadHeader): ByteArray =
            ByteBuffer.allocate(HEADER_SIZE).apply {
                put(MAGIC)
                put(header.version)
                put(header.flags)
                putInt(header.payloadLen)
                putInt(header.crc32.toInt())
                putShort(0)                   // reserved
            }.array()

        /**
         * Deserialises [HEADER_SIZE] bytes back to [PayloadHeader].
         *
         * @throws IllegalArgumentException if magic bytes don't match.
         */
        fun decode(bytes: ByteArray): PayloadHeader {
            require(bytes.size >= HEADER_SIZE) { "Buffer too small for header." }
            val buf = ByteBuffer.wrap(bytes)

            val magic = ByteArray(4).also { buf.get(it) }
            require(magic.contentEquals(MAGIC)) {
                "Invalid magic bytes — not a HexGlyph payload."
            }

            val version    = buf.get()
            val flags      = buf.get()
            val payloadLen = buf.int
            val crc32      = buf.int.toLong() and 0xFFFFFFFFL
            // skip 2 reserved bytes

            return PayloadHeader(version, flags, payloadLen, crc32)
        }

        /**
         * Computes CRC32 of [data] and returns it as an unsigned Long.
         */
        fun computeCrc32(data: ByteArray): Long {
            val crc = CRC32()
            crc.update(data)
            return crc.value
        }

        /**
         * Convenience: build a header for [payload], marking it as encrypted if [encrypted].
         */
        fun forPayload(payload: ByteArray, encrypted: Boolean = false): PayloadHeader =
            PayloadHeader(
                version    = VERSION_CURRENT,
                flags      = if (encrypted) FLAG_ENCRYPTED else 0,
                payloadLen = payload.size,
                crc32      = computeCrc32(payload)
            )
    }
}
