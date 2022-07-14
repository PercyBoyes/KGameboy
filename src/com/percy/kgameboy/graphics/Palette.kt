package com.percy.kgameboy.graphics

/*
    Bit 7-6 - Colour for index 3
    Bit 5-4 - Colour for index 2
    Bit 3-2 - Colour for index 1
    Bit 1-0 - Colour for index 0

    Colour Values:
    0 - white
    1 - light grey
    2 - dark gray
    3 - black
*/

// given a colour index and a palette I should be able to compute a colour

fun displayColour(colour: Int) : LCD.Colour {
    return when (colour) {
        0 -> LCD.Colour.WHITE
        1 -> LCD.Colour.LIGHT_GRAY
        2 -> LCD.Colour.DARK_GRAY
        3 -> LCD.Colour.BLACK
        else -> LCD.Colour.UNKNOWN
    }
}

fun getRealColour(paletteData: Int, index: Int) : LCD.Colour {
    when(index) {
        3 -> {
            val colour = (paletteData and 0x000000c0) shr 6
            return displayColour(colour)
        }
        2 -> {
            val colour = (paletteData and 0x00000030) shr 4
            return displayColour(colour)
        }
        1 -> {
            val colour = (paletteData and 0x0000000c) shr 2
            return displayColour(colour)
        }
        0 -> {
            val colour = (paletteData and 0x00000003)
            return displayColour(colour)
        }
    }
    return LCD.Colour.UNKNOWN
}
