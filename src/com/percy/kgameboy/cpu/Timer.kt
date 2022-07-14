package com.percy.kgameboy.cpu

import com.percy.kgameboy.bus.MemoryRegion
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.isSet

class Timer(private val interruptManager: InterruptManager, private val logger: SystemLogger) :
    MemoryRegion {
    private val div = Register8("DIV")
    private val tima = Register8("TIMA")
    private val tma = Register8("TMA")
    private val tmc = Register8("TMC")

    // CLOCK SPEED = 4194304
    private var timerCounter = 1024
    private var dividerCounter = 0

    fun getDiv() = div.getUnsigned()
    fun getTima() = tima.getUnsigned()
    fun getTma() = tma.getUnsigned()
    fun getTmc() = tmc.getUnsigned()

    fun clock() {
        clockDividerRegister()

        if (isTimerEnabled()) {                                                                                         // the clock must be enabled to update the clock
            timerCounter--
            if (timerCounter <= 0) {                                                                                    // enough cpu clock cycles have happened to update the timer

                setClockFreq()                                                                                          // reset timerCounter to the correct value

                if (tima.getUnsigned() == 0xffu.toUByte()) {                                                            // timer about to overflow
                    tima.set(tma.getUnsigned())
                    interruptManager.requestInterrupt(InterruptManager.InterruptType.TIMER)
                    logger.log(SystemLogger.Component.TIMER, "com.percy.kgameboy.cpu.Timer Interrupt!")
                } else {
                    increment(tima)
                }
            }
        }
    }

    private fun isTimerEnabled() = isSet(tmc.getUnsigned(), 2)

    private fun setClockFreq() {
        when (tmc.getUnsigned() and 0x3u) {
            0x00u.toUByte() -> timerCounter = 1024                                                                      // freq 4096
            0x01u.toUByte() -> timerCounter = 16                                                                        // freq 262144
            0x02u.toUByte() -> timerCounter = 64                                                                        // freq 65536
            0x03u.toUByte() -> timerCounter = 256                                                                       // freq 16382
            else -> {}
        }
    }

    private fun clockDividerRegister() {
        dividerCounter++;
        if (dividerCounter >= 255) {
            dividerCounter = 0
            increment(div)
        }
    }

    override fun addressInRange(address: UShort) : Boolean = address >= 0xff04u && address < 0xff08u

    override fun write8(address: UShort, value: UByte) {
        when (address) {
            0xff04u.toUShort() -> div.set(0x00u)
            0xff05u.toUShort() -> tima.set(value)
            0xff06u.toUShort() -> tma.set(value)
            0xff07u.toUShort() -> tmc.set(value)
            else -> {}
        }
    }

    override fun read8(address: UShort) : UByte {
        return when (address) {
            0xff04u.toUShort() -> div.getUnsigned()
            0xff05u.toUShort() -> tima.getUnsigned()
            0xff06u.toUShort() -> tma.getUnsigned()
            0xff07u.toUShort() -> tmc.getUnsigned()
            else -> 0x00u
        }
    }
}