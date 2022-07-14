package com.percy.kgameboy.graphics

import com.percy.kgameboy.utils.SystemLogger

class LCD(private val logger: SystemLogger) {
    companion object {
        const val SCREEN_WIDTH_PIXELS = 160
        const val SCREEN_HEIGHT_PIXELS = 144
        const val TOTAL_SCREEN_PIXELS = SCREEN_WIDTH_PIXELS * SCREEN_HEIGHT_PIXELS
    }

    enum class Colour {
        WHITE,
        LIGHT_GRAY,
        DARK_GRAY,
        BLACK,
        UNKNOWN
    }

    private var backBuffer = Array(TOTAL_SCREEN_PIXELS) { Colour.UNKNOWN }
    private var displayBuffer = Array(TOTAL_SCREEN_PIXELS) { Colour.UNKNOWN }

    fun drawPixel(x: Int, y: Int, colour: Colour) {
        if (pixelValidRange(x, y)) {
            val index = (y * SCREEN_WIDTH_PIXELS) + x
            backBuffer[index] = if (index in 0 until TOTAL_SCREEN_PIXELS) colour else Colour.UNKNOWN
        }
    }

    fun getPixel(x: Int, y: Int) = if (pixelValidRange(x, y)) backBuffer[(y * SCREEN_WIDTH_PIXELS) + x] else Colour.UNKNOWN

    fun pixelValidRange(x: Int, y: Int) = (x in 0 until SCREEN_WIDTH_PIXELS && y in 0 until SCREEN_HEIGHT_PIXELS)

    fun frameComplete() {
        val tempBuffer = displayBuffer
        displayBuffer = backBuffer
        backBuffer = tempBuffer
        logger.log(SystemLogger.Component.DISPLAY, "Swap Display Buffers")
    }

    fun getCurrentDisplayFrame() = displayBuffer
}