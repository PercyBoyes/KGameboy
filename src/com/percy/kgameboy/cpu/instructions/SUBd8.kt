package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString

class SUBd8(val a : Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SUB d8"
    override val description = "Subtract operand value from A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(sub(a.getUnsigned(), operand[0], flags))
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${a.name} = ${toHexString(a.getUnsigned())}, Operand = ${toHexString(operand[0])}, Flags: [Z=${flags.isZeroSet()}, N=${flags.isNegSet()}, H=${flags.isHalfCarrySet()}, C=${flags.isCarrySet()}"
    }
}