package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toUShort

class JPCa16(private val pc: Register16, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "JP C, a16"
    override val description = "Jump to address if carry flag is set"
    override val length = 3
    private val jumpCycles = 16
    private val continueCycles = 12

    override fun run(operand: UByteArray): Int {
        return if(flags.isCarrySet()) {
            pc.set(toUShort(operand[1], operand[0]))
            jumpCycles
        } else {
            continueCycles
        }
    }
}