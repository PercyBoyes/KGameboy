package com.percy.kgameboy.cartridge.mbcs

import com.percy.kgameboy.common.ROM

class NoMBC(private val romBanks: Array<ROM>) : MemoryBankController {
    override fun write8(address: UShort, value: UByte) {}

    override fun read8(address: UShort): UByte = if (address < 0x4000u) romBanks[0].read8(address) else romBanks[1].read8(address)

    override fun restoreRamDump(ramBanks: Array<ByteArray>) {}

    override fun getRamDump(): Array<ByteArray> = Array(0) { ByteArray(0) }
}