package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.setBit

class RESr(private val src: Register8, private val bit: Int, override val opCode: UByte) : Instruction {
    override val name = "RES $bit, ${src.name}"
    override val description = "Reset the bit $bit in register ${src.name}"
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        src.set(setBit(src.getUnsigned(), bit, false))
        return cycles
    }
}