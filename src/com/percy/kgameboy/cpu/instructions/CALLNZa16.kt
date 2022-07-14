package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.utils.toUShort

class CALLNZa16(private val pc: Register16, private val stack: Stack,
                private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "CALL NZ d16"
    override val description = "Calls subroutine at address provided in operand if Zero flag is not set"
    override val length = 3
    private val callCycles = 24
    private val continueCycles = 12

    override fun run(operand: UByteArray) : Int {
        return if (!flags.isZeroSet()) {
            stack.push(pc.get())
            pc.set(toUShort(operand[1], operand[0]))
            callCycles
        } else
            continueCycles
    }
}