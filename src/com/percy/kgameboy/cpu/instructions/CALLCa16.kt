package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.utils.toUShort

class CALLCa16(private val pc: Register16, private val stack: Stack,
               private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "CALL C d16"
    override val description = "Calls subroutine at address provided in operand if Carry flag is set"
    override val length = 3
    private val callCycles = 24
    private val continueCycles = 12

    override fun run(operand: UByteArray) : Int {
        return if (flags.isCarrySet()) {
            stack.push(pc.get())
            pc.set(toUShort(operand[1], operand[0]))
            callCycles
        } else
            continueCycles
    }
}