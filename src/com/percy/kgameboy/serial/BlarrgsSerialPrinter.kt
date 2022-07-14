package com.percy.kgameboy.serial

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.utils.Debugger

class BlarrgsSerialPrinter(private val bus: DefaultBusImpl,
                           private val debugger: Debugger) {
    fun clock() {
        if (bus.read8Unsigned(0xff02u) == 0x81u.toUByte()) {
            val c: Char = bus.read8Unsigned(0xff01u).toByte().toChar()
            print("$c")
            bus.write8(0xff02u, 0x00u)
            // debugger.breakPoint()
        }
    }
}