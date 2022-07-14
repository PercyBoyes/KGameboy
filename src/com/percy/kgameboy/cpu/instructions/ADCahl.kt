package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

class ADCahl(private val a: Register8, private val src: Register16,
             private val flags: Flags, private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "ADC A, (${src.name})"
    override val description = "Add value at memory location in ${src.name}, and the carry value from A and store result in A"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        a.set(adc(a.getUnsigned(), bus.read8Unsigned(src.get()), flags))
        return cycles
    }
}