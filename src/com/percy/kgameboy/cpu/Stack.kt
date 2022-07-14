package com.percy.kgameboy.cpu

import com.percy.kgameboy.bus.Bus
import com.percy.kgameboy.common.Register16

class Stack(private val sp: Register16, private val bus: Bus) {

    fun push(value: UShort) {
        decrement(sp)
        decrement(sp)
        bus.write16(sp.get(), value)

    }

    fun pop() : UShort {
        val value = bus.read16Unsigned(sp.get())
        increment(sp)
        increment(sp)
        return value
    }
}