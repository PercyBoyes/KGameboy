package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.utils.toHexString
import kotlin.system.exitProcess

class InvalidInstruction(override val opCode: UByte) : Instruction {
    override val name = "Invalid Opcode: ${toHexString(opCode)}"
    override val description = "The opCode doesn't exist"
    override val length = 0

    override fun run(operand: UByteArray): Int {
        println("$name - $description")
        exitProcess(0)
        return 0
    }
}