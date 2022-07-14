package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register8

class LDrd8(private val dest: Register8, override val opCode: UByte) : Instruction {
    override val name = "LD ${dest.name}, d8"
    override val description = "Load 8 bit register with direct value"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val value = operand[0]
 //       println("Load d8: 0x${value.toString(16)}")
        dest.set(value)
        return cycles
    }
}