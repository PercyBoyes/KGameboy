package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.swapNibbles

class SWAP (private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SWAP ${src.name}"
    override val description = "Swap the high and low nibbles of ${src.name}"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val result = swapNibbles(src.getUnsigned())
        src.set(result)
        flags.setZero(src.isZero())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(false)
        return cycles
    }
}