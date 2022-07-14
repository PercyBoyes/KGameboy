package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.CPU

class EI(val cpu: CPU) : Instruction {
    override val name = "EI"
    override val description = "Enable Interrupts"
    override val length = 1
    override val opCode = 0xf3u.toUByte()
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        cpu.enableInterrupts()
        return cycles
    }
}