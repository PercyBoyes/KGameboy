package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

class ORahl(val a : Register8, val hl: Register16, val flags: Flags,
            private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "OR (${hl.name})"
    override val description = "Or the value in memory at address in register ${hl.name} to A and store result in A"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        a.set(or(a.getUnsigned(), bus.read8Unsigned(hl.get()), flags))
        return cycles
    }
}