package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.willSubCauseHalfCarry

class DECahl(private val hl: Register16, private val flags: Flags,
             private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "DEC (${hl.name})"
    override val description = "Decrement value is memory at address in ${hl.name}"
    override val length = 1
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        var value = bus.read8Unsigned(hl.get())
        val hc = willSubCauseHalfCarry(value, 0x1u)
        value = value.dec()
        flags.setZero(value == 0x00u.toUByte())
        flags.setNegative(true)
        flags.setHalfCarry(hc)
        bus.write8(hl.get(), value)
        return cycles
    }
}