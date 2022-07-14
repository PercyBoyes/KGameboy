package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.isSet

class SLAah(private val src: Register16, private val flags: Flags,
            private val bus: Bus, override val opCode: UByte) : Instruction {
    override val name = "SLA (${src.name}"
    override val description = "Shift ${src.name} Left 1 bit"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        val memValue = bus.read8Unsigned(src.get())

        val bit7 = isSet(memValue, 7)
        val result = ((memValue.toUInt() shl 1).toUByte())

        flags.setZero(result == 0x00u.toUByte())
        flags.setHalfCarry(false)
        flags.setNegative(false)
        flags.setCarry(bit7)

        bus.write8(src.get(), result)

        return cycles
    }
}