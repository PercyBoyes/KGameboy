package com.percy.kgameboy.cartridge.mbcs

import com.percy.kgameboy.common.RAM
import com.percy.kgameboy.common.ROM
import com.percy.kgameboy.utils.SystemLogger
import kotlin.system.exitProcess

class MBC1(private val romBanks: Array<ROM>, ramBankCount: Int, private val logger: SystemLogger) :
    MemoryBankController {
    private val ramBanks: Array<RAM>
    private var currentRamBank: Int = 0
    private var currentRomBank: Int = 1
    private var ramEnabled = false
    private var romBanking = true

    init {
        logger.log(SystemLogger.Component.MBC, "Cart with MBC1 Identified. ROM Banks: ${romBanks.size}, RAM Banks: $ramBankCount")
        ramBanks = Array(if(ramBankCount > 0) ramBankCount else 1) { RAM(0x2000, 0xa000u) }
    }

    override fun read8(address: UShort): UByte {
        return if (address < 0x4000u) {
            romBanks[0].read8(address)
        } else if (address >= 0x4000u && address < 0x8000u) {
            romBanks[currentRomBank].read8(address)
        } else if (address >= 0xa000u && address < 0xc000u) {
            ramBanks[currentRamBank].read8(address)
        } else {
            println("Attempt to read cartridge outside of available memory range")
            exitProcess(0)
            0x00u               // TODO: There was an error - attempt to read from memory outside of cartridge space
        }
    }

    override fun write8(address: UShort, value: UByte) {
        if (address < 0x8000u) {
            handleBanking(address, value)
        } else if ((address >= 0xa000u) && (address < 0xc000u)) {
            if (ramEnabled) {
                ramBanks[currentRamBank].write8(address, value)
            }
        }
    }

    private fun handleBanking(address: UShort, value: UByte) {
        if (address < 0x2000u) {
            enableRam(address, value)
        } else if ((address >= 0x0200u) && (address < 0x4000u)) {        // do ROM bank change
            changeLoRomBank(value)
        } else if ((address >= 0x4000u) && (address < 0x6000u)) {        // do ROM or RAM bank change
            if (romBanking) changeHiRomBank(value) else ramBankChange(value)
        } else if ((address >= 0x6000u) && (address < 0x8000u)) {         // this will change whether we are doing ROM banking or RAM banking with the above if statement
                changeRomRamMode(value)
        }
    }

    private fun changeRomRamMode(value: UByte) {
        val newData = value and 0x01u
        romBanking = newData == 0x00u.toUByte()
        if (romBanking)
            currentRamBank = 0x00
    }

    private fun ramBankChange(value: UByte) {
        currentRamBank = value.toInt() and 0x03
    }

    private fun changeHiRomBank(value: UByte) {
        currentRomBank = currentRomBank and 0x1f                   // turn off the upper 3 bits of the current rom
        var data = value.toInt()
        data = data and 0xe0                                       // turn off the lower 5 bits of the data
        currentRomBank = currentRomBank or data
        if (currentRomBank == 0) currentRomBank++

        logger.log(SystemLogger.Component.MBC, "Chang Hi ROM Bank: $currentRomBank")
    }

    private fun changeLoRomBank(value: UByte) {
        val lower5 = value.toInt() and 0x1f
        currentRomBank = currentRomBank and 0xe0 // turn off the lower 5
        currentRomBank = currentRomBank or lower5
        if (currentRomBank == 0) currentRomBank++

        logger.log(SystemLogger.Component.MBC, "Chang Lo ROM Bank: $currentRomBank")

        if (currentRomBank >= romBanks.size) {
            println("Current ROM Bank is out of range: $currentRomBank. Only ${romBanks.size} banks available in this cartridge.")
            exitProcess(0)
            //currentRomBank = 1
        }
    }

    private fun enableRam(address: UShort, value: UByte) {
        val testData = value and 0x0fu.toUByte()
        if (testData == 0x0au.toUByte())
            ramEnabled = true
        else if (testData == 0x00u.toUByte())
            ramEnabled = false
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