package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.toHexString

class LDa8A (private val a: Register8, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD (a8), ${a.name}"
    override val description = "Store Accumulator to zero page address #ff00 + a8 (a8 from operand)"
    override val length = 2
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val address = 0xff00u + operand[0]
        bus.write8(address.toUShort(), a.getUnsigned())
        return cycles
    }

    override fun getState(operand: UByteArray) : String {
        return "${a.name} = ${toHexString(a.getUnsigned())}, Address = ${toHexString((0xff00u + operand[0]).toUShort())}, Memory = ${toHexString(bus.read8Unsigned((0xff00u + operand[0]).toUShort()))}"
    }
}