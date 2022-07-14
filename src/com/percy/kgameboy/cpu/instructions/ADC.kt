package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class ADC(private val a: Register8, private val src: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "ADC A, ${src.name}"
    override val description = "Add ${src.name} value, and the carry value from A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray): Int {
        a.set(adc(a.getUnsigned(), src.getUnsigned(), flags))
        return cycles
    }
}