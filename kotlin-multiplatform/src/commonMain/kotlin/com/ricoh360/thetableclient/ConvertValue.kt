package com.ricoh360.thetableclient

fun ByteArray.toShort(): Short {
    var result: Int = 0
    for (i in 0 until count()) {
        result = result or (get(i).toUByte().toInt() shl 8 * i)
    }
    return result.toShort()
}

fun ByteArray.toInt(): Int {
    var result: Int = 0
    for (i in 0 until count()) {
        result = result or (get(i).toUByte().toInt() shl 8 * i)
    }
    return result
}

fun ByteArray.toLong(): Long {
    var result: Long = 0
    for (i in 0 until count()) {
        result = result or (get(i).toUByte().toLong() shl 8 * i)
    }
    return result
}

fun Byte.toBytes(): ByteArray {
    val result = ByteArray(1)
    result[0] = this
    return result
}

fun Short.toBytes(): ByteArray {
    var l = this.toInt()
    val result = ByteArray(2)
    for (i in 0..1) {
        result[i] = (l and 0xff).toByte()
        l = l shr 8
    }
    return result
}

fun Int.toBytes(): ByteArray {
    var l = this
    val result = ByteArray(4)
    for (i in 0..3) {
        result[i] = (l and 0xff).toByte()
        l = l shr 8
    }
    return result
}

fun Long.toBytes(): ByteArray {
    var l = this
    val result = ByteArray(8)
    for (i in 0..7) {
        result[i] = (l and 0xFF).toByte()
        l = l shr 8
    }
    return result
}
