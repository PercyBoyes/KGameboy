package com.percy.kgameboy.test

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.DefaultRegister16
import com.percy.kgameboy.cpu.Stack
import com.percy.kgameboy.cpu.VirtualRegister16
import com.percy.kgameboy.cpu.instructions.POPr16

fun main(args: Array<String>) {
    val a = Register8("A")
    val f = Register8("F")
    val af = VirtualRegister16(a, f,  "AF")

    val sp = DefaultRegister16("SP")

    val bus = SimpleBus()

    val stack = Stack(sp, bus)

    val instruction = POPr16(af, stack, 0x00u)
}

    fun run() {
    }