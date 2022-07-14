package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack

class PUSHr16(private val src: Register16, private val stack: Stack,
              override val opCode: UByte) : Instruction {
    override val name = "PUSH ${src.name}"
    override val description = "PUSH value of 16 bit register onto stack"
    override val length = 1
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        stack.push(src.get())
        return cycles
    }
}