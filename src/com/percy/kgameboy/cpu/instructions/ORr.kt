package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString

class ORr(val a : Register8, val src: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "OR ${src.name}"
    override val description = "Or the value in register ${src.name} to A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        a.set(or(a.getUnsigned(),src.getUnsigned(), flags))
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${a.name} = ${toHexString(a.getUnsigned())}, ${src.name} = ${toHexString(src.getUnsigned())}, Flags: [Z=${flags.isZeroSet()}, N=${flags.isNegSet()}, H=${flags.isHalfCarrySet()}, C=${flags.isCarrySet()}]"
    }
}