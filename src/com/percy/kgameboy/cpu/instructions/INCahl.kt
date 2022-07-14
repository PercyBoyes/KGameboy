package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.willAddCauseHalfCarry

class INCahl(private val dest: Register16, private val bus: DefaultBusImpl, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "INC (${dest.name})"
    override val description = "Increment memory at address (${dest.name})"
    override val length = 1
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val current = bus.read8Unsigned(dest.get())
        val hc = willAddCauseHalfCarry(current, 0x1u)
        val result = current.inc()
        flags.setZero(result == 0x00u.toUByte())
        flags.setNegative(false)
        flags.setHalfCarry(hc)
        bus.write8(dest.get(), result)
        return cycles
    }
}