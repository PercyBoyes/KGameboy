package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.increment
import com.percy.kgameboy.utils.toHexString

class INCrr(private val src: Register16, override val opCode: UByte) : Instruction {
    override val name = "INC ${src.name}"
    override val description = "Increment ${src.name}"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        increment(src)
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${src.name} = ${toHexString(src.get())}"
    }
}