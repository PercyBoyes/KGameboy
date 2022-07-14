package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toHexString

class POPAF(private val dest: Register16, private val stack: Stack,
            override val opCode: UByte) : Instruction {
    override val name = "POP ${dest.name}"
    override val description = "POP value off stack into 16 bit register ${dest.name}."
    override val length = 1
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        dest.set(stack.pop())
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${dest.name} = ${toHexString(dest.get())}"
    }
}