package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit
import com.percy.kgameboy.cpu.shiftLeft

class RLC(private val src: Register8, private val flags: Flags,
          override val opCode: UByte) : Instruction {
    override val name = "RLC ${src.name}"
    override val description = "Rotates bits in ${src.name} to the left"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val bit7 = isSet(src.getUnsigned(), 7)
        shiftLeft(src)
        src.set(setBit(src.getUnsigned(), 0, bit7))
        flags.setZero(src.isZero())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit7)
        return cycles
    }
}