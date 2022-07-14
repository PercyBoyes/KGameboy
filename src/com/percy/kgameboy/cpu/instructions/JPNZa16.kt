package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toUShort

class JPNZa16(private val pc: Register16, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "JP NZ, a16"
    override val description = "Jump to address if zero flag is not set"
    override val length = 3
    private val jumpCycles = 16
    private val continueCycles = 12

    override fun run(operand: UByteArray): Int {
        return if(!flags.isZeroSet()) {
            pc.set(toUShort(operand[1], operand[0]))
            jumpCycles
        } else
            continueCycles
    }
}