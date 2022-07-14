package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toUShort

class LDa16A(private val a: Register8, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD (a16), ${a.name}"
    override val description = "Store Accumulator to address a16 from operand"
    override val length = 3
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        val address = toUShort(operand[1], operand[0])
        bus.write8(address.toUShort(), a.getUnsigned())
        return cycles
    }
}