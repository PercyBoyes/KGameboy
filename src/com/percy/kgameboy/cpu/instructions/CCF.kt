package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags

class CCF(private val flags: Flags) : Instruction {
    override val name = "CCF"
    override val description = "Compliment the carry flag"
    override val length = 1
    private val cycles = 4
    override val opCode = 0x3fu.toUByte()

    override fun run(operand: UByteArray) : Int {
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(!flags.isCarrySet())
        return cycles
    }
}