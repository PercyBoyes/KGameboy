package com.percy.kgameboy.graphics

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet

class LcdConfigHelper(private val lcdc: Register8) {
    companion object {
        // LCD Control Register Bits
        const val BIT_BACKGROUND_ENABLED = 0
        const val BIT_SPRITES_ENABLED = 1
        const val BIT_LARGE_SPRITES_ENABLED = 2
        const val BIT_BACKGROUND_TILE_MAP_DISPLAY_SELECT = 3
        const val BIT_BACKGROUND_WINDOW_TILE_DATA_SELECT = 4
        const val BIT_WINDOW_ENABLED = 5
        const val BIT_WINDOW_TILE_MAP_DISPLAY_SELECT = 6
    }

    fun isDisplayEnabled() = isSet(lcdc.getUnsigned(), 7)
    fun isWindowEnabled() = isSet(lcdc.getUnsigned(), BIT_WINDOW_ENABLED)
    fun useBackgroundMapZero() = !isSet(lcdc.getUnsigned(), BIT_BACKGROUND_TILE_MAP_DISPLAY_SELECT)
    fun useWindowMapZero() = !isSet(lcdc.getUnsigned(), BIT_WINDOW_TILE_MAP_DISPLAY_SELECT)
    fun useTileSetZero() = isSet(lcdc.getUnsigned(), BIT_BACKGROUND_WINDOW_TILE_DATA_SELECT)
    fun spriteEnabled() = isSet(lcdc.getUnsigned(), BIT_SPRITES_ENABLED)
    fun backgroundEnabled() = isSet(lcdc.getUnsigned(), BIT_BACKGROUND_ENABLED)
    fun useLargeSprites() = isSet(lcdc.getUnsigned(), BIT_LARGE_SPRITES_ENABLED)
}