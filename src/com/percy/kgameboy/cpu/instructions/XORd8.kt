package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class XORd8 (private val a: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "XOR d8"
    override val description = "XOR each bit of operand with register A and store result in A"
    override val length = 2
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(xor(a.getUnsigned(), operand[0], flags))
        return cycles
    }
}