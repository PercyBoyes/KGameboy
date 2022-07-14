package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16

class JPhl(private val pc: Register16, private val hl: Register16, override val opCode: UByte) : Instruction {
    override val name = "JP hl"
    override val description = "Jump to address in hl register"
    override val length = 1
    private val jumpCycles = 4

    override fun run(operand: UByteArray): Int {
        pc.set(hl.get())
        return jumpCycles
    }
}