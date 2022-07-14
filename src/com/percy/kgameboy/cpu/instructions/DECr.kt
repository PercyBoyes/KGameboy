package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.decrement
import com.percy.kgameboy.utils.toHexString
import com.percy.kgameboy.cpu.willSubCauseHalfCarry

class DECr(private val src: Register8, private val flags: Flags,
           override val opCode: UByte) : Instruction {
    override val name = "DEC ${src.name}"
    override val description = "Decrement ${src.name}"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray): Int {
        val hc = willSubCauseHalfCarry(src.getUnsigned(), 0x1u)
        decrement(src)
        flags.setZero(src.isZero())
        flags.setNegative(true)
        flags.setHalfCarry(hc)

//        if (src.name == "B")
//            println(               "B register: ${toHexString(src.get())}")

        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${src.name} = ${toHexString(src.getUnsigned())}, Flags: [Z=${flags.isZeroSet()}, N=${flags.isNegSet()}, H=${flags.isHalfCarrySet()}, C=${flags.isCarrySet()}]"
    }
}