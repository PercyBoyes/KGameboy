package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.offset

class JRr8(private val pc: Register16, override val opCode: UByte) : Instruction {
    override val name = "JR r8"
    override val description = "Jump relative"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        offset(pc, operand[0].toByte())
        return cycles
    }
}