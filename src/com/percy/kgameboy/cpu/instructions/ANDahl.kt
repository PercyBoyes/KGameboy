package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

class ANDahl(private val a : Register8, private val hl: Register16,
             private val flags: Flags, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "AND (${hl.name})"
    override val description = "Add value in memory at address in register ${hl.name} to A and store result in A"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(and(a.getUnsigned(), bus.read8Unsigned(hl.get()), flags))
        return cycles
    }
}