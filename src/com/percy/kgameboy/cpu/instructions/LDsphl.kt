package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16


class LDsphl(private val sp: Register16, private val hl: Register16, override val opCode: UByte) : Instruction {
    override val name = "LD SP, HL"
    override val description = "Load the value of HL into the SP"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        sp.set(hl.get())
        return cycles
    }
}