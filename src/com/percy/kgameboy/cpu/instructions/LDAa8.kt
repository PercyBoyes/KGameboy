package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8

class LDAa8 (private val a: Register8, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD ${a.name}, (a8)"
    override val description = "Load Accumulator with value from zero page address #ff00 + a8 (a8 from operand)"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val address = 0xff00u + operand[0]
        a.set(bus.read8Unsigned(address.toUShort()))
        return cycles
    }
}