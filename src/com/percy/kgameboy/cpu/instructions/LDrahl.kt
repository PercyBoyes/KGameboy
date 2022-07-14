package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

class LDrahl (private val dest: Register8, private val src: Register16,
              private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD ${dest.name}, (${src.name})"
    override val description = "Copy value in memory at address in HL register to ${dest.name}"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        dest.set(bus.read8Unsigned(src.get()))
        return cycles
    }
}
