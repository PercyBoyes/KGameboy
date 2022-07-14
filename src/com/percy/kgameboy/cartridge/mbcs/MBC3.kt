package com.percy.kgameboy.cartridge.mbcs

import com.percy.kgameboy.common.RAM
import com.percy.kgameboy.common.ROM
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.toHexString
import kotlin.system.exitProcess

class MBC3(private val romBanks: Array<ROM>, ramBankCount: Int, private val logger: SystemLogger) :
    MemoryBankController {
    private val ramBanks: Array<RAM>
    private var currentRamBank: Int = 0
    private var currentRomBank: Int = 1
    private var ramEnabled = false

    init {
        println("Cart with MBC3 Identified. ROM Banks: ${romBanks.size}, RAM Banks: $ramBankCount")
        ramBanks = Array(if(ramBankCount > 0) ramBankCount else 1) { RAM(0x2000, 0xa000u) }
    }

    override fun write8(address: UShort, value: UByte) {
        if (address < 0x8000u) {
            handleBanking(address, value)
        } else if ((address >= 0xa000u) && (address < 0xc000u)) {
            if (ramEnabled) {
                ramBanks[currentRamBank].write8(address, value)
            }
        } else {
            print("Unhandled Write - Address: ${toHexString(address)}, Value: ${toHexString(value)}")
        }
    }

    override fun read8(address: UShort): UByte {
        return if (address < 0x4000u) {
            romBanks[0].read8(address)
        } else if (address >= 0x4000u && address < 0x8000u) {
            romBanks[currentRomBank].read8(address)
        } else if (address >= 0xa000u && address < 0xc000u) {
            ramBanks[currentRamBank].read8(address)
        } else {
            exitProcess(0)
            0x00u               // TODO: There was an error - attempt to read from memory outside of cartridge space
        }
    }

    private fun handleBanking(address: UShort, value: UByte) {
        if (address < 0x2000u) {
            enableRam(value)
        } else if ((address >= 0x2000u) && (address < 0x4000u)) {        // do ROM bank change
            changeRomBank(value)
        } else if ((address >= 0x4000u) && (address < 0x6000u)) {        // do ROM or RAM bank change
            ramBankChange(value)
        } else if ((address >= 0x6000u) && (address < 0x8000u)) {         // this will change whether we are doing ROM banking or RAM banking with the above if statement
            // TODO: Not implemented
        }
    }

    private fun ramBankChange(value: UByte) {
        if (value <= 0x03u) {
            currentRamBank = value.toInt() and 0x03
        } else {
            // TODO: Implement RTC Registers
        }
    }

    private fun changeRomBank(value: UByte) {
        if (value == 0x00u.toUByte()) { currentRomBank = 1 }

        val romBankBits = value and 0x7fu
        currentRomBank = romBankBits.toInt()
    }

    private fun enableRam(value: UByte) {
        if (value == 0x0au.toUByte()) ramEnabled = true

        if (value == 0x00u.toUByte()) ramEnabled = false
    }

    override fun restoreRamDump(newRamBanks: Array<ByteArray>) {
        for ((i, ramBank) in ramBanks.withIndex())
            ramBank.initialise(newRamBanks[i])
    }

    override fun getRamDump(): Array<ByteArray> {
        val outputArray = Array(ramBanks.size) {ByteArray(0x2000)}

        for((i, ramBank) in ramBanks.withIndex())
            outputArray[i] = ramBank.getRamData()

        return outputArray
    }
}