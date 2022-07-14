package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.setBit

class SETr(private val src: Register8, private val bit: Int, override val opCode: UByte) : Instruction {
    override val name = "SET $bit, ${src.name}"
    override val description = "Set the bit $bit in register ${src.getUnsigned()}"
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        src.set(setBit(src.getUnsigned(), bit, true))
        return cycles
    }
}