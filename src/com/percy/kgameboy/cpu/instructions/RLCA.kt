package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit
import com.percy.kgameboy.cpu.shiftLeft

class RLCA(private val a: Register8, private val flags: Flags,
           override val opCode: UByte) : Instruction {
    override val name = "RLCA"
    override val description = "Rotates bits in ${a.name} to the left"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val bit7 = isSet(a.getUnsigned(), 7)
        shiftLeft(a)
        a.set(setBit(a.getUnsigned(), 0, bit7))

        flags.setZero(false)
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit7)
        return cycles
    }
}