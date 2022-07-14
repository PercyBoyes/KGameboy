package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags

class SCF(private val flags: Flags) : Instruction {
    override val name = "SCF"
    override val description = "Set the carry flag"
    override val length = 1
    private val cycles = 4
    override val opCode = 0x37u.toUByte()

    override fun run(operand: UByteArray) : Int {
        flags.setCarry(true)
        flags.setNegative(false)
        flags.setHalfCarry(false)
        return cycles
    }
}