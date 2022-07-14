package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.utils.toUShort

class LDa16SP(private val sp: Register16, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "LD (a16), ${sp.name}"
    override val description = "Store value of SP to memory at address a16"
    override val length = 3
    private val cycles = 12

    override fun run(operand: UByteArray) : Int {
        val address = toUShort(operand[1], operand[0])
        bus.write16(address, sp.get())
        return cycles
    }
}