package com.percy.kgameboy.cartridge.mbcs

interface MemoryBankController {
    fun write8(address: UShort, value: UByte)
    fun read8(address: UShort) : UByte

    fun restoreRamDump(ramBanks : Array<ByteArray>)
    fun getRamDump() : Array<ByteArray>
}