package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16


class ADDhlr16(val hl: Register16, val src: Register16, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "ADD HL, ${src.name}"
    override val description = "Add value in register ${src.name} to HL and store result in HL"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val regHL = hl.get().toInt()
        val value = src.get().toInt()
        val result = regHL + value

        flags.setNegative(false)
        flags.setHalfCarry((regHL and 0xFFF) + (value and 0xFFF) and 0x1000 != 0)
        flags.setCarry((regHL and 0xFFFF) + (value and 0xFFFF) and 0x10000 != 0)

        hl.set(result.toUShort())
        return cycles
    }
}