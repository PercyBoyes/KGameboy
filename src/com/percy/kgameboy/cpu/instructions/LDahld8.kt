package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16

class LDahld8(private val dest: Register16, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD (${dest.name}), d8"
    override val description = "Load memory with direct 8 bit value"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val value = operand[0]
        //       println("Load d8: 0x${value.toString(16)}")
        bus.write8(dest.get(), value)
        return cycles
    }
}