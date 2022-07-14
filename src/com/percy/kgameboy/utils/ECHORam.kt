package com.percy.kgameboy.utils

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.bus.MemoryRegion

class ECHORam(private var bus: DefaultBusImpl) : MemoryRegion {
    private val offset = 0x2000u

    override fun addressInRange(address: UShort) = address >= 0xe000u && address <= 0xfdffu

    override fun write8(address: UShort, value: UByte) = bus.write8((address - offset).toUShort(), value)

    override fun read8(address: UShort): UByte = bus.read8Unsigned((address - offset).toUShort())
}