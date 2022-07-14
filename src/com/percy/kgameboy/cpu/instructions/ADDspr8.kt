package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.offset

class ADDspr8(private val sp: Register16, private val flags: Flags) : Instruction {
    override val name = "ADD sp, r8"
    override val description = "Add signed value to stack pointer"
    override val length = 2
    override val opCode: UByte
        get() = 0xe8u
    private val cycles = 16

    /**
     * Adds signed value to the SP, calculates carry and half carry based on unsigned value
     */
    override fun run(operand: UByteArray): Int {
        flags.setZero(false)
        flags.setNegative(false)
        flags.setCarry((((sp.get() and 0xffu) + (operand[0] and 0xffu)) and 0x100u) == 0x100u)
        flags.setHalfCarry((((sp.get() and 0x0fu) + (operand[0] and 0x0fu)) and 0x10u) == 0x10u)

        offset(sp, operand[0].toByte())

        return cycles
    }
}