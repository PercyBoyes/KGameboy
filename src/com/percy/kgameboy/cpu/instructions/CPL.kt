package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class CPL(private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "CPL"
    override val description = "Take compliment value in register A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        src.set(src.getUnsigned().inv())
        flags.setNegative(true)
        flags.setHalfCarry(true)
        return cycles
    }
}