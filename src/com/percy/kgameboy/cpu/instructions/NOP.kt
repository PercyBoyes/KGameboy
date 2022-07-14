package com.percy.kgameboy.cpu.instructions

class NOP() : Instruction {
    override val name = "NOP"
    override val description = "No Operation"
    override val length = 1
    private val cycles = 4
    override val opCode: UByte = 0x00u

    override fun run(operand: UByteArray) = cycles
}