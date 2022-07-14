package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class SRA(private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SRA ${src.name}"
    override val description = "Shift ${src.name} Right 1 bit"
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val bit0 = isSet(src.getUnsigned(), 0)
        src.set((src.getUnsigned().toUInt() shr 1).toUByte())
        src.set(setBit(src.getUnsigned(), 7, isSet(src.getUnsigned(), 6)))
        flags.setZero(src.isZero())
        flags.setHalfCarry(false)
        flags.setNegative(false)
        flags.setCarry(bit0)
        return cycles
    }
}