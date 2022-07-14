package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString

class LDrr(private val dest: Register8, private val src: Register8, override val opCode: UByte) : Instruction {
    override val name = "LD ${dest.name}, ${src.name}"
    override val description = "Copy value in 8 bit register to another 8 bit register"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        dest.set(src.getUnsigned())
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${dest.name} = ${toHexString(dest.getUnsigned())}, ${src.name} = ${toHexString(src.getUnsigned())}]"
    }
}