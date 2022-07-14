package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack

class RETNC(private val pc: Register16, private val stack: Stack,
            private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "RET NC"
    override val description = "Returns from subroutine if carry flag not set"
    override val length = 1
    private val returnCycles = 20
    private val continueCycles = 8

    override fun run(operand: UByteArray) : Int {
        return if (!flags.isCarrySet()) {
            pc.set(stack.pop())
            returnCycles
        } else {
            continueCycles
        }
    }
}