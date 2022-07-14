import com.percy.kgameboy.graphics.LCD
import com.percy.kgameboy.input.GamePadInputManager
import com.percy.kgameboy.graphics.getRealColour
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.toHexString
import java.awt.*
import java.awt.Font.PLAIN
import java.awt.Font.BOLD
import java.awt.event.*
import javax.swing.*
import javax.swing.Timer
import kotlin.system.exitProcess


class TestWindow(title: String, private val gameboy: GameBoy) : JFrame(), WindowListener {
    private val displayPanel = SystemPanel(gameboy)

    init {
        addWindowListener(this)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setTitle(title)
        displayPanel.preferredSize = Dimension(1500, 1300)
        displayPanel.background = Color.CYAN
        layout = BorderLayout()
        add(displayPanel, BorderLayout.CENTER)
        add(ButtonPanel(gameboy), BorderLayout.LINE_END)
        pack()
        this.addKeyListener(GamePadInputHandler(gameboy))
        isFocusable = true
        isVisible = true
    }

    private class GamePadInputHandler(private val gameboy: GameBoy) : KeyListener {
        private fun handleInput(event: KeyEvent, pressed: Boolean) {
            val button = when (event.keyCode) {
                KeyEvent.VK_W -> GamePadInputManager.Button.JOYPAD_UP
                KeyEvent.VK_A -> GamePadInputManager.Button.JOYPAD_LEFT
                KeyEvent.VK_S -> GamePadInputManager.Button.JOYPAD_DOWN
                KeyEvent.VK_D -> GamePadInputManager.Button.JOYPAD_RIGHT
                KeyEvent.VK_K -> GamePadInputManager.Button.JOYPAD_A
                KeyEvent.VK_L -> GamePadInputManager.Button.JOYPAD_B
                KeyEvent.VK_T -> GamePadInputManager.Button.JOYPAD_SELECT
                KeyEvent.VK_Y -> GamePadInputManager.Button.JOYPAD_START
                else -> return
            }

            if (pressed) gameboy.pressed(button) else gameboy.released(button)
        }

        override fun keyTyped(p0: KeyEvent?) {}

        override fun keyPressed(e: KeyEvent?) {
            e?.let {
                handleInput(e, true)
            }
        }

        override fun keyReleased(e: KeyEvent?) {
            e?.let {
                handleInput(e, false)
            }
        }
    }

    override fun windowClosed(e: WindowEvent?) {
        gameboy.stop()
    }

    override fun windowDeiconified(e: WindowEvent?) {}
    override fun windowClosing(e: WindowEvent?) {}
    override fun windowActivated(e: WindowEvent?) {}
    override fun windowDeactivated(e: WindowEvent?) {}
    override fun windowOpened(e: WindowEvent?) {}
    override fun windowIconified(e: WindowEvent?) {}
}

class ButtonPanel(private val system: GameBoy) : JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        val stepButton = JButton("Step")
        stepButton.preferredSize = Dimension(100, 40)
        stepButton.addActionListener { system.stepForward() }
        add(stepButton)
        val jumpForwardButton = JButton("Jump Forward")
        jumpForwardButton.preferredSize = Dimension(100, 40)
        jumpForwardButton.addActionListener { system.resumeUntil(promptUserForAddress("What's the address to pause at?")) }
        add(jumpForwardButton)
        val continueButton = JButton("Run")
        continueButton.preferredSize = Dimension(100, 40)
        continueButton.addActionListener { system.resumeExecution() }
        add(continueButton)
        val printMemoryButton = JButton("Print Memory Value")
        printMemoryButton.preferredSize = Dimension(100, 40)
        printMemoryButton.addActionListener {
            val address = promptUserForAddress("Print memory value at address:")
            val value = system.getMemoryValue(address)
            println("Address: ${ toHexString(address)},  Memory: ${toHexString(value)}")}
        add(printMemoryButton)

        val enableMemBreakPoints = JButton("Enabled Mem Breaks")
        enableMemBreakPoints.preferredSize = Dimension(100, 40)
        enableMemBreakPoints.addActionListener {
            system.enableMemoryBreaks()
        }
        add(enableMemBreakPoints)
        val saveCartridgeRam = JButton("SaveCartRam")
        saveCartridgeRam.preferredSize = Dimension(100, 40)
        saveCartridgeRam.addActionListener {
            system.dumpCartRamToSaveFile()
        }
        add(saveCartridgeRam)
    }

    private fun promptUserForAddress(message: String) : UShort {
        val input = JOptionPane.showInputDialog(null, message)
        return Integer.parseInt(input, 16).toUShort()
    }
}


class SystemPanel(private val systemState: GameBoy) : JPanel() {

    private var frame = 0

    private val timer: Timer = Timer(20, ActionListener { repaint() })
    private val testFont = Font("Consolas", PLAIN, 18)
    private val testFontBold = Font("Consolas", BOLD, 18)

    init {
        timer.isRepeats = true
        timer.delay = 20        // 50 FPS
        timer.start()
    }

    private fun handleException(e: Throwable) {
        e.printStackTrace()
        exitProcess(0)
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        frame++

        g?.let {
            it.color = Color.black
            it.drawString("The Frame is: $frame, The Size is: $width, $height", 20, 20)
            paintScreen(g)
            paintDebugPanel(it)
            paintStack(it)
            paintVRAM(it)
        }
    }

    private fun paintStack(g: Graphics) {
        val debugPanelStart = size.width - 280

        g.font = testFontBold
        g.drawString("FLAGS", debugPanelStart + 20, 40)
        g.font = testFont

        g.drawString("ZERO FLAG:        ${systemState.getZeroFlag()}", debugPanelStart + 20, 60)
        g.drawString("SUBTRACT FLAG:    ${systemState.getSubtractionFlag()}", debugPanelStart + 20, 80)
        g.drawString("HALF CARRY FLAG:  ${systemState.getHalfCarryFlag()}", debugPanelStart + 20, 100)
        g.drawString("CARRY FLAG:       ${systemState.getCarryFlag()}", debugPanelStart + 20, 120)
        g.drawString("INTERRUPT ENABLE: ${systemState.areInterruptsEnabled()}", debugPanelStart + 20, 140)
        g.drawString("CPU HALTED:       ${systemState.isHalted()}", debugPanelStart + 20, 160)

        g.drawString("WATCH POINTS", debugPanelStart + 20, 240)
        g.drawString("FF80:            ${toHexString(systemState.getMemoryValue(0xff80u))}", debugPanelStart + 20, 260)
        g.drawString("DF7F:            ${toHexString(systemState.getMemoryValue(0xdf7fu))}", debugPanelStart + 20, 280)
        g.drawString("FF0F:            ${toHexString(systemState.getMemoryValue(0xff0fu))}", debugPanelStart + 20, 300)
    }

    private fun paintScreen(g: Graphics) {
        val display = systemState.getDisplay()
        val pixelSize = 5

        val offsetX = 32
        val offsetY = 32

        for (y in 0..143) {
            for (x in 0..159) {
                g.color = getColour(display[(y * 160) + x])
                g.fillRect(offsetX + x * pixelSize, offsetY + y * pixelSize, pixelSize, pixelSize)
            }
        }
    }

    private fun paintVRAMRegion(memStart: Int, renderingOffsetX: Int, renderingOffsetY:Int, palette: Int, pixelSize: Int, tileRowSize: Int, g: Graphics) {
        val tileSize = pixelSize * 8

        for (tileIndex in 0..127) {
            val tileX = renderingOffsetX + ((tileIndex % tileRowSize) * tileSize)
            val tileY = renderingOffsetY + ((tileIndex / tileRowSize) * tileSize)

            var row = 0
            while (row < 8)
            {
                val byte0 = systemState.getMemoryValue((memStart + (tileIndex * 16) + (row * 2)).toUShort())
                val byte1 = systemState.getMemoryValue((memStart + (tileIndex * 16) + (row * 2) + 1).toUShort())

                var column = 0
                while (column < 8) {
                    var colourBit = column
                    colourBit -= 7
                    colourBit *= -1

                    // combine data 2 and data 1 to get the colour id for this pixel
                    // in the tile
                    var colourNum = if(isSet(byte1,colourBit)) 1 else 0
                    colourNum = colourNum shl 1

                    val data1Val = (if(isSet(byte0,colourBit)) 1 else 0)
                    colourNum = colourNum or data1Val

                    g.color = getColour(getRealColour(palette, colourNum))
                    g.fillRect(tileX + (column * pixelSize), tileY + (row * pixelSize), pixelSize, pixelSize)

                    column++
                }
                row++
            }
        }
    }

    private fun paintVRAM(g: Graphics) {

        val tileRowSize = 32

        // 0x8000-0x87ff
        val pixelSize = 4

        val bgp = systemState.getMemoryValue(0xff47u).toInt()

        var memOffset = 0x8000

        val staticYOffset = 800

        g.font = testFontBold
        g.color = Color.black
        g.drawString("VRAM TILE DATA REGION: ${toHexString(0x8000u)}", 32, staticYOffset - 15)
        paintVRAMRegion(0x8000, 32, staticYOffset, bgp, pixelSize, tileRowSize, g)

        g.color = Color.black
        g.drawString("VRAM TILE DATA REGION: ${toHexString(0x8800u)}", 32, (staticYOffset + (160 / tileRowSize) * 8 * pixelSize) - 15)
        paintVRAMRegion(0x8800, 32, staticYOffset + (160 / tileRowSize) * 8 * pixelSize, bgp, pixelSize, tileRowSize, g)

        g.color = Color.black
        g.drawString("VRAM TILE DATA REGION: ${toHexString(0x9000u)}", 32, (staticYOffset + (320 / tileRowSize) * 8 * pixelSize) - 15)
        paintVRAMRegion(0x9000, 32, staticYOffset + (320 / tileRowSize) * 8 * pixelSize, bgp, pixelSize, tileRowSize, g)
    }

    private val black = Color(15, 56, 15)
    private val darkGrey = Color(48, 98, 48)
    private val lightGrey = Color(139, 172, 15)
    private val white = Color(155, 188, 15)
    private val off = Color(170, 170, 170)

    private fun getColour(c: LCD.Colour) : Color {
        return when(c) {
            LCD.Colour.WHITE -> white
            LCD.Colour.LIGHT_GRAY -> lightGrey
            LCD.Colour.DARK_GRAY -> darkGrey
            LCD.Colour.BLACK -> black
            else -> off
        }
    }

    private fun paintDebugPanel(g: Graphics) {
        val debugPanelStart = size.width - 500

        g.color = Color.yellow
        g.fillRect(debugPanelStart, 0, size.width, size.height)

        g.color = Color.black
        g.font = testFontBold
        g.drawString("CPU REGISTERS", debugPanelStart + 20, 40)
        g.font = testFont
        g.drawString(registerValueText("A", systemState.getA()), debugPanelStart + 20, 60)
        g.drawString(registerValueText("B", systemState.getB()), debugPanelStart + 20, 80)
        g.drawString(registerValueText("C", systemState.getC()), debugPanelStart + 20, 100)
        g.drawString(registerValueText("D", systemState.getD()), debugPanelStart + 20, 120)
        g.drawString(registerValueText("E", systemState.getE()), debugPanelStart + 20, 140)
        g.drawString(registerValueText("F", systemState.getF()), debugPanelStart + 20, 160)
        g.drawString(registerValueText("H", systemState.getH()), debugPanelStart + 20, 180)
        g.drawString(registerValueText("L", systemState.getL()), debugPanelStart + 20, 200)
        g.drawString(registerValueText("AF", systemState.getAF()), debugPanelStart + 20, 220)
        g.drawString(registerValueText("BC", systemState.getBC()), debugPanelStart + 20, 240)
        g.drawString(registerValueText("DE", systemState.getDE()), debugPanelStart + 20, 260)
        g.drawString(registerValueText("HL", systemState.getHL()), debugPanelStart + 20, 280)
        g.drawString(registerValueText("SP", systemState.getStackPointer()), debugPanelStart + 20, 300)
        g.drawString(registerValueText("PC", systemState.getProgramCounter()), debugPanelStart + 20, 320)

        g.font = testFontBold
        g.drawString("PPU", debugPanelStart + 20, 360)
        g.font = testFont
        g.drawString(registerValueText("LCDC", systemState.getLCDC()), debugPanelStart + 20, 380)
        g.drawString(registerValueText("LY", systemState.getLY()), debugPanelStart + 20, 400)
        g.drawString(registerValueText("LYC", systemState.getLYC()), debugPanelStart + 20, 420)
        g.drawString(registerValueText("SCROLLY", systemState.getScrollY()), debugPanelStart + 20, 440)
        g.drawString(registerValueText("SCROLLX", systemState.getScrollX()), debugPanelStart + 20, 460)

        g.font = testFontBold
        g.drawString("TIMER", debugPanelStart + 20, 600)
        g.font = testFont
        g.drawString(registerValueText("DIV", systemState.getDIV()), debugPanelStart + 20, 620)
        g.drawString(registerValueText("TMC", systemState.getTMC()), debugPanelStart + 20, 640)
        g.drawString(registerValueText("TIMA", systemState.getTIMA()), debugPanelStart + 20, 660)
        g.drawString(registerValueText("TMA", systemState.getTMA()), debugPanelStart + 20, 680)
    }

    private fun registerValueText(regName: String, regValue: String) : String {
        return if (regName.length == 2 && regValue.length == 6)
            "$regName:     $regValue"
        else if (regName.length == 2 && regValue.length == 4)
            "$regName:       $regValue"
        else if (regName.length == 3 && regValue.length == 4)
            "$regName:      $regValue"
        else if (regName.length == 4 && regValue.length == 4)
            "$regName:     $regValue"
        else if (regName.length == 7 && regValue.length == 4)
            "$regName:  $regValue"
        else
            "$regName:        $regValue"
    }
}