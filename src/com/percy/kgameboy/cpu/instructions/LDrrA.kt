package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.decrement
import com.percy.kgameboy.cpu.increment
import com.percy.kgameboy.utils.toHexString

class LDrrA(private val src: Register16, private val srcR: Register8,
            private val bus: DefaultBusImpl, override val opCode: UByte, private val postOperation: PostOperation)
    : Instruction {

    override val name = "LD (${destName()}), ${srcR.name}"
    override val description = "Store Accumulator to address from register"
    override val length = 1
    private val cycles = 8

    override fun run(operand: UByteArray) : Int {
        bus.write8(src.get(), srcR.getUnsigned())

        when(postOperation) {
            PostOperation.POST_INCREMENT_REGISTER -> increment(src)
            PostOperation.POST_DECREMENT_REGISTER -> decrement(src)
            PostOperation.NO_POST_MODIFICATION -> {}
        }

        //println("          HL: ${toHexString(src.get())}")
        return cycles
    }

    private fun destName() = when(postOperation) {
            PostOperation.POST_INCREMENT_REGISTER -> "${src.name}+"
            PostOperation.POST_DECREMENT_REGISTER -> "${src.name}-"
            PostOperation.NO_POST_MODIFICATION -> "${src.name}"
        }

    override fun getState(operand: UByteArray) : String {
        return "${src.name} = ${toHexString(src.get())}, ${srcR.name} = ${toHexString(srcR.getUnsigned())}"
    }
}