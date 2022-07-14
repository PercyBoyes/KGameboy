package com.percy.kgameboy.cpu

import com.percy.kgameboy.bus.MemoryRegion
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class InterruptManager(private val logger: SystemLogger) : MemoryRegion {
    private val irr = Register8("IRR")      // Interrupt Request Register
    private val ier = Register8("IER")      // Interrupt Enable Register

    enum class InterruptType {
        V_BLANK,
        LCD,
        TIMER,
        JOY_PAD
    }

    fun requestInterrupt(type: InterruptType) {
        val result = setBit(irr.getUnsigned(), getInterruptBit(type), true)
        irr.set(result)

        if (type == InterruptType.JOY_PAD || type == InterruptType.TIMER)
            logger.log(SystemLogger.Component.INTERRUPT_MANAGER, "Requesting ${type.name} Interrupt")
    }

    fun interruptsRequested() : Boolean {
        return (irr.getUnsigned() and ier.getUnsigned()) != 0x00u.toUByte()
    }

    fun checkInterupts(cpu: CPU) {
        if (!cpu.interruptsEnabled()) {
            logger.log(SystemLogger.Component.INTERRUPT_MANAGER, "IGNORING INT: Interrupts Disabled - Not servicing")
            if (interruptsRequested()) {
                cpu.resume()
            }
            return
        }                                // If cpu has interrupts disabled return

        if (!cpu.lastInstructionComplete()) {
            //logger.log(SystemLogger.Component.INTERRUPT_MANAGER, "IGNORING INT: CPU last instruction not yet complete")
            return
        }                          // If the cpu is currently running an operation return

        if (irr.getUnsigned() == 0x00u.toUByte()) {
            //logger.log(SystemLogger.Component.INTERRUPT_MANAGER, "IGNORING INT: No interrupt requested")
            return
        }                            // If no interrupt has been requested return

        for (i in InterruptType.values()) {                                 // For each possible interrupt,
            val interruptBit = getInterruptBit(i)
            if (isSet(irr.getUnsigned(), interruptBit)) {                          // If the interrupt is requested,
                if (isSet(ier.getUnsigned(), interruptBit)) {                       // And the interrupt is enabled,
                    cpu.disableInterrupts()
                    irr.set(setBit(irr.getUnsigned(), interruptBit, false))
                    // if (i == InterruptType.JOY_PAD) {logger.log(SystemLogger.Component.INTERRUPT_MANAGER, "Servicing ${i.name} Interrupt")}
                    cpu.serviceInterrupt(getInterruptAddress(i))                 // Interrupt the CPU
                    return          // TODO - Not sure about whether to do this
                }
            }
        }
    }

    override fun addressInRange(address: UShort): Boolean =
        (address == 0xffffu.toUShort() || address == 0xff0fu.toUShort())

    override fun write8(address: UShort, value: UByte) {
        when (address) {
            0xffffu.toUShort() -> ier.set(value)
            0xff0fu.toUShort() -> irr.set(value)
            else -> {}
        }
    }

    override fun read8(address: UShort): UByte {
        return when (address) {
            0xffffu.toUShort() -> ier.getUnsigned()
            0xff0fu.toUShort() -> irr.getUnsigned()
            else -> 0x00u
        }
    }

    private fun getInterruptBit(interrupt: InterruptType) : Int = when (interrupt) {
        InterruptType.V_BLANK -> 0
        InterruptType.LCD -> 1
        InterruptType.TIMER -> 2
        InterruptType.JOY_PAD -> 4
    }

    private fun getInterruptAddress(interrupt: InterruptType) : UShort = when(interrupt) {
        InterruptType.V_BLANK -> 0x40u
        InterruptType.LCD -> 0x48u
        InterruptType.TIMER -> 0x50u
        InterruptType.JOY_PAD -> 0x60u
    }
}