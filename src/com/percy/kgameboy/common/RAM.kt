package com.percy.kgameboy.common

class RAM(val size: Int, private val offset: UShort) {
    private val mem = Array<UByte>(size) { 0x00u }

    fun read8(address: UShort) = mem[(address - offset).toInt()]

    fun write8(address: UShort, value: UByte) {
        mem[(address - offset).toInt()] = value
    }

    fun initialise(initialState: ByteArray) {
        var byteIndex = 0
        while(byteIndex < mem.size) {
            this.mem[byteIndex] = initialState[byteIndex].toUByte()
            byteIndex++
        }
    }

    fun getRamData() : ByteArray {
        val outputArray = ByteArray(mem.size)

        for ((i, unsignedByte) in mem.withIndex())
            outputArray[i] = unsignedByte.toByte()

        return outputArray
    }
}