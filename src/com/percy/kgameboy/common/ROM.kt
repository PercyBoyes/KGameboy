package com.percy.kgameboy.common

class ROM(private val offset: UShort, srcData: UByteArray, start: UInt, end: UInt) {
    private val romData = UByteArray((end - start).toInt())

    init {
        var i = 0
        while (i < romData.size) {
            romData[i] = srcData[(start + i.toUInt()).toInt()]
            i++
        }
    }

    fun read8(address: UShort) = romData[(address - offset).toInt()]
}