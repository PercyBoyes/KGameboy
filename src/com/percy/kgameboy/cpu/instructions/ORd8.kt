package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class ORd8(val a : Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "OR d8"
    override val description = "Or the operand value with A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(or(a.getUnsigned(), operand[0], flags))
        return cycles
    }
}