package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class SRAhl(private val src: Register16, private val flags: Flags,
            private val bus: Bus, override val opCode: UByte) : Instruction {
    override val name = "SRA (${src.name}"
    override val description = "Shift ${src.name} Right 1 bit"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        val memValue = bus.read8Unsigned(src.get())

        val bit0 = isSet(memValue, 0)
        var result = ((memValue.toUInt() shr 1).toUByte())
        result = setBit(result, 7, isSet(result, 6))

        flags.setZero(result == 0x00u.toUByte())
        flags.setHalfCarry(false)
        flags.setNegative(false)
        flags.setCarry(bit0)

        bus.write8(src.get(), result)

        return cycles
    }
}