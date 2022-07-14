package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class SBCr8(val a : Register8, private val src: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SBC ${a.name}, ${src.name}"
    override val description = "Subtract operand value, and the carry value from A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray): Int {
        a.set(sbc(a.getUnsigned(), src.getUnsigned(), flags))
        return cycles
    }
}