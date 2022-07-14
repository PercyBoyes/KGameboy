package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toUShort

class LDrrd16(private val dest: Register16, override val opCode: UByte) : Instruction {
    override val name = "LD ${dest.name}, d16"
    override val description = "Load 16 bit register with direct value"
    override val length = 3
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val value = toUShort(operand[1], operand[0])
//        println("Load d16: 0x${value.toString(16)}")
        dest.set(value)
        return cycles
    }
}