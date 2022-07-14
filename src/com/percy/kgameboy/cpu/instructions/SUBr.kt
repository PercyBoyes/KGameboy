package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class SUBr(val a : Register8, val src: Register8, val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "SUB ${src.name}"
    override val description = "Subtract value in register ${src.name} from A and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        a.set(sub(a.getUnsigned(), src.getUnsigned(), flags))
        return cycles
    }
}