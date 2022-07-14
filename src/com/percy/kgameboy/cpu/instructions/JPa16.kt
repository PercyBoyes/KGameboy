package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toHexString
import com.percy.kgameboy.utils.toUShort

class JPa16(private val pc: Register16, override val opCode: UByte) : Instruction {
    override val name = "JP a16"
    override val description = "Jump to address"
    override val length = 3
    private val jumpCycles = 16

    override fun run(operand: UByteArray): Int {
        pc.set(toUShort(operand[1], operand[0]))
        return jumpCycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${pc.name} = ${toHexString(pc.get())}, Operand = ${toHexString(toUShort(operand[1], operand[0]))}]"
    }
}