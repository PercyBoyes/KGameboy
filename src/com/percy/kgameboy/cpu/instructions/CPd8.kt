package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString

class CPd8(private val a: Register8, private val flags: Flags,
           override val opCode: UByte) : Instruction {
    override val name = "CP d8"
    override val description = "Compare A with operand byte"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        sub(a.getUnsigned(), operand[0], flags)
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${a.name} = ${toHexString(a.getUnsigned())}, Operand = ${toHexString(operand[0])}, Flags: [Z=${flags.isZeroSet()}, N=${flags.isNegSet()}, H=${flags.isHalfCarrySet()}, C=${flags.isCarrySet()}"
    }
}