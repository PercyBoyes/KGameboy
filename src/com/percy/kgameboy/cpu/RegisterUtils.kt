package com.percy.kgameboy.cpu

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

fun increment(src: Register8) = src.set(src.getUnsigned().inc())
fun decrement(src: Register8) = src.set(src.getUnsigned().dec())
fun increment(src: Register16) = src.set((src.get() + 0x1u).toUShort())
fun decrement(src: Register16) = src.set((src.get() - 0x1u).toUShort())
fun shiftLeft(reg: Register8) = reg.set((reg.getUnsigned().toUInt() shl 1).toUByte())
fun shiftRight(reg: Register8) = reg.set((reg.getUnsigned().toUInt() shr 1).toUByte())

fun offset(src: Register16, offset: Byte) {
    val result = (src.get().toShort() + offset).toUShort()
    src.set(result)
}

fun willAddCauseHalfCarry(val0: UByte, val1: UByte) : Boolean {
    var hTest = (val0.toUInt() and 0xfu)
    hTest += (val1.toUInt() and 0xfu)
    return hTest > 0xfu
}

fun willSubCauseHalfCarry(val0: UByte, val1: UByte) : Boolean {
    var hTest = (val0.toInt() and 0xf)
    hTest -= (val1.toInt() and 0xf)
    return (hTest < 0)
}
