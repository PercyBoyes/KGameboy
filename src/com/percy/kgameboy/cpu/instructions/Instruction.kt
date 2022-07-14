package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.cpu.willAddCauseHalfCarry
import com.percy.kgameboy.cpu.willSubCauseHalfCarry

enum class PostOperation {
    POST_INCREMENT_REGISTER,
    POST_DECREMENT_REGISTER,
    NO_POST_MODIFICATION
}

interface Instruction {
    val name: String
    val description: String
    val length: Int
    val opCode: UByte

    fun run(operand: UByteArray) : Int

    fun getState(operand: UByteArray) = ""
    fun test() {}
}

fun isZero(val0: UByte) = val0 == 0x00u.toUByte()

fun or(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val result = val0 or val1
    flags.setZero(isZero(result))
    flags.setNegative(false)
    flags.setHalfCarry(false)
    flags.setCarry(false)
    return result
}

fun xor(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val result = val0 xor val1
    flags.setZero(isZero(result))
    flags.setNegative(false)
    flags.setHalfCarry(false)
    flags.setCarry(false)
    return result
}

fun sub(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val result = val0 - val1
    flags.setZero(isZero(result.toUByte()))
    flags.setNegative(true)
    flags.setHalfCarry(willSubCauseHalfCarry(val0, val1))
    flags.setCarry(val0 < val1)
    return result.toUByte()
}

fun sbc(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val regA = val0.toInt()
    val value = val1.toInt()
    val carry = if (flags.isCarrySet()) 1 else 0
    val result = regA - value - carry

    flags.setZero(result and 0xff == 0)
    flags.setNegative(true)
    flags.setHalfCarry((regA and 0xF) - (value and 0xF) - carry and 0x10 != 0)
    flags.setCarry((regA and 0xFF) - (value and 0xFF) - carry and 0x100 != 0)

    return result.toUByte()
}

fun add(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val result = val0 + val1
    flags.setZero(isZero(result.toUByte()))
    flags.setNegative(false)
    flags.setCarry(result > 0xffu)
    flags.setHalfCarry(willAddCauseHalfCarry(val0, val1))
    return result.toUByte()
}

fun and(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val result = val0 and val1
    flags.setZero(isZero(result))
    flags.setNegative(false)
    flags.setHalfCarry(true)
    flags.setCarry(false)
    return result
}

fun adc(val0: UByte, val1: UByte, flags: Flags) : UByte {
    val regA = val0.toInt()
    val value = val1.toInt()
    val carry = if (flags.isCarrySet()) 1 else 0
    val result = regA + value + carry

    flags.setZero(result and 0xff == 0)
    flags.setNegative(false)
    flags.setHalfCarry((regA and 0xF) + (value and 0xF) + carry and 0x10 != 0)
    flags.setCarry((regA and 0xFF) + (value and 0xFF) + carry and 0x100 != 0)

    return result.toUByte()
}



