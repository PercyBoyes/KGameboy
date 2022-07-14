package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit
import com.percy.kgameboy.cpu.shiftRight

class RRr(private val r: Register8, private val flags: Flags,
          override val opCode: UByte) : Instruction {
    override val name = "RR ${r.name}"
    override val description = "Rotates bits in ${r.name} to the right, through the carry flag."
    override val length = 2
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val bit0 = isSet(r.getUnsigned(), 0)
        shiftRight(r)
        r.set(setBit(r.getUnsigned(), 7, flags.isCarrySet()))
        flags.setZero(r.isZero())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit0)
        return cycles
    }
}