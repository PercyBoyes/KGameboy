package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class DAA(private val a: Register8, private val flags: Flags,
          override val opCode: UByte) : Instruction {

    override val name = "DAA"
    override val description = "Decimal adjust register A to get a correct BCD representation after an arithmetic instruction."
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        var regA = a.getUnsigned().toInt()

        if (!flags.isNegSet()) {
            if (flags.isHalfCarrySet() || (regA and 0xf) > 9) regA += 0x06
            if (flags.isCarrySet() || regA > 0x9f) regA += 0x60
        } else {
            if (flags.isHalfCarrySet()) regA = (regA - 6) and 0xFF
            if (flags.isCarrySet()) regA -= 0x60
        }

        flags.setHalfCarry(false)
        flags.setZero(false)
        if ((regA and 0x100) == 0x100) {
            flags.setCarry(true)
        }

        regA = regA and 0xFF

        if (regA == 0) flags.setZero(true)

        a.set(regA.toUByte())

        return cycles
    }
}