package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.offset

class JRZr8(private val pc: Register16, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "JR Z, r8"
    override val description = "Jump relative if zero flag set"
    override val length = 2
    private val jumpCycles = 12
    private val continueCycles = 8

    override fun run(operand: UByteArray): Int {
        return if (flags.isZeroSet()) {
            offset(pc, operand[0].toByte())
            jumpCycles
        } else {
            continueCycles
        }
    }
}