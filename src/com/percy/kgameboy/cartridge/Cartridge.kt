package com.percy.kgameboy.cartridge

import com.percy.kgameboy.cartridge.mbcs.MemoryBankController
import com.percy.kgameboy.bus.MemoryRegion
import com.percy.kgameboy.common.ROM
import com.percy.kgameboy.cartridge.mbcs.MBC1
import com.percy.kgameboy.cartridge.mbcs.MBC3
import com.percy.kgameboy.cartridge.mbcs.NoMBC
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.toHexString
import java.util.*
import kotlin.system.exitProcess

class Cartridge(cartData: ByteArray, private val logger: SystemLogger) : MemoryRegion {
    private enum class MBCType {
        ROM_ONLY,
        MBC_1,
        MBC_2,
        MBC_3,
        UNKNOWN
    }

    private val mbc: MemoryBankController

    init {
        val temporaryMemory = UByteArray(cartData.size)

        for(index in cartData.indices) {
            temporaryMemory[index] = cartData[index].toUByte()
        }

        // Figure out which memory bank controller to use
        val mbcType = when (temporaryMemory[0x147]) {
            0x00u.toUByte() -> MBCType.ROM_ONLY
            0x01u.toUByte() -> MBCType.MBC_1
            0x02u.toUByte() -> MBCType.MBC_1
            0x03u.toUByte() -> MBCType.MBC_1
            0x05u.toUByte() -> MBCType.MBC_2
            0x06u.toUByte() -> MBCType.MBC_2
            0x13u.toUByte() -> MBCType.MBC_3
            else -> MBCType.UNKNOWN
        }

        println("Reading Cartridge Header....")
        println("Cartridge Type: ${toHexString(temporaryMemory[0x147])}")

        val romBankCount: UInt = when (temporaryMemory[0x148]) {
            0x00u.toUByte() -> 2u   //32KByte (no ROM banking)
            0x01u.toUByte() -> 4u   //64KByte (4 banks)
            0x02u.toUByte() -> 8u   //128KByte (8 banks)
            0x03u.toUByte() -> 16u  //256KByte (16 banks)
            0x04u.toUByte() -> 32u  //512KByte (32 banks)
            0x05u.toUByte() -> 64u  //1MByte (64 banks)  - only 63 banks used by MBC1
            0x06u.toUByte() -> 128u //2MByte (128 banks) - only 125 banks used by MBC1
            0x07u.toUByte() -> 256u //4MByte (256 banks)
            0x08u.toUByte() -> 512u //8MByte (512 banks)
            0x52u.toUByte() -> 72u  //1.1MByte (72 banks)
            0x53u.toUByte() -> 80u  //1.2MByte (80 banks)
            0x54u.toUByte() -> 96u  //1.5MByte (96 banks)
            else -> 0u
        }

        println("ROM Banks: $romBankCount")

        if (mbcType == MBCType.UNKNOWN) {
            println("Unknown Cartridge Type: ${toHexString(temporaryMemory[0x147])}")
            exitProcess(0)
        }

        val romBankList = LinkedList<ROM>()
        var bankIndex: UInt = 0u
        while (bankIndex < romBankCount) {
            romBankList.add(getRomBank(bankIndex, temporaryMemory))
            bankIndex++
        }

        val romBanks = romBankList.toTypedArray()
        mbc = when (mbcType) {
            MBCType.ROM_ONLY -> NoMBC(romBanks)
            MBCType.MBC_1 -> MBC1(romBanks, getRamBankCount(temporaryMemory[0x149]), logger)
            MBCType.MBC_3 -> MBC3(romBanks, getRamBankCount(temporaryMemory[0x149]), logger)
            else -> NoMBC(romBanks) // Should fail here
        }

    }

    private fun getRamBankCount(value: UByte) = when (value) {
        0x00u.toUByte() -> 0          //        00h - None
        0x01u.toUByte() -> 1          //        01h - 2 KBytes
        0x02u.toUByte() -> 1          //        02h - 8 Kbytes
        0x03u.toUByte() -> 4          //        03h - 32 KBytes (4 banks of 8KBytes each)
        0x04u.toUByte() -> 16         //        04h - 128 KBytes (16 banks of 8KBytes each)
        0x05u.toUByte() -> 8          //        05h - 64 KBytes (8 banks of 8KBytes each)
        else -> 0
    }

    private fun getRomBank(bankIndex: UInt, data: UByteArray) : ROM {
        val ROM_BANK_SIZE = 0x4000u
        val start = bankIndex * ROM_BANK_SIZE
        val end = start + ROM_BANK_SIZE
        val offset = if(bankIndex == 0u) 0x0000u else 0x4000u

        return ROM(offset.toUShort(), data, start, end)
    }

    override fun addressInRange(address: UShort) = (address >= 0x0000u && address < 0x8000u) || (address >= 0xa000u && address < 0xc000u)

    override fun write8(address: UShort, value: UByte) = mbc.write8(address, value)

    override fun read8(address: UShort) : UByte = mbc.read8(address)

    fun getRamDump() = mbc.getRamDump()

    fun initialiseRam(banks: Array<ByteArray>) = mbc.restoreRamDump(banks)
}