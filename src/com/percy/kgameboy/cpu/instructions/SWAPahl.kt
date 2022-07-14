package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.swapNibbles

class SWAPahl(private val src: Register16, private val flags: Flags,
              private val bus: Bus, override val opCode: UByte) : Instruction {
    override val name = "SWAP (${src.name})"
    override val description = "Swap the high and low nibbles of the byte in memory at (${src.name})"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val memValue = bus.read8Unsigned(src.get())
        val result = swapNibbles(memValue)
        flags.setZero(result == 0x00u.toUByte())
        flags.setNegative(false)
        flags.setHalfCarry(false)
        flags.setCarry(false)
        bus.write8(src.get(), result)
        return cycles
    }
}