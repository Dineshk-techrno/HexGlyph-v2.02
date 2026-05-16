package com.hexglyph.a1.core.security

/**
 * Overwrites every byte with zero so that sensitive data does not linger in
 * the JVM heap after the reference is released.
 *
 * Call on: passwords (as byte form), decrypted payloads, intermediate key
 * material, and any other short-lived sensitive buffers.
 */
fun ByteArray.secureWipe() {
    fill(0)
}

/**
 * Runs [block] and guarantees [this] array is wiped even on exception.
 */
inline fun <R> ByteArray.useSecurely(block: (ByteArray) -> R): R =
    try {
        block(this)
    } finally {
        secureWipe()
    }
