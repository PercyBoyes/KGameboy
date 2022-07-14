package com.percy.kgameboy.input

import com.percy.kgameboy.bus.MemoryRegion
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.InterruptManager
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit
import com.percy.kgameboy.utils.toHexString

class GamePadInputManager(private val interruptManager: InterruptManager,
                          private val logger: SystemLogger) : MemoryRegion {
    enum class Button {
        JOYPAD_A,
        JOYPAD_B,
        JOYPAD_SELECT,
        JOYPAD_START,
        JOYPAD_RIGHT,
        JOYPAD_LEFT,
        JOYPAD_UP,
        JOYPAD_DOWN
    }

    private fun getKeyIdentifier(button: Button) : Int = when (button) {
        Button.JOYPAD_A -> 4
        Button.JOYPAD_B -> 5
        Button.JOYPAD_SELECT -> 7
        Button.JOYPAD_START -> 6
        Button.JOYPAD_RIGHT -> 0
        Button.JOYPAD_LEFT -> 1
        Button.JOYPAD_UP -> 2
        Button.JOYPAD_DOWN -> 3
    }

    private val ff00 = Register8("ff00")        // 0xff00
    private val joyPadState = Register8("JoyPadState")

    init {
        joyPadState.set(0xffu)          // Initialise all buttons unpressed
    }

    override fun addressInRange(address: UShort) = (address == 0xff00u.toUShort())

    override fun write8(address: UShort, value: UByte) {
        logger.log(SystemLogger.Component.GAME_PAD, "GAMEPAD saw write to ${toHexString(address)}: ${toHexString(value)}")
        ff00.set(value)
    }

    override fun read8(address: UShort): UByte {
        val value = getJoypadState()
        //logger.log(SystemLogger.Component.GAME_PAD, "GAMEPAD saw read from ${toHexString(address)}: ${toHexString(value)}")
        return value
    }

    private fun getJoypadState() : UByte {
        var res = ff00.getUnsigned()
        // flip all the bits
        res = res xor 0xffu

        // are we interested in the standard buttons?
        if (!isSet(res, 4)) {
            var topJoypad = (joyPadState.getUnsigned().toUInt() shr 4).toUByte()
            topJoypad = topJoypad or 0xf0u // turn the top 4 bits on
            res = res and topJoypad // show what buttons are pressed
        }
        else if (!isSet(res,5)) {       // Direction buttons
            var bottomJoypad = joyPadState.getUnsigned() and 0x0fu
            bottomJoypad = bottomJoypad or 0xf0u
            res = res and bottomJoypad
        }

        return res
    }

    fun buttonPressed(inButton: Button) {
        logger.log(SystemLogger.Component.GAME_PAD, "Saw ${inButton.name} Press")

        val keyIdentifier = getKeyIdentifier(inButton)

        var previouslyUnset = false

        // if setting from 1 to 0 we may have to request an interrupt
        if (!isSet(joyPadState.getUnsigned(), keyIdentifier))
            previouslyUnset = true

        // remember if a keypressed its bit is 0 not 1
        joyPadState.set(setBit(joyPadState.getUnsigned(), keyIdentifier, false))

        // button pressed
        val button = (keyIdentifier > 0x03)
        val keyReq = ff00.getUnsigned()
        var requestInterupt = false

        //println("Dir buttons = ${!isSet(keyReq, 4)}, Action Buttons: ${!isSet(keyReq, 5)}, keyReq: ${toHexString(keyReq)}")

        // only request interupt if the button just pressed is
        // the style of button the game is interested in
        if (button && !isSet(keyReq, 5))
            requestInterupt = true
        else if (!button && !isSet(keyReq, 4))          // same as above but for directional button
            requestInterupt = true

        // request interrupt

        //println("Request Interrupt: ${requestInterupt}, Previously Unset: ${previouslyUnset} ")
        if (requestInterupt && !previouslyUnset)
            interruptManager.requestInterrupt(InterruptManager.InterruptType.JOY_PAD)
    }

    fun buttonReleased(button: Button) {
        logger.log(SystemLogger.Component.GAME_PAD, "Saw ${button.name} Release")
        joyPadState.set(setBit(joyPadState.getUnsigned(), getKeyIdentifier(button), true))
    }
}