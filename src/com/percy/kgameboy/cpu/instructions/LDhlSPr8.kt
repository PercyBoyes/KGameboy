package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.offset

class LDhlSPr8(private val sp: Register16, private val hl: Register16, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "LD HL, SP + r8"
    override val description = "Load the value of SP with offset r8 into the HL"
    override val length = 2
    private val cycles = 12

    /**
     * Adds signed value to the SP and stores it in HL, calculates carry and half carry based on unsigned value
     */
    override fun run(operand: UByteArray): Int {
        flags.setZero(false)
        flags.setNegative(false)
        flags.setCarry((((sp.get() and 0xffu) + (operand[0] and 0xffu)) and 0x100u) == 0x100u)
        flags.setHalfCarry((((sp.get() and 0x0fu) + (operand[0] and 0x0fu)) and 0x10u) == 0x10u)

        hl.set(sp.get())
        offset(hl, operand[0].toByte())

        return cycles
    }
}