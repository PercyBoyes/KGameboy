package com.percy.kgameboy.test

import com.percy.kgameboy.bus.Bus

class SimpleBus : Bus {
    val memory = Array<UByte>(200) { 0x00u }

    override fun write8(address: UShort, value: UByte) {
        memory[address.toInt()] = value
    }

    override fun read8Unsigned(address: UShort) = memory[address.toInt()]

    override fun write16(dest: UShort, value: UShort) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun read16Unsigned(src: UShort): UShort {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}