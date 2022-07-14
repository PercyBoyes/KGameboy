package com.percy.kgameboy.bus

import com.percy.kgameboy.common.ROM
import com.percy.kgameboy.utils.*
import java.util.*

class DefaultBusImpl(bootRom: ByteArray, private val logger: SystemLogger) : Bus {
    private val temporaryMemory = UByteArray(0x10000)
    private val regions = LinkedList<MemoryRegion>()

    private val bootRom: ROM
    private var bootRomEnabled = true

    private lateinit var debugger: Debugger

    fun addDebugger(debugger: Debugger) { this.debugger = debugger }

    init {
        for(index in bootRom.indices)
            temporaryMemory[index] = bootRom[index].toUByte()

        this.bootRom = ROM(0x0000u, temporaryMemory, 0u, bootRom.size.toUInt())

        val gameBoyLogo : Array<UByte> = arrayOf(0xceu, 0xedu, 0x66u, 0x66u, 0xccu, 0x0du, 0x00u, 0x0bu, 0x03u, 0x73u, 0x00u,
                                                 0x83u, 0x00u, 0x0cu, 0x00u, 0x0du, 0x00u, 0x08u, 0x11u, 0x1fu, 0x88u, 0x89u,
                                                 0x00u, 0x0eu, 0xdcu, 0xccu, 0x6eu, 0xe6u, 0xddu, 0xddu, 0xd9u, 0x99u, 0xbbu,
                                                 0xbbu, 0x67u, 0x63u, 0x6eu, 0x0eu, 0xecu, 0xccu, 0xddu, 0xdcu, 0x99u, 0x9fu,
                                                 0xbbu, 0xb9u, 0x33u, 0x3eu)

        // Copy the gameboy logo int memory at 0x104
        for (index in gameBoyLogo.indices)
            temporaryMemory[0x104 + index] = gameBoyLogo[index]
    }

    fun addRegion(memoryRegion: MemoryRegion) {
        regions.add(memoryRegion)
    }

    override fun write8(address: UShort, value: UByte) {
        debugger.checkMemoryWriteBreakPoints(address, value)

        if (address == 0xff50u.toUShort() && value == 0x1u.toUByte()) {
            bootRomEnabled = false
            println("Disable Boot ROM")
        }

        logger.log(SystemLogger.Component.BUS,"        BUS: 8 bit write. Address: ${toHexString(address)}. Value: ${toHexString(value)}")
        for (region in regions) {
            if (region.addressInRange(address)) {
                region.write8(address, value)
                return
            }
        }
        temporaryMemory[address.toInt()] = value
    }

    override fun read8Unsigned(address: UShort) : UByte {
        if (bootRomEnabled && address <= 0xffu) return bootRom.read8(address)

        // TODO: Temporary Fix: Undocumented i/o Registers (GB only)
        if (address == 0xff4d.toUShort()) return 0xffu

        for (region in regions)
            if (region.addressInRange(address))
                return region.read8(address)

        val memValue = temporaryMemory[address.toInt()]
        logger.log(SystemLogger.Component.BUS, "        DEFAULT BUS: 8 bit read. Address: ${toHexString(address)}. Value: ${toHexString(memValue)}")
        return memValue
    }

    override fun write16(dest: UShort, value: UShort) {
        logger.log(SystemLogger.Component.BUS, "        BUS: 16 bit write. Address: ${toHexString(dest)}. Value: ${toHexString(value)}")
        write8((dest + 0x1u).toUShort(), value.topByte())
        write8(dest, value.bottomByte())
    }

    override fun read16Unsigned(src: UShort) : UShort {
        val memValue = toUShort(read8Unsigned((src + 0x1u).toUShort()), read8Unsigned(src))
        logger.log(SystemLogger.Component.BUS,"        BUS: 16 bit read. Address: ${toHexString(src)}. Value: ${toHexString(memValue)}")
        return memValue
    }
}