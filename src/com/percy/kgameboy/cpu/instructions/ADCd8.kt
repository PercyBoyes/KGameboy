package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class ADCd8 (private val a: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "ADC A, d8"
    override val description = "Add operand value, and the carry value from A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray): Int {
        a.set(adc(a.getUnsigned(), operand[0], flags))
        return cycles
    }
}