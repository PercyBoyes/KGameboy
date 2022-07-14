package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet

class BITr(private val src: Register8, private val bit: Int, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "BIT $bit, ${src.name}"
    override val description = "Copy the compliment of the bit b in register r to the zero flag"
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val isSet = isSet(src.getUnsigned(), bit)
        flags.setZero(!isSet)
        flags.setHalfCarry(true)
        flags.setNegative(false)
        return cycles
    }
}