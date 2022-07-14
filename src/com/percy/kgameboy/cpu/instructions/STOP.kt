package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.CPU

class STOP(private val cpu: CPU) : Instruction {
    override val name = "STOP"
    override val description = "STOP execution on the CPU"
    override val length = 2
    override val opCode: UByte = 0x10u

    override fun run(operand: UByteArray): Int {
        cpu.halt()
        return 0
    }
}