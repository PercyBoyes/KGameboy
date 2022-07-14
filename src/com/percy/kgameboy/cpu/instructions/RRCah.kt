package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class RRCah(private val hl: Register16, private val flags: Flags, private val bus: Bus,
            override val opCode: UByte) : Instruction {
    override val name = "RRC (${hl.name})"
    override val description = "Rotates bits in memory at (${hl.name}) to the right"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        val memVal = bus.read8Unsigned(hl.get())

        val bit0 = isSet(memVal, 0)
        var result = (memVal.toUInt() shr 1).toUByte()
        result = setBit(result, 7, bit0)
        bus.write8(hl.get(), result)

        flags.setZero(result == 0x00u.toUByte())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit0)
        return cycles
    }
}