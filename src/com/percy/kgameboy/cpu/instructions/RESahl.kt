package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.setBit

class RESahl(private val hl: Register16, private val bit: Int,
             private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "RES $bit, (${hl.name})"
    override val description = "Reset the bit $bit in memory (${hl.get()})"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        var value = bus.read8Unsigned(hl.get())
        value = setBit(value, bit, false)
        bus.write8(hl.get(), value)
        return cycles
    }
}