package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class SBCd8(val a : Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SBC ${a.name}, d8"
    override val description = "Subtract operand value, and the carry value from A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        a.set(sbc(a.getUnsigned(), operand[0], flags))
        return cycles
    }
}