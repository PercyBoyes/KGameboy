package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString
import com.percy.kgameboy.utils.toUShort

class LDAa16(private val a: Register8, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD ${a.name}, (a16)"
    override val description = "Store value at address a16 to the accumulator"
    override val length = 3
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        val address = toUShort(operand[1], operand[0])
        a.set(bus.read8Unsigned(address.toUShort()))
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        val address = toUShort(operand[1], operand[0])
        val mem = bus.read8Unsigned(address.toUShort())
        return "${a.name} = ${toHexString(a.getUnsigned())}, Memory Value = ${toHexString(mem)}]"
    }
}