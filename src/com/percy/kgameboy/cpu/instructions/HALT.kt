package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.CPU

class HALT(private val cpu: CPU) : Instruction {
    override val name = "HALT"
    override val description = "HALT execution on the CPU"
    override val length = 1
    override val opCode: UByte = 0x76u

    override fun run(operand: UByteArray): Int {
            cpu.halt()
        return 4
    }

}