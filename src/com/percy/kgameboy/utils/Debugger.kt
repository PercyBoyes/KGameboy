package com.percy.kgameboy.utils

import com.percy.kgameboy.cpu.CPU
import com.percy.kgameboy.bus.DefaultBusImpl
import java.util.*

class Debugger(private val bus: DefaultBusImpl? = null) {
    private val instructionBreakPoints: LinkedList<UByte> = LinkedList()        // Break when opcode is executed
    private val breakPoints : LinkedList<UShort> = LinkedList()                 // Break when program counter matches
    private val dumpPoints : LinkedList<UShort> = LinkedList()                  // Dump processor state when program counter matches
    private val memoryWriteBreaks : LinkedList<UShort> = LinkedList()           // Break when memory address is written too

    private var memoryBreaksEnabled = false
    private var memoryBreaks = 0

    private var lastAddress: UShort = 0x0000u
    private var lastInstructionInformation = ""
    private var paused = false

    private var shouldStep = false

    init {
        // instructionBreakPoints.add(0x18u)


         //breakPoints.add(0xc363u)
//
//        memoryWriteBreaks.add(0xff80u)
//        memoryWriteBreaks.add(0xdf7fu)
//
//        dumpPoints.add(0xc48fu)
//        dumpPoints.add(0xc490u)
//        dumpPoints.add(0xc492u)
//        dumpPoints.add(0xc493u)
//        dumpPoints.add(0xc494u)
//        dumpPoints.add(0xc496u)
//        dumpPoints.add(0xc498u)
//        dumpPoints.add(0xc499u)
    }

    fun checkDumpPoints(address: UShort, cpu: CPU) {
        if (dumpPoints.contains(address)) {
            cpu.dumpState()
        }
    }

    fun breakPoint() {
        paused = true
    }

    fun enableMemorybreaks() {
        memoryBreaksEnabled = true
    }

    fun checkMemoryWriteBreakPoints(address: UShort, value: UByte) {
        if (!memoryBreaksEnabled) return

        paused = memoryWriteBreaks.contains(address)

        if (paused) {
            memoryBreaks++
            println("MEMORY BREAK: Write to ${toHexString(address)}, value: ${toHexString(value)}      : (${memoryBreaks})")
        }
    }

    fun checkBreakPoints(address: UShort, opCode: UByte, instructionData: String) {
        //bus?.let { if (it.isBooting()) return }

        lastAddress = address
        lastInstructionInformation = instructionData
        paused = breakPoints.contains(address) || instructionBreakPoints.contains(opCode)

        if (shouldStep) {
            shouldStep = false
            paused = true
        }
    }

    fun resumeUntil(address: UShort) {
        if (breakPoints.isNotEmpty()) {
            breakPoints.clear()
        }
        breakPoints.add(address)
        resume()
    }

    fun breakPointHit() = paused

    fun step() {
        shouldStep = true
        paused = false
    }

    fun resume() {
        shouldStep = false
        paused = false
    }

    fun getLastInstruction() = lastInstructionInformation
}