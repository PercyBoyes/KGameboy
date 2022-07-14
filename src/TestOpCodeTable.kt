import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.CPU
import com.percy.kgameboy.cpu.DefaultRegister16
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.cpu.InterruptManager
import com.percy.kgameboy.cpu.instructions.CBPrefix
import com.percy.kgameboy.utils.Debugger
import com.percy.kgameboy.utils.SystemLogger
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import javax.swing.JFrame
import javax.swing.JPanel

fun main(args: Array<String>) {
    TestOpCodeTable(CPU(DefaultBusImpl(ByteArray(255), SystemLogger()), SystemLogger(), Debugger(), InterruptManager((SystemLogger()))))
}

class TestOpCodeTable(cpu: CPU) : JFrame(), WindowListener {

    private val displayPanel = OpCodePanel(cpu)

    init {
        addWindowListener(this)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        title = "OP Codes"
        displayPanel.preferredSize = Dimension(1280, 600)
        add(displayPanel)
        pack()
        isFocusable = true
        isVisible = true
    }

    override fun windowDeiconified(e: WindowEvent?) {}
    override fun windowClosing(e: WindowEvent?) {}
    override fun windowClosed(e: WindowEvent?) {}
    override fun windowActivated(e: WindowEvent?) {}
    override fun windowDeactivated(e: WindowEvent?) {}
    override fun windowOpened(e: WindowEvent?) {}
    override fun windowIconified(e: WindowEvent?) {}
}

class OpCodePanel(private val cpu: CPU) : JPanel() {
    override fun paint(g: Graphics?) {
        super.paint(g)

        g?.let {
            drawOpCodes(g)
        }
    }

    private fun drawOpCodes(g: Graphics) {
        val cellWidth = 80
        val cellHeight = 40

        val cb = CBPrefix(
            Register8("A"), Register8("B"), Register8("C"), Register8("D"),
            Register8("E"), Register8("F"), Register8("H"), Register8("L"),
            DefaultRegister16("HL"), DefaultBusImpl(ByteArray(255), SystemLogger()), Flags(Register8("")), SystemLogger())

        for(opCode in 0x00u..0xffu) {
            val i = cpu.getInstruction(opCode.toUByte())

            i?.let {
                val x = ((opCode % 16u) * cellWidth.toUInt()).toInt()
                val y = ((opCode / 16u) * cellHeight.toUInt()).toInt()
                g.color = Color.GREEN
                g.fillRect(x, y, x + cellWidth, y + cellHeight)
            }
        }

        g.color = Color.BLACK

        for (x in 0..16)
            g.drawLine(x * cellWidth, 0, x * cellWidth, 17 * cellHeight)

        for (y in 0..16)
            g.drawLine(0, y * cellHeight, 17 * cellWidth, y * cellHeight)

        for(opCode in 0x00u..0xffu) {
            val i = cb.getInstruction(opCode.toUByte())

            i?.let {
                val x = (opCode % 16u) * cellWidth.toUInt()
                val y = (opCode / 16u) * cellHeight.toUInt()
                g.drawString(it.name, x.toInt() + 5, y.toInt() + (cellHeight / 2))
                g.drawString("${it.length}", x.toInt() + 5, y.toInt() + (cellHeight / 2) + 15)
            }
        }
    }
}