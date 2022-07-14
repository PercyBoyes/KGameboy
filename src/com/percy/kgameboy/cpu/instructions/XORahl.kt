package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8

class XORahl(private val hl: Register16, private val a: Register8, private val flags: Flags,
             private val bus: DefaultBusImpl, override val opCode: UByte) : Instruction {
    override val name = "XOR (hl)"
    override val description = "XOR value at memory address in register HL with value in A and store result in A"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {

        val beforeA = a.getUnsigned()
        a.set(xor(a.getUnsigned(), bus.read8Unsigned(hl.get()), flags))

        // println("A: ${toHexString(beforeA)} xor (${toHexString(hl.get())}): ${toHexString(bus.read8(hl.get()))}. Result: ${toHexString(a.get())}")

        return cycles
    }
}