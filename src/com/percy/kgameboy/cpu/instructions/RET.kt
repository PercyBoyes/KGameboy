package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.utils.toHexString

class RET(private val pc: Register16, private val stack: Stack,
          override val opCode: UByte) : Instruction {
    override val name = "RET"
    override val description = "Returns from subroutine"
    override val length = 1
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        pc.set(stack.pop())
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${pc.name} = ${toHexString(pc.get())}"
    }
}