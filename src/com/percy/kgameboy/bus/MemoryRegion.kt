package com.percy.kgameboy.bus

interface MemoryRegion {
    fun addressInRange(address: UShort) : Boolean
    fun write8(address: UShort, value: UByte)
    fun read8(address: UShort) : UByte
}