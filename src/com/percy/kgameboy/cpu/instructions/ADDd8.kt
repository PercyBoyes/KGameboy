package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class ADDd8(val a : Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "ADD d8"
    override val description = "Add operand value to A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(add(a.getUnsigned(), operand[0], flags))
        return cycles
    }
}