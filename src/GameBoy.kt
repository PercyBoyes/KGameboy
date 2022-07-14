import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cartridge.Cartridge
import com.percy.kgameboy.cpu.CPU
import com.percy.kgameboy.cpu.InterruptManager
import com.percy.kgameboy.cpu.Timer
import com.percy.kgameboy.graphics.LCD
import com.percy.kgameboy.graphics.PPU
import com.percy.kgameboy.input.GamePadInputManager
import com.percy.kgameboy.serial.BlarrgsSerialPrinter
import com.percy.kgameboy.utils.Debugger
import com.percy.kgameboy.utils.ECHORam
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.toHexString
import java.io.File

class GameBoy {

    // private val cartName = "links_awakening"        // in game!
    // private val cartName = "megaman"                // in game!
    // private val cartName = "sml"                    // in game!
    // private val cartName = "tetris"                 // in game!
    private val cartName = "pokemon_blue"           // in game - sprite bug in game start sequence

    // private val cartName = "01-special"             // PASSED
    // private val cartName = "02-interrupts"          // PASSED
    // private val cartName = "03-op sp,hl"            // PASSED
    // private val cartName = "04-op r,imm"            // PASSED
    // private val cartName = "05-op rp"               // PASSED
    // private val cartName = "06-ld r,r"              // PASSED
    // private val cartName = "07-jr,jp,call,ret,rst"  // PASSED
    // private val cartName = "08-misc instrs"         // PASSED
    // private cal cartName = "09-op r,r"              // PASSED
    // private val cartName = "10-bit ops"             // PASSED
    // private val cartName = "11-op a,(hl)"           // PASSED
    // private val cartName = "cpu_instrs"             // PASSED!! Woohoo!

    // Hard code cart to load (TODO: should inject this)
    //private val cartName = "links_awakening"

    // Debug stuff external to the emulation
    private val logger = SystemLogger()
    private val loggingEnabled = false

    // System Components
    private  val lcd = LCD(logger)                                              // Holds rendered pixels
    private val interruptManager = InterruptManager(logger)                     // Handles interrupt requests
    private val timer = Timer(interruptManager, logger)                         // Tracks the timers and generates interrupts
    private val gamePad = GamePadInputManager(interruptManager, logger)         // Generate interrupts based on user input
    private val cartridge = Cartridge(loadCart(), logger)                       // Loads data from a .gb rom, identifies cart type and initialises the appropriate MBC emulation
    private val bus = DefaultBusImpl(loadBootRom(), logger)                     // Centralised read/write to the system memory map
    private val gpu = PPU(lcd, interruptManager, bus)                           // Track state of ppu registers, render tiles and sprites to lcd, generates interrupts
    private val debugger = Debugger(bus)                                        // Observes bus, allows for breakpoints etc. TODO: This is broken so removed from the main loop currently
    private val cpu = CPU(bus, logger, debugger, interruptManager)              // Decodes, executes instructions, reacts to interrupts

    private var running = false

    init {
        val echoRAM = ECHORam(bus)                                              // Echo ram view of memory map (https://rylev.github.io/DMG-01/public/book/memory_map.html)

        // Create Memory Map
        bus.addRegion(timer)
        bus.addRegion(gpu)
        bus.addRegion(interruptManager)
        bus.addRegion(cartridge)
        bus.addRegion(gamePad)
        bus.addRegion(echoRAM)

        bus.addDebugger(debugger)

        if (loggingEnabled) enableLogs()
    }

    // Screen
    fun getDisplay() = lcd.getCurrentDisplayFrame()

    // GamePad
    fun pressed(button: GamePadInputManager.Button) = gamePad.buttonPressed(button)
    fun released(button: GamePadInputManager.Button) = gamePad.buttonReleased(button)

    private fun loadBootRom() = File("./resources/dmg_boot.bin").inputStream().readBytes()

    private fun loadCart() = File("./resources/${cartName}.gb").inputStream().readBytes()

    fun run() {
        loadCartRamFromSaveFile()           // Attempt to load Save Data
        println("Initialised: ${cpu.getInstructionsImplemented()} Instructions Implemented")
        val serialPrinter = BlarrgsSerialPrinter(bus, debugger)                                 // Serial output for Blaargs CPU tests

        running = true
        while(running) {                                                                        // Emulator Run Loop
            cpu.clock()                                                                         // clock the CPU
            timer.clock()                                                                       // clock the timers

            if (cpu.lastInstructionComplete())                                                  // ppu is only updated after every instruction currently (hope to create a FIFO implementation in future)
                gpu.clockMultiple(cpu.getCurrentInstructionLengthInCycles())                    // clock forward the ppu the number of cycles in the complete instruction

            interruptManager.checkInterupts(cpu)                                                // check for interrupts generated by timers and ppu
            serialPrinter.clock()                                                               // clock out some data from the serial port
        }
    }

    fun stop() { running = false }                                                             // kill the game loop

    fun dumpCartRamToSaveFile() {
        val ramBanks = cartridge.getRamDump()
        val saveFile = File("./resources/${cartName}.save")
        saveFile.writeBytes(ByteArray(0))
        for (ramBank in ramBanks)
            saveFile.appendBytes(ramBank)
    }

    private fun loadCartRamFromSaveFile() {
        val saveFile = File("./resources/${cartName}.save")

        if (saveFile.exists()) {                        // File exists, load bytes
            println("Found Save Data: Loading")
            val bytes = saveFile.readBytes()
            val bankCount = bytes.size / 0x2000
            val ramArray = Array(bankCount) { ByteArray(0x2000) }

            var bank = 0
            while (bank < bankCount) {
                var byteIndex = 0
                while (byteIndex < 0x2000) {
                    ramArray[bank][byteIndex] = bytes[byteIndex + (0x2000 * bank)]
                    byteIndex++
                }
                bank++
            }

            cartridge.initialiseRam(ramArray)
        } else
            println("No Save Data Found")
    }

    // Debugger
    fun stepForward() = debugger.step()
    fun resumeExecution() = debugger.resume()
    fun resumeUntil(address: UShort) = debugger.resumeUntil(address)
    fun getMemoryValue(address: UShort) = bus.read8Unsigned(address)
    fun enableMemoryBreaks() = debugger.enableMemorybreaks()
    fun enableLoggingFor(component: SystemLogger.Component) = logger.enableLogging(component)

    // Logging
    private fun enableLogs() {
        enableLoggingFor(SystemLogger.Component.TIMER)
        enableLoggingFor(SystemLogger.Component.CPU)
        logger.enableLogging(SystemLogger.Component.GAME_PAD)
        logger.enableLogging(SystemLogger.Component.BUS)
        // logger.enableLogging(SystemLogger.Component.DISPLAY)
        logger.enableLogging(SystemLogger.Component.CARTRIDGE)
        enableLoggingFor(SystemLogger.Component.INTERRUPT_MANAGER)
        enableLoggingFor(SystemLogger.Component.MBC)
    }

    // Helpers for fetching system value strings

    // CPU Values
    fun getProgramCounter() = toHexString(cpu.getProgramCounter())
    fun getStackPointer() = toHexString(cpu.getStackPointer())
    fun getA() = toHexString(cpu.getA())
    fun getB() = toHexString(cpu.getB())
    fun getC() = toHexString(cpu.getC())
    fun getD() = toHexString(cpu.getD())
    fun getE() = toHexString(cpu.getE())
    fun getF() = toHexString(cpu.getF())
    fun getH() = toHexString(cpu.getH())
    fun getL() = toHexString(cpu.getL())
    fun getAF() = toHexString(cpu.getAF())
    fun getBC() = toHexString(cpu.getBC())
    fun getDE() = toHexString(cpu.getDE())
    fun getHL() = toHexString(cpu.getHL())
    fun getZeroFlag() = cpu.isZeroFlagSet()
    fun getCarryFlag() = cpu.isCarryFlagSet()
    fun getHalfCarryFlag() = cpu.isHalfCarryFlagSet()
    fun getSubtractionFlag() = cpu.isSubtractFlagSet()
    fun areInterruptsEnabled() = cpu.interruptsEnabled()
    fun isHalted() = cpu.isHalted()

    // GPU Values
    fun getLCDC() = toHexString(gpu.getLCDC())
    fun getLY() = toHexString(gpu.getLY())
    fun getLYC() = toHexString(gpu.getLYC())
    fun getScrollY() = toHexString(gpu.getScrollY())
    fun getScrollX() = toHexString(gpu.getScrollX())

    // Timer
    fun getTIMA() = toHexString(timer.getTima())
    fun getDIV() = toHexString(timer.getDiv())
    fun getTMA() = toHexString(timer.getTma())
    fun getTMC() = toHexString(timer.getTmc())
}