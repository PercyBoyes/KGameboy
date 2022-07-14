package com.percy.kgameboy.common

import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.toHexString

class Register8 (val name: String, private val writeMask: UByte = 0xffu, private val log: Boolean = false) {
    private var value: UByte = 0x00u

    fun set(value: UByte) {
        if (log) println("$name: Register Changed from ${toHexString(this.value)} to ${toHexString(value)}")
        this.value = value and writeMask
    }

    fun getUnsigned() = value

    fun getSigned() = value.toByte()

    fun isZero() = value == 0x00u.toUByte()

    fun isBitSet(bit: Int) = isSet(this.value, bit)
}