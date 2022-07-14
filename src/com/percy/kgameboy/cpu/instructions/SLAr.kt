package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet

class SLAr(private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SLA ${src.name}"
    override val description = "Shift ${src.name} Left 1 bit"
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val bit7 = isSet(src.getUnsigned(), 7)
        src.set((src.getUnsigned().toUInt() shl 1).toUByte())
        flags.setZero(src.isZero())
        flags.setHalfCarry(false)
        flags.setNegative(false)
        flags.setCarry(bit7)
        return cycles
    }
}