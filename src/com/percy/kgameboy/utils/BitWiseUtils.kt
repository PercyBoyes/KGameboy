package com.percy.kgameboy.utils

import kotlin.math.pow

fun UShort.topByte() = (this.toInt() shr 8).toUByte()

fun UShort.bottomByte() = (this.toInt() and 0x000000ff).toUByte()

fun toUShort(top: UByte, bottom: UByte) = ((top.toUInt() shl 8) or bottom.toUInt()).toUShort()

fun setBit(src: UByte, bit: Int, enable: Boolean) : UByte {
    val bitMask = 2f.pow(bit).toUInt()
    return if (enable)
        src or bitMask.toUByte()
    else
        src and bitMask.inv().toUByte()
}

fun toHexString(b: UByte) : String {
    val hexString = b.toString(16)

    return when (hexString.length) {
        1 -> "0x0$hexString"
        2 -> "0x$hexString"
        else -> "Error"
    }
}

fun toHexString(s: UShort) : String {
    val hexString = s.toString(16)

    return when (hexString.length) {
        1 -> "0x000$hexString"
        2 -> "0x00$hexString"
        3 -> "0x0$hexString"
        4 -> "0x$hexString"
        else -> "Error"
    }
}

fun swapNibbles(value: UByte) : UByte {
    val high = (value and 0xf0u).toInt() shr 4
    val low = (value and 0x0fu).toInt() shl 4
    return (high or low).toUByte()
}

fun isSet(src: UByte, bit: Int): Boolean {
    val bitMask = 2f.pow(bit).toUInt()
    val result = src and bitMask.toUByte()
    return result > 0x00u
}

fun getBitValue(src: UByte, bit: Int) = if (isSet(src, bit)) 1 else 0