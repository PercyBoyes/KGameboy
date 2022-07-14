package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.increment
import com.percy.kgameboy.utils.toHexString
import com.percy.kgameboy.cpu.willAddCauseHalfCarry

class INCr(private val src: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "INC ${src.name}"
    override val description = "Increment ${src.name}"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        val hc = willAddCauseHalfCarry(src.getUnsigned(), 0x1u)
        increment(src)
        flags.setZero(src.isZero())
        flags.setNegative(false)
        flags.setHalfCarry(hc)

        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${src.name} = ${toHexString(src.getUnsigned())}, Flags: [Z=${flags.isZeroSet()}, N=${flags.isNegSet()}, H=${flags.isHalfCarrySet()}, C=${flags.isCarrySet()}]"
    }
}