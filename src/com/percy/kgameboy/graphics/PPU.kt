package com.percy.kgameboy.graphics

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.bus.MemoryRegion
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.InterruptManager
import com.percy.kgameboy.cpu.increment
import com.percy.kgameboy.utils.*


class PPU(private val display: LCD, private val interruptManager: InterruptManager,
          private val bus: DefaultBusImpl) : MemoryRegion {

    companion object {
        // Rendering Constants
        const val CLOCKS_PER_HBLANK = 204
        const val CLOCKS_PER_SCANLINE_OAM = 80
        const val CLOCKS_PER_SCANLINE_VRAM = 172
        const val CLOCKS_PER_SCANLINE = (CLOCKS_PER_SCANLINE_OAM + CLOCKS_PER_SCANLINE_VRAM + CLOCKS_PER_HBLANK)

        const val CLOCKS_PER_VBLANK = 4560
        const val SCANLINES_PER_FRAME = 144
        const val CLOCKS_PER_FRAME = (CLOCKS_PER_SCANLINE * SCANLINES_PER_FRAME) + CLOCKS_PER_VBLANK

        const val TILE_SET_ZERO_ADDRESS = 0x8000u
        const val TILE_SET_ONE_ADDRESS = 0x8800u
        const val TILE_MAP_ZERO_ADDRESS = 0x9800u
        const val TILE_MAP_ONE_ADDRESS = 0x9C00u
        const val TILE_BYTES = 16
        const val TILE_SIZE_PX = 8
        const val TILE_MAP_SIZE_TILES = 32

        const val TILE_SET_SPRITES_ADDRESS = 0x8000
        const val TOTAL_SPRITES = 40
        const val SPRITE_ATTRIBUTE_TABLE_ADDRESS = 0xfe00
        const val SPRITE_BYTES = 4
        const val SPRITE_HEIGHT = 8
        const val SPRITE_HEIGHT_LARGE = 16
        const val SPRITE_WIDTH = 8

        const val BIT_SPRITE_ATTRIBUTE_VERTICAL_FLIP = 6
        const val BIT_SPRITE_ATTRIBUTE_HORIZONTAL_FLIP = 5
        const val BIT_SPRITE_ATTRIBUTE_USE_PALETTE_ONE = 4
        const val SPRITE_PALETTE_ZERO_ADDRESS = 0xff48
        const val SPRITE_PALETTE_ONE_ADDRESS = 0xff49
    }

    private enum class SpriteAttribute {
        Y_POSITION,
        X_POSITION,
        TILE_INDEX,
        ATTRIBUTES
    }

    private enum class LcdMode {
        H_BLANK,
        V_BLANK,
        SEARCH_OAM_TABLE,
        LCD_DATA_TRANSFER
    }

    private val lcdc = Register8("LCDC")                                                                          // 0xff40 - Lcd control register
    private val stat = Register8("STAT")
    private val ly = Register8("LY")                                                                              // 0xff44 - Current scanline
    private val lyc = Register8("LYC")

    private val scrollY = Register8("SCROLL_Y")
    private val scrollX = Register8("SCROLL_X")
    private val windowY = Register8("WINDOW_Y")
    private val windowX = Register8("WINDOW_X")

    private val bgp = Register8("BGP")
    private val spp0 = Register8("SPRITE PALETTE ZERO")                                                           // 0xff48
    private val spp1 = Register8("SPRITE PALETTE ONE")                                                            // 0xff49

    private val lcdControlHelper = LcdConfigHelper(lcdc)

    private var cycleCounter = 0
    private var currentMode = LcdMode.V_BLANK

    fun clockMultiple(cycles: Int) {
        cycleCounter += cycles

        when (currentMode) {
            LcdMode.SEARCH_OAM_TABLE -> spriteAttributeModeUpdate()
            LcdMode.LCD_DATA_TRANSFER -> lcdDataTransferModeUpdate()
            LcdMode.H_BLANK -> hBlankModeUpdate()
            LcdMode.V_BLANK -> vBlankModeUpdate()
        }
    }

    override fun addressInRange(address: UShort): Boolean = (address >= 0xff40u) && (address <= 0xff4bu)

    override fun write8(address: UShort, value: UByte) {
        when (address.toInt()) {
            0xff40 -> lcdc.set(value)
            0xff41 -> stat.set(value)
            0xff42 -> scrollY.set(value)
            0xff43 -> scrollX.set(value)
            0xff44 -> ly.set(0x00u)                                                                                     // Writing to ly resets it too 0
            0xff46 -> doDMATransfer(value)
            0xff47 -> bgp.set(value)
            0xff48 -> spp0.set(value)
            0xff49 -> spp1.set(value)
            0xff4a -> windowY.set(value)
            0xff4b -> windowX.set(value)
            0xff45 -> lyc.set(value)
        }
    }

    override fun read8(address: UShort): UByte {
        return when (address.toInt()) {
            0xff40 -> lcdc.getUnsigned()
            0xff41 -> stat.getUnsigned()
            0xff42 -> scrollY.getUnsigned()
            0xff43 -> scrollX.getUnsigned()
            0xff44 -> ly.getUnsigned()
            0xff47 -> bgp.getUnsigned()
            0xff48 -> spp0.getUnsigned()
            0xff49 -> spp1.getUnsigned()
            0xff4a -> windowY.getUnsigned()
            0xff4b -> windowX.getUnsigned()
            else -> 0x00u
        }
    }

    private fun doDMATransfer(value: UByte) {                                                                           // Do DMA instantly for now (http://www.codeslinger.co.uk/pages/projects/gameboy/dma.html)
        val address = value.toUInt() shl 8                                                                              // source address is data * 100
        for (i in 0..0xa0u.toInt()) {
            val destAddress = (0xfe00u + i.toUInt()).toUShort()
            val srcAddress = (address + i.toUInt()).toUShort()
            bus.write8(destAddress, bus.read8Unsigned(srcAddress))
        }
    }

    private fun drawScanLine(scanLine: Int) {
        if (lcdControlHelper.backgroundEnabled()) { renderBackgroundScanline(scanLine) }
        if (lcdControlHelper.spriteEnabled()) { renderSpriteScanline(scanLine) }
    }

    private fun renderBackgroundScanline(screenYPixel: Int) {
        val usingWindow = lcdControlHelper.isWindowEnabled() && (windowY.getUnsigned().toInt() <= screenYPixel)

        val tileSetBaseAddress = getTileSetBaseAddress().toUShort()                                                     // select the tile set address
        val tileMapBaseAddress = getTileMapBaseAddress(usingWindow).toUShort()                                          // select the tile map / nametable

        val tileMapYPixel = calculateTileMapYPosition(usingWindow, screenYPixel)                                        // Calculate the y position of the current scanline in the tile map
        val tileMapYTile = (tileMapYPixel / TILE_SIZE_PX)

        for (screenXPixel in 0 until LCD.SCREEN_WIDTH_PIXELS) {                                                         // for each pixel in this scan line
            val tileMapXPixel = calculateTileMapXPosition(usingWindow, screenXPixel)
            val tileMapXTile = (tileMapXPixel / TILE_SIZE_PX)                                                           // which of the 32 horizontal tiles does this xPos fall within?

            val tileDataAddress = calculateTileDataAddress(tileMapXTile, tileMapYTile, tileSetBaseAddress, tileMapBaseAddress)
            val tileYPixel = tileMapYPixel % 8                                                                          // calc which line in the tile are we drawing
            var tileXPixel = tileMapXPixel % 8                                                                          // pixel 0 in the tile is in bit 7 of data 1 and data2.

            val pixelColour = getTilePixelColour(tileXPixel, tileYPixel, tileDataAddress)                               // now we have the colour id get the actual colour from palette 0xFF47
            display.drawPixel(screenXPixel, screenYPixel, pixelColour)
        }
    }

    private fun renderSpriteScanline(scanLine: Int) {
        val useDoubleHeightSprites = lcdControlHelper.useLargeSprites()

        for (sprite in 0 until TOTAL_SPRITES) {
            val spriteYPositionPixel = (getSpriteAttribute(sprite, SpriteAttribute.Y_POSITION) - SPRITE_HEIGHT_LARGE.toUInt()).toInt()
            val spriteXPositionPixel = (getSpriteAttribute(sprite, SpriteAttribute.X_POSITION) - SPRITE_WIDTH.toUInt()).toInt()
            val tileOffset = getSpriteAttribute(sprite, SpriteAttribute.TILE_INDEX).toInt()
            val attributes = getSpriteAttribute(sprite, SpriteAttribute.ATTRIBUTES)

            val spriteHeightPixels = if (useDoubleHeightSprites) SPRITE_HEIGHT_LARGE else SPRITE_HEIGHT
            if (scanLine < spriteYPositionPixel || scanLine >= (spriteYPositionPixel + spriteHeightPixels))
                continue    // No need to draw sprites outside current scanLine

            val lineToRender = calculateSpriteLineToRender(scanLine, spriteYPositionPixel, isSet(attributes, BIT_SPRITE_ATTRIBUTE_VERTICAL_FLIP), spriteHeightPixels)

            val dataAddress = (TILE_SET_SPRITES_ADDRESS + tileOffset * TILE_BYTES + (lineToRender * 2)).toUShort()
            val data1 = bus.read8Unsigned(dataAddress)
            val data2 = bus.read8Unsigned((dataAddress + 0x01u).toUShort())


            for (tilePixel in 7 downTo 0) {                                                                             // Pixel 0 is bit 7, pixel 1 is bit 6, etc...
                val colourIndex = getSpritePixelColour(tilePixel, attributes, data1, data2)
                if (colourIndex == 0)
                    continue                                                                                            // colourIndex 0 is transparent, skip pixel

                val palette = if (isSet(attributes, BIT_SPRITE_ATTRIBUTE_USE_PALETTE_ONE)) spp1 else spp0               // fetch the palette
                val colour = getColour(colourIndex.toUByte(), palette.getUnsigned())

                val xPix = (0 - tilePixel) + 7
                val screenX = spriteXPositionPixel + xPix

                // TODO: This is a hack - just assuming white is the 0 background colour (Link's Awakening you can see through the sword on the beach O_O )
                if ( isSet(attributes, 7) && display.getPixel(screenX, scanLine) != LCD.Colour.WHITE)
                    continue

                display.drawPixel(screenX, scanLine, colour)
            }
        }
    }

    private fun getTileSetBaseAddress() = if (lcdControlHelper.useTileSetZero()) TILE_SET_ZERO_ADDRESS else TILE_SET_ONE_ADDRESS

    private fun getTileMapBaseAddress(window: Boolean) : UInt {
        val useTileMapZero = if (window) lcdControlHelper.useWindowMapZero() else lcdControlHelper.useBackgroundMapZero()
        return if (useTileMapZero) TILE_MAP_ZERO_ADDRESS else TILE_MAP_ONE_ADDRESS                                      // which tile map memory to use?
    }

    private fun calculateTileMapYPosition(window: Boolean, scanLine: Int) : Int {
        return if (!window)
            (scrollY.getSigned() + scanLine) and 0xff
        else
            (scanLine - windowY.getSigned()) and 0xff
    }

    private fun calculateTileMapXPosition(window: Boolean, screenXPosition: Int) : Int {
        val windowXAdjusted = (windowX.getSigned() - 7).toByte()                                                        // Not sure why, but need to offset window (http://www.codeslinger.co.uk/pages/projects/gameboy/graphics.html)

        var xPos = (screenXPosition + scrollX.getSigned()) and 0xff

        if (window) {
            if (screenXPosition >= windowXAdjusted) {
                xPos = (screenXPosition - windowXAdjusted) and 0xff
            }
        }

        return xPos
    }

    private fun calculateTileDataAddress(tileMapX: Int, tileMapY: Int, tileSetAddress: UShort, tileMapBaseAddress: UShort) : UInt {
        val tileIndex = (tileMapY * TILE_MAP_SIZE_TILES) + tileMapX                                                     // calculate the index of the tile in the tile map based on the x and y position
        val tileMapTileAddress = (tileMapBaseAddress + tileIndex.toUInt()).toUShort()                                   // calculate the address of the tile in the tile map by offsetting index by the tile map address

        return if (lcdControlHelper.useTileSetZero())                                                                   // calculate the tile data address in tile set (depending on which tile set, we should treat the tile mpa value as signed or unsigned)
            tileSetAddress + (bus.read8Unsigned(tileMapTileAddress) * TILE_BYTES.toUShort())                            // unsigned index
        else
            tileSetAddress + ((bus.read8Signed(tileMapTileAddress) + 128) * TILE_BYTES).toUShort()                      // signed index
    }

    private fun getTilePixelColour(x: Int, y: Int, baseAddress: UInt) : LCD.Colour {
        val line = (y * 2).toUInt()                                                                        // each vertical line takes up two bytes of memory

        val data1 = bus.read8Unsigned((baseAddress + line).toUShort())                                          // fetch byte 1 for this line of the tile
        val data2 = bus.read8Unsigned((baseAddress + line + 1u).toUShort())                                     // fetch byte 2 for this line of the tile

        val colourBit = (x - 7) * -1

        var colour = if (isSet(data2, colourBit)) 1 else 0
        colour = colour shl 1

        val data1Val = if (isSet(data1, colourBit)) 1 else 0
        colour = colour or data1Val

        return getColour(colour.toUByte(), bgp.getUnsigned())
    }

    private fun getColour(colourNum: UByte, palette: UByte) : LCD.Colour {
        var hi = 0
        var lo = 0

        // which bits of the colour palette does the colour id map to?
        when (colourNum) {
            0x00u.toUByte() -> { hi = 1; lo = 0 }
            0x01u.toUByte() -> { hi = 3; lo = 2 }
            0x02u.toUByte() -> { hi = 5; lo = 4 }
            0x03u.toUByte() -> { hi = 7; lo = 6 }
        }

        var colour = 0

        if (isSet(palette, hi)) colour += 2

        if (isSet(palette, lo)) colour += 1

        // convert the colour index to a real screen colour
        return when (colour) {
            0 -> LCD.Colour.WHITE
            1 -> LCD.Colour.LIGHT_GRAY
            2 -> LCD.Colour.DARK_GRAY
            3 -> LCD.Colour.BLACK
            else -> LCD.Colour.UNKNOWN
        }
    }

    private fun getSpriteAttribute(spriteNumber: Int, attribute: SpriteAttribute) : UByte =
        bus.read8Unsigned((SPRITE_ATTRIBUTE_TABLE_ADDRESS + (spriteNumber * SPRITE_BYTES) + attribute.ordinal).toUShort())

    private fun calculateSpriteLineToRender(scanLine: Int, spriteYPosition: Int, flippedVertical: Boolean, spriteHeight: Int) =
        if (flippedVertical) (((scanLine - spriteYPosition) - spriteHeight) * -1) else (scanLine - spriteYPosition)

    private fun getSpritePixelColour(xPositionPixel: Int, attributes: UByte, data1: UByte, data2: UByte) : Int {
        val colourBit = if (isSet(attributes, BIT_SPRITE_ATTRIBUTE_HORIZONTAL_FLIP)) ((xPositionPixel - 7) * -1) else xPositionPixel

        // same as for tiles
        var colour = getBitValue(data2, colourBit)
        colour = colour shl 1
        colour = colour or getBitValue(data1, colourBit)

        return colour
    }

    private fun spriteAttributeModeUpdate() {
        if (cycleCounter >= CLOCKS_PER_SCANLINE_OAM) {
            cycleCounter %= CLOCKS_PER_SCANLINE_OAM
            stat.set(setBit(stat.getUnsigned(),1,true))
            stat.set(setBit(stat.getUnsigned(),0,true))
            currentMode = LcdMode.LCD_DATA_TRANSFER
        }
    }

    private fun lcdDataTransferModeUpdate() {
        if (cycleCounter >= CLOCKS_PER_SCANLINE_VRAM) {
            cycleCounter %= CLOCKS_PER_SCANLINE_VRAM
            currentMode = LcdMode.H_BLANK

            val hBlankInterrupt = isSet(stat.getUnsigned(), 3)

            if (hBlankInterrupt) {
                interruptManager.requestInterrupt(InterruptManager.InterruptType.LCD)
            }

            val lyCoincidenceInterrupt = isSet(stat.getUnsigned(), 6)
            val lyCoincidence = (lyc.getUnsigned() == ly.getUnsigned())

            if (lyCoincidenceInterrupt && lyCoincidence) {
                interruptManager.requestInterrupt(InterruptManager.InterruptType.LCD)
            }

            stat.set(setBit(stat.getUnsigned(),2, lyCoincidence))
            stat.set(setBit(stat.getUnsigned(),1,false))
            stat.set(setBit(stat.getUnsigned(),0,false))
        }
    }

    private fun hBlankModeUpdate() {
        if (cycleCounter >= CLOCKS_PER_HBLANK) {

            drawScanLine(ly.getUnsigned().toInt())                                                                      // Render the current scanline in hBlank
            increment(ly)                                                                                               // Increment ly after the current line is drawn

            cycleCounter %= CLOCKS_PER_HBLANK

            if (ly.getUnsigned().toInt() == LCD.SCREEN_HEIGHT_PIXELS) {
                currentMode = LcdMode.V_BLANK
                stat.set(setBit(stat.getUnsigned(),1,false))
                stat.set(setBit(stat.getUnsigned(),0,true))
                interruptManager.requestInterrupt(InterruptManager.InterruptType.V_BLANK)
            } else {
                stat.set(setBit(stat.getUnsigned(),1,true))
                stat.set(setBit(stat.getUnsigned(),0,false))
                currentMode = LcdMode.SEARCH_OAM_TABLE
            }
        }
    }

    private fun vBlankModeUpdate() {
        if (cycleCounter >= CLOCKS_PER_SCANLINE) {
            increment(ly)

            cycleCounter %= CLOCKS_PER_SCANLINE

            if (ly.getUnsigned().toInt() == 154) {
                display.frameComplete()                                                                                 // flip the display buffer to show the rendered frame
                ly.set(0x00u)
                currentMode = LcdMode.SEARCH_OAM_TABLE
                stat.set(setBit(stat.getUnsigned(),1,true))
                stat.set(setBit(stat.getUnsigned(),0,false))
            }
        }
    }

    // Debug Value
    fun getLCDC() = lcdc.getUnsigned()
    fun getLY() = ly.getUnsigned()
    fun getLYC() = lyc.getUnsigned()
    fun getScrollY() = scrollY.getUnsigned()
    fun getScrollX() = scrollX.getUnsigned()
}