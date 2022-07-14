package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class ADDr(val a : Register8, val src: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "ADD ${src.name}"
    override val description = "Add value in register ${src.name} to A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        a.set(add(a.getUnsigned(), src.getUnsigned(), flags))
        return cycles
    }
}