package com.percy.kgameboy.bus

import com.percy.kgameboy.utils.*

interface Bus {
    fun write8(address: UShort, value: UByte)

    fun read8Unsigned(address: UShort) : UByte
    fun read8Signed(address: UShort) : Byte = read8Unsigned(address).toByte()

    fun write16(dest: UShort, value: UShort)
    fun read16Unsigned(src: UShort) : UShort
}