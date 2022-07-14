package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit
import com.percy.kgameboy.cpu.shiftRight

class RRC(private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "RRC ${src.name}"
    override val description = "Rotates bits in ${src.name} to the right"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val bit0 = isSet(src.getUnsigned(), 0)
        shiftRight(src)
        src.set(setBit(src.getUnsigned(), 7, bit0))
        flags.setZero(src.isZero())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit0)
        return cycles
    }
}