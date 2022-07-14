package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register8

class XORr(private val src: Register8, private val a: Register8, private val flags: Flags, override val opCode: UByte) : Instruction {
    override val name = "XOR ${src.name}"
    override val description = "XOR each bit of A with register and store result in A"
    override val length = 1
    private val cycles = 4

    override fun run(operand: UByteArray) : Int {
        a.set(xor(a.getUnsigned(), src.getUnsigned(), flags))
        return cycles
    }

    override fun test() {
        val r0 = Register8("TestA")
        val r1 = Register8("TestB")
        val flags = Flags(Register8("Flags"))

        val xor = XORr(r0, r1, flags, 0x00u)

        val operand = UByteArray(2)

        flags.clear()
        r0.set(0u)
        r1.set(0u)

        xor.run(operand)


    }
}