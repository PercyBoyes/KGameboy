package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.utils.toUShort

class CALLa16(private val pc: Register16, private val stack: Stack, override val opCode: UByte) : Instruction {
    override val name = "CALL d16"
    override val description = "Calls subroutine at address provided in operand"
    override val length = 3
    private val cycles = 24

    override fun run(operand: UByteArray) : Int {
        stack.push(pc.get())
        pc.set(toUShort(operand[1], operand[0]))
        return cycles
    }
}