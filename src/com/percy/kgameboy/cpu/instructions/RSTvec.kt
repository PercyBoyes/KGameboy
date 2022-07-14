package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.utils.toHexString

class RSTvec(private val pc: Register16, private val stack: Stack,
             private val resetVector: UShort, override val opCode: UByte) : Instruction {
    override val name = "RST ${toHexString(resetVector)}"
    override val description = "Push pc and jump to reset vector"
    override val length = 1
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        stack.push(pc.get())
        pc.set(resetVector)
        return cycles
    }
}