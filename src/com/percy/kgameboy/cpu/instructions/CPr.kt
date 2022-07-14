package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class CPr(private val a: Register8, private val src: Register8, private val flags: Flags,
          override val opCode: UByte) : Instruction {
    override val name = "CP ${src.name}"
    override val description = "Compare A with value in register ${src.name}"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray): Int {
        sub(a.getUnsigned(), src.getUnsigned(), flags)
        return cycles
    }
}