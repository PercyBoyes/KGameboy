package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.decrement
import com.percy.kgameboy.cpu.increment

class LDArr(private val a: Register8, private val src: Register16,
            private val bus: DefaultBusImpl, override val opCode: UByte, private val postOperation: PostOperation) : Instruction {

    override val name = "LD A, (${srcName()})"
    override val description = "Load value into Accumulator from address in 16 bit register"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        a.set(bus.read8Unsigned(src.get()))

        //println("(${src.name}) = ${toHexString(bus.read8(src.get()))}")

        when(postOperation) {
            PostOperation.POST_INCREMENT_REGISTER -> increment(src)
            PostOperation.POST_DECREMENT_REGISTER -> decrement(src)
            PostOperation.NO_POST_MODIFICATION -> {}
        }
        return cycles
    }

    private fun srcName() = when(postOperation) {
        PostOperation.POST_INCREMENT_REGISTER -> "${src.name}+"
        PostOperation.POST_DECREMENT_REGISTER -> "${src.name}-"
        PostOperation.NO_POST_MODIFICATION -> "${src.name}"
    }

}