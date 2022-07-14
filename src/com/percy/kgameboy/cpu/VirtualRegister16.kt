package com.percy.kgameboy.cpu

import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.bottomByte
import com.percy.kgameboy.utils.toUShort
import com.percy.kgameboy.utils.topByte

class VirtualRegister16(private val top: Register8, private val bottom: Register8, override val name: String) : Register16 {
    override fun set(value: UShort) {
        top.set(value.topByte())
        bottom.set(value.bottomByte())
    }

    override fun get() = toUShort(top.getUnsigned(), bottom.getUnsigned())
}