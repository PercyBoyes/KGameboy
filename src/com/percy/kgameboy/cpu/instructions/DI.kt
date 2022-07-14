package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.CPU

class DI(val cpu: CPU) : Instruction {
    override val name = "DI"
    override val description = "Disable Interrupts"
    override val length = 1
    override val opCode = 0xfbu.toUByte()
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        cpu.disableInterrupts()
        return cycles
    }
}