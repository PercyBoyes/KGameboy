package com.percy.kgameboy.cpu

import com.percy.kgameboy.common.Register16

class DefaultRegister16(override val name: String) : Register16 {
    private var value: UShort = 0x0000u

    override fun set(value: UShort) { this.value = value }
    override fun get() = value
}