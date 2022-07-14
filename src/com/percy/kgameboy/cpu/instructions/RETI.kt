package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.CPU
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.cpu.Stack

class RETI(private val pc: Register16, private val stack: Stack, private val cpu: CPU,
           override val opCode: UByte) : Instruction {
    override val name = "RETI"
    override val description = "Returns from subroutine and enable interrupts"
    override val length = 1
    private val cycles = 16

    override fun run(operand: UByteArray) : Int {
        pc.set(stack.pop())
        cpu.enableInterrupts()
        return cycles
    }
}