package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class RLah(private val src: Register16, private val flags: Flags,
           private val bus: Bus, override val opCode: UByte) : Instruction {
    override val name = "RL (${src.name})"
    override val description = "Rotates bits in memory at (${src.name}) to the left, through the carry flag."
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray): Int {
        val memVal = bus.read8Unsigned(src.get())

        val bit7 = isSet(memVal, 7)
        var result = (memVal.toUInt() shl 1).toUByte()
        result = setBit(result, 0, flags.isCarrySet())
        flags.setZero(result == 0x00u.toUByte())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(bit7)

        bus.write8(src.get(), result)
        return cycles
    }
}