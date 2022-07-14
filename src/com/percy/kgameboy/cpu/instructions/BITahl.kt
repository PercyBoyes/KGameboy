package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.isSet

class BITahl(private val src: Register16, private val bit: Int, private val bus: DefaultBusImpl,
             private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "BIT $bit, (${src.name})"
    override val description = "Copy the compliment of the bit b in memory location (hl) to the zero flag"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val isSet = isSet(bus.read8Unsigned(src.get()), bit)
        flags.setZero(!isSet)
        flags.setHalfCarry(true)
        flags.setNegative(false)
        return cycles
    }
}