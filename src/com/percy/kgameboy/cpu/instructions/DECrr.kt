package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.decrement
import com.percy.kgameboy.utils.toHexString

class DECrr(private val src: Register16, override val opCode: UByte) : Instruction {
    override val name = "DEC ${src.name}"
    override val description = "Decrement ${src.name}"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        decrement(src)
        //println(               "B register: ${toHexString(src.get())}")
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${src.name} = ${toHexString(src.get())}"
    }
}