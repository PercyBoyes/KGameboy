package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8

class LDrA(private val src: Register8, private val a: Register8,
           private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {

    override val name = "LD (${src.name}), A"
    override val description = "Store Accumulator to zero page address #ff00 + ${src.name}"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        val address = 0xff00u + src.getUnsigned()
        bus.write8(address.toUShort(), a.getUnsigned())
        return cycles
    }
}