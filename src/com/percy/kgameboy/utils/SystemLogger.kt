package com.percy.kgameboy.utils

class SystemLogger {
    enum class Component {
        BUS,
        CPU,
        GPU,
        DISPLAY,
        TIMER,
        INTERRUPT_MANAGER,
        CARTRIDGE,
        GAME_PAD,
        MBC
    }

    private val enabled : MutableMap<Component,Boolean> = HashMap()

    fun isEnabled(c: Component) = enabled[c]

    fun enableLogging(component: Component) {
        enabled[component] = true
    }

    fun log(component: Component, message: String, newLine: Boolean = true) {
        if (enabled[component] == true) {
            if (newLine) println(message) else print(message)
        }
    }
}