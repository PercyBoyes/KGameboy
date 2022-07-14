package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack

class RETC(private val pc: Register16, private val stack: Stack,
           private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "RET C"
    override val description = "Returns from subroutine if carry flag set"
    override val length = 1
    private val returnCycles = 20
    private val continueCycles = 8

    override fun run(operand: UByteArray) : Int {
        return if (flags.isCarrySet()) {
            pc.set(stack.pop())
            returnCycles
        } else {
            continueCycles
        }
    }
}