package com.percy.kgameboy.cpu

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.cpu.instructions.*
import com.percy.kgameboy.utils.*

class CPU(private val bus: DefaultBusImpl, private val logger: SystemLogger,
          private val debugger: Debugger, private val interruptManager: InterruptManager
) {
    // 16 Bit registers
    private val pc : Register16 = DefaultRegister16("PC")
    private val sp : Register16 = DefaultRegister16("SP")

    // 8 Bit registers
    private val a = Register8("A")
    private val b = Register8("B")
    private val c = Register8("C")
    private val d = Register8("D")
    private val e = Register8("E")
    private val f = Register8("F", 0xf0u)
    private val h = Register8("H")
    private val l = Register8("L")

    // 16 Bit virtual registers
    private val af : Register16 = VirtualRegister16(a, f, "AF")
    private val bc : Register16 = VirtualRegister16(b, c, "BC")
    private val de : Register16 = VirtualRegister16(d, e, "DE")
    private val hl : Register16 = VirtualRegister16(h, l, "HL")

    // Helper view for access to flags
    private val flags = Flags(f)

    // Helper view for access to the Stack
    private val stack = Stack(sp, bus)

    private val opCodes : Map<UByte,Instruction> = mapOf(
        0x00u.toUByte() to NOP(),
        0x01u.toUByte() to LDrrd16(bc, 0x01u),
        0x02u.toUByte() to LDrrA(bc, a, bus, 0x02u, PostOperation.NO_POST_MODIFICATION),
        0x03u.toUByte() to INCrr(bc, 0x03u),
        0x04u.toUByte() to INCr(b, flags, 0x04u),
        0x05u.toUByte() to DECr(b, flags, 0x05u),
        0x06u.toUByte() to LDrd8(b, 0x06u),
        0x07u.toUByte() to RLCA(a, flags, 0x07u),
        0x08u.toUByte() to LDa16SP(sp, bus, 0x08u),
        0x09u.toUByte() to ADDhlr16(hl, bc, flags, 0x09u),
        0x0au.toUByte() to LDArr(a, bc, bus, 0x0au, PostOperation.NO_POST_MODIFICATION),
        0x0bu.toUByte() to DECrr(bc, 0x0bu),
        0x0cu.toUByte() to INCr(c, flags, 0x0cu),
        0x0du.toUByte() to DECr(c, flags, 0x0du),
        0x0eu.toUByte() to LDrd8(c, 0x0eu),
        0x0fu.toUByte() to RRCA(a, flags, 0x0fu),
        0x10u.toUByte() to STOP(this),
        0x11u.toUByte() to LDrrd16(de, 0x11u),
        0x12u.toUByte() to LDrrA(de, a, bus, 0x12u, PostOperation.NO_POST_MODIFICATION),
        0x13u.toUByte() to INCrr(de, 0x13u),
        0x14u.toUByte() to INCr(d, flags, 0x14u),
        0x15u.toUByte() to DECr(d, flags, 0x15u),
        0x16u.toUByte() to LDrd8(d, 0x16u),
        0x17u.toUByte() to RLA(a, flags, 0x17u),
        0x18u.toUByte() to JRr8(pc, 0x18u),
        0x19u.toUByte() to ADDhlr16(hl, de, flags, 0x19u),
        0x1au.toUByte() to LDArr(a, de, bus, 0x1au, PostOperation.NO_POST_MODIFICATION),
        0x1bu.toUByte() to DECrr(de, 0x1bu),
        0x1cu.toUByte() to INCr(e, flags, 0x1cu),
        0x1du.toUByte() to DECr(e, flags, 0x1du),
        0x1eu.toUByte() to LDrd8(e, 0x1eu),
        0x1fu.toUByte() to RRA(a, flags, 0x1fu),
        0x20u.toUByte() to JRNZr8(pc, flags, 0x20u),
        0x21u.toUByte() to LDrrd16(hl, 0x21u),
        0x22u.toUByte() to LDrrA(hl, a, bus, 0x22u, PostOperation.POST_INCREMENT_REGISTER),
        0x23u.toUByte() to INCrr(hl, 0x23u),
        0x24u.toUByte() to INCr(h, flags, 0x24u),
        0x25u.toUByte() to DECr(h, flags, 0x25u),
        0x26u.toUByte() to LDrd8(h, 0x26u),
        0x27u.toUByte() to DAA(a, flags, 0x27u),
        0x28u.toUByte() to JRZr8(pc, flags, 0x28u),
        0x29u.toUByte() to ADDhlr16(hl, hl, flags, 0x29u),
        0x2au.toUByte() to LDArr(a, hl, bus, 0x2au, PostOperation.POST_INCREMENT_REGISTER),
        0x2bu.toUByte() to DECrr(hl, 0x2bu),
        0x2cu.toUByte() to INCr(l, flags, 0x2cu),
        0x2du.toUByte() to DECr(l, flags, 0x2du),
        0x2eu.toUByte() to LDrd8(l, 0x2eu),
        0x2fu.toUByte() to CPL(a, flags, 0x2fu),
        0x30u.toUByte() to JRNCr8(pc, flags, 0x30u),
        0x31u.toUByte() to LDrrd16(sp, 0x31u),
        0x32u.toUByte() to LDrrA(hl, a, bus, 0x32u, PostOperation.POST_DECREMENT_REGISTER),
        0x33u.toUByte() to INCrr(sp, 0x33u),
        0x34u.toUByte() to INCahl(hl, bus, flags, 0x34u),
        0x35u.toUByte() to DECahl(hl, flags, bus, 0x35u),
        0x36u.toUByte() to LDahld8(hl, bus, 0x36u),
        0x37u.toUByte() to SCF(flags),
        0x38u.toUByte() to JRCr8(pc, flags, 0x38u),
        0x39u.toUByte() to ADDhlr16(hl, sp, flags, 0x39u),
        0x3au.toUByte() to LDArr(a, hl, bus, 0x3au, PostOperation.POST_DECREMENT_REGISTER),
        0x3bu.toUByte() to DECrr(sp, 0x3bu),
        0x3cu.toUByte() to INCr(a, flags, 0x3cu),
        0x3du.toUByte() to DECr(a, flags, 0x3du),
        0x3eu.toUByte() to LDrd8(a, 0x3eu),
        0x3fu.toUByte() to CCF(flags),
        0x40u.toUByte() to LDrr(b, b, 0x40u),
        0x41u.toUByte() to LDrr(b, c, 0x41u),
        0x42u.toUByte() to LDrr(b, d, 0x42u),
        0x43u.toUByte() to LDrr(b, e, 0x43u),
        0x44u.toUByte() to LDrr(b, h, 0x44u),
        0x45u.toUByte() to LDrr(b, l, 0x45u),
        0x46u.toUByte() to LDrahl(b, hl, bus, 0x46u),
        0x47u.toUByte() to LDrr(b, a, 0x47u),
        0x48u.toUByte() to LDrr(c, b, 0x48u),
        0x49u.toUByte() to LDrr(c, c, 0x49u),
        0x4au.toUByte() to LDrr(c, d, 0x4au),
        0x4bu.toUByte() to LDrr(c, e, 0x4bu),
        0x4cu.toUByte() to LDrr(c, h, 0x4cu),
        0x4du.toUByte() to LDrr(c, l, 0x4du),
        0x4eu.toUByte() to LDrahl(c, hl, bus, 0x4eu),
        0x4fu.toUByte() to LDrr(c, a, 0x4fu),
        0x50u.toUByte() to LDrr(d, b, 0x50u),
        0x51u.toUByte() to LDrr(d, c, 0x51u),
        0x52u.toUByte() to LDrr(d, d, 0x52u),
        0x53u.toUByte() to LDrr(d, e, 0x53u),
        0x54u.toUByte() to LDrr(d, h, 0x54u),
        0x55u.toUByte() to LDrr(d, l, 0x55u),
        0x56u.toUByte() to LDrahl(d, hl, bus, 0x56u),
        0x57u.toUByte() to LDrr(d, a, 0x57u),
        0x58u.toUByte() to LDrr(e, b, 0x58u),
        0x59u.toUByte() to LDrr(e, c, 0x59u),
        0x5au.toUByte() to LDrr(e, d, 0x5au),
        0x5bu.toUByte() to LDrr(e, e, 0x5bu),
        0x5cu.toUByte() to LDrr(e, h, 0x5cu),
        0x5du.toUByte() to LDrr(e, l, 0x5du),
        0x5eu.toUByte() to LDrahl(e, hl, bus, 0x5eu),
        0x5fu.toUByte() to LDrr(e, a, 0x5fu),
        0x60u.toUByte() to LDrr(h, b, 0x60u),
        0x61u.toUByte() to LDrr(h, c, 0x61u),
        0x62u.toUByte() to LDrr(h, d, 0x62u),
        0x63u.toUByte() to LDrr(h, e, 0x63u),
        0x64u.toUByte() to LDrr(h, h, 0x64u),
        0x65u.toUByte() to LDrr(h, l, 0x65u),
        0x66u.toUByte() to LDrahl(h, hl, bus, 0x66u),
        0x67u.toUByte() to LDrr(h, a, 0x67u),
        0x68u.toUByte() to LDrr(l, b, 0x68u),
        0x69u.toUByte() to LDrr(l, c, 0x69u),
        0x6au.toUByte() to LDrr(l, d, 0x6au),
        0x6bu.toUByte() to LDrr(l, e, 0x6bu),
        0x6cu.toUByte() to LDrr(l, h, 0x6cu),
        0x6du.toUByte() to LDrr(l, l, 0x6du),
        0x6eu.toUByte() to LDrahl(l, hl, bus, 0x6eu),
        0x6fu.toUByte() to LDrr(l, a, 0x6fu),
        0x70u.toUByte() to LDrrA(hl, b, bus, 0x70u, PostOperation.NO_POST_MODIFICATION),
        0x71u.toUByte() to LDrrA(hl, c, bus, 0x71u, PostOperation.NO_POST_MODIFICATION),
        0x72u.toUByte() to LDrrA(hl, d, bus, 0x72u, PostOperation.NO_POST_MODIFICATION),
        0x73u.toUByte() to LDrrA(hl, e, bus, 0x73u, PostOperation.NO_POST_MODIFICATION),
        0x74u.toUByte() to LDrrA(hl, h, bus, 0x74u, PostOperation.NO_POST_MODIFICATION),
        0x75u.toUByte() to LDrrA(hl, l, bus, 0x75u, PostOperation.NO_POST_MODIFICATION),
        0x76u.toUByte() to HALT(this),
        0x77u.toUByte() to LDrrA(hl, a, bus, 0x77u, PostOperation.NO_POST_MODIFICATION),
        0x78u.toUByte() to LDrr(a, b, 0x78u),
        0x79u.toUByte() to LDrr(a, c, 0x79u),
        0x7au.toUByte() to LDrr(a, d, 0x7au),
        0x7bu.toUByte() to LDrr(a, e, 0x7bu),
        0x7cu.toUByte() to LDrr(a, h, 0x7cu),
        0x7du.toUByte() to LDrr(a, l, 0x7du),
        0x7eu.toUByte() to LDrahl(a, hl, bus, 0x7eu),
        0x7fu.toUByte() to LDrr(a, a, 0x7fu),
        0x80u.toUByte() to ADDr(a, b, flags,0x80u),
        0x81u.toUByte() to ADDr(a, c, flags,0x81u),
        0x82u.toUByte() to ADDr(a, d, flags,0x82u),
        0x83u.toUByte() to ADDr(a, e, flags,0x83u),
        0x84u.toUByte() to ADDr(a, h, flags,0x84u),
        0x85u.toUByte() to ADDr(a, l, flags,0x85u),
        0x86u.toUByte() to ADDahl(a, hl, flags, bus, 0x86u),
        0x87u.toUByte() to ADDr(a, a, flags,0x87u),
        0x88u.toUByte() to ADC(a, b, flags, 0x88u),
        0x89u.toUByte() to ADC(a, c, flags, 0x89u),
        0x8au.toUByte() to ADC(a, d, flags, 0x8au),
        0x8bu.toUByte() to ADC(a, e, flags, 0x8bu),
        0x8cu.toUByte() to ADC(a, h, flags, 0x8cu),
        0x8du.toUByte() to ADC(a, l, flags, 0x8du),
        0x8eu.toUByte() to ADCahl(a, hl, flags, bus, 0x8eu),
        0x8fu.toUByte() to ADC(a, a, flags, 0x8fu),
        0x90u.toUByte() to SUBr(a, b, flags,0x90u),
        0x91u.toUByte() to SUBr(a, c, flags,0x91u),
        0x92u.toUByte() to SUBr(a, d, flags,0x92u),
        0x93u.toUByte() to SUBr(a, e, flags,0x93u),
        0x94u.toUByte() to SUBr(a, h, flags,0x94u),
        0x95u.toUByte() to SUBr(a, l, flags,0x95u),
        0x96u.toUByte() to SUBahl(a, hl, flags, bus, 0x96u),
        0x97u.toUByte() to SUBr(a, a, flags,0x97u),
        0x98u.toUByte() to SBCr8(a, b, flags, 0x98u),
        0x99u.toUByte() to SBCr8(a, c, flags, 0x99u),
        0x9au.toUByte() to SBCr8(a, d, flags, 0x9au),
        0x9bu.toUByte() to SBCr8(a, e, flags, 0x9bu),
        0x9cu.toUByte() to SBCr8(a, h, flags, 0x9cu),
        0x9du.toUByte() to SBCr8(a, l, flags, 0x9du),
        0x9eu.toUByte() to SBCahl(a, hl, flags, bus, 0x9eu),
        0x9fu.toUByte() to SBCr8(a, a, flags, 0x9fu),
        0xa0u.toUByte() to ANDr(a, b, flags, 0xa0u),
        0xa1u.toUByte() to ANDr(a, c, flags, 0xa1u),
        0xa2u.toUByte() to ANDr(a, d, flags, 0xa2u),
        0xa3u.toUByte() to ANDr(a, e, flags, 0xa3u),
        0xa4u.toUByte() to ANDr(a, h, flags, 0xa4u),
        0xa5u.toUByte() to ANDr(a, l, flags, 0xa5u),
        0xa6u.toUByte() to ANDahl(a, hl, flags, bus, 0xa6u),
        0xa7u.toUByte() to ANDr(a, a, flags, 0xa7u),
        0xa8u.toUByte() to XORr(b, a, flags,0xa8u),
        0xa9u.toUByte() to XORr(c, a, flags,0xa9u),
        0xaau.toUByte() to XORr(d, a, flags,0xaau),
        0xabu.toUByte() to XORr(e, a, flags,0xabu),
        0xacu.toUByte() to XORr(h, a, flags,0xacu),
        0xadu.toUByte() to XORr(l, a, flags,0xadu),
        0xaeu.toUByte() to XORahl(hl, a, flags, bus, 0xaeu),
        0xafu.toUByte() to XORr(a, a, flags,0xafu),
        0xb0u.toUByte() to ORr(a, b, flags, 0xb0u),
        0xb1u.toUByte() to ORr(a, c, flags, 0xb1u),
        0xb2u.toUByte() to ORr(a, d, flags, 0xb2u),
        0xb3u.toUByte() to ORr(a, e, flags, 0xb3u),
        0xb4u.toUByte() to ORr(a, h, flags, 0xb4u),
        0xb5u.toUByte() to ORr(a, l, flags, 0xb5u),
        0xb6u.toUByte() to ORahl(a, hl, flags, bus,0xb6u),
        0xb7u.toUByte() to ORr(a, a, flags, 0xb7u),
        0xb8u.toUByte() to CPr(a, b, flags, 0xb8u),
        0xb9u.toUByte() to CPr(a, c, flags, 0xb9u),
        0xbau.toUByte() to CPr(a, d, flags, 0xbau),
        0xbbu.toUByte() to CPr(a, e, flags, 0xbbu),
        0xbcu.toUByte() to CPr(a, h, flags, 0xbcu),
        0xbdu.toUByte() to CPr(a, l, flags, 0xbdu),
        0xbeu.toUByte() to CPahl(a, hl, flags, bus, 0xbeu),
        0xbfu.toUByte() to CPr(a, a, flags, 0xbfu),
        0xc0u.toUByte() to RETNZ(pc, stack, flags, 0xc0u),
        0xc1u.toUByte() to POPr16(bc, stack, 0xc1u),
        0xc2u.toUByte() to JPNZa16(pc, flags, 0xc2u),
        0xc3u.toUByte() to JPa16(pc, 0xc3u),
        0xc4u.toUByte() to CALLNZa16(pc, stack, flags, 0xc4u),
        0xc5u.toUByte() to PUSHr16(bc, stack, 0xc5u),
        0xc6u.toUByte() to ADDd8(a, flags, 0xc6u),
        0xc7u.toUByte() to RSTvec(pc, stack, 0x0000u, 0xc7u),
        0xc8u.toUByte() to RETZ(pc, stack, flags, 0xc8u),
        0xc9u.toUByte() to RET(pc, stack, 0xc9u),
        0xcau.toUByte() to JPZa16(pc, flags, 0xcau),
        0xcbu.toUByte() to CBPrefix(a, b, c, d, e, f, h, l, hl, bus, flags, logger),
        0xccu.toUByte() to CALLZa16(pc, stack, flags, 0xccu),
        0xcdu.toUByte() to CALLa16(pc, stack, 0xcdu),
        0xceu.toUByte() to ADCd8(a, flags, 0xceu),
        0xcfu.toUByte() to RSTvec(pc, stack, 0x0008u, 0xcfu),
        0xd0u.toUByte() to RETNC(pc, stack, flags, 0xd0u),
        0xd1u.toUByte() to POPr16(de, stack, 0xd1u),
        0xd2u.toUByte() to JPNCa16(pc, flags, 0xd2u),
        0xd4u.toUByte() to CALLNCa16(pc, stack, flags, 0xd4u),
        0xd7u.toUByte() to RSTvec(pc, stack, 0x0010u, 0xd7u),
        0xd8u.toUByte() to RETC(pc, stack, flags, 0xd8u),
        0xd9u.toUByte() to RETI(pc, stack, this, 0xd9u),
        0xd5u.toUByte() to PUSHr16(de, stack, 0xd5u),
        0xd6u.toUByte() to SUBd8(a, flags, 0xd6u),
        0xdau.toUByte() to JPCa16(pc, flags, 0xcau),
        0xdcu.toUByte() to CALLCa16(pc, stack, flags, 0xdcu),
        0xdeu.toUByte() to SBCd8(a, flags, 0xdeu),
        0xdfu.toUByte() to RSTvec(pc, stack, 0x0018u, 0xdfu),
        0xe0u.toUByte() to LDa8A(a, bus, 0xe0u),
        0xe1u.toUByte() to POPr16(hl, stack, 0xe1u),
        0xe2u.toUByte() to LDrA(c, a, bus, 0xe2u),
        0xe5u.toUByte() to PUSHr16(hl, stack, 0xe5u),
        0xe6u.toUByte() to ANDd8(a, flags, 0xe6u),
        0xe7u.toUByte() to RSTvec(pc, stack, 0x0020u, 0xe7u),
        0xe8u.toUByte() to ADDspr8(sp, flags),
        0xe9u.toUByte() to JPhl(pc, hl, 0x9eu),
        0xeau.toUByte() to LDa16A(a, bus, 0xeau),
        0xeeu.toUByte() to XORd8(a, flags, 0xeeu),
        0xefu.toUByte() to RSTvec(pc, stack, 0x0028u, 0xefu),
        0xf0u.toUByte() to LDAa8(a, bus, 0xf0u),
        0xf1u.toUByte() to POPAF(af, stack, 0xf1u),
        0xf2u.toUByte() to LDAr(c, a, bus, 0xf2u),
        0xf3u.toUByte() to DI(this),
        0xf5u.toUByte() to PUSHr16(af, stack, 0xf5u),
        0xf6u.toUByte() to ORd8(a, flags, 0xf6u),
        0xf7u.toUByte() to RSTvec(pc, stack, 0x0030u, 0xf7u),
        0xf8u.toUByte() to LDhlSPr8(sp, hl, flags,0xf8u),
        0xf9u.toUByte() to LDsphl(sp, hl, 0xf9u),
        0xfau.toUByte() to LDAa16(a, bus, 0xfau),
        0xfbu.toUByte() to EI(this),
        0xfeu.toUByte() to CPd8(a, flags, 0xfeu),
        0xffu.toUByte() to RSTvec(pc, stack, 0x0038u, 0xffu)
    )

    private var halted = false
    private var halting = false

    private var interruptsEnabled = false
    private var remainingCyclesInCurrentInstruction = 0
    private var totalCyclesInCurrentInstruction = 0
    private val operand = UByteArray(2)
    private var cycles = 0
    private var currentInstruction : Instruction = InvalidInstruction(0x00u)

    fun clock() {
        if (halted) return                                                              // Do nothing if CPU has been halted

        if (halting && remainingCyclesInCurrentInstruction <= 0) {                      // If finished executing Halt instruction, set the halted flag and reset halting
            halted = true
            halting = false
        }
        else if (remainingCyclesInCurrentInstruction <= 0) {
            val opCode = bus.read8Unsigned(pc.get())                                            // read the next opcode
            currentInstruction = opCodes[opCode] ?: InvalidInstruction(opCode)          // decode the instruction

            increment(pc)                                                               // increment program counter

            val operandLength = currentInstruction.length - 1                           // get operand length for instruction
            for(index in 0 until operandLength) {
                operand[index] = bus.read8Unsigned(pc.get())                                    // read byte from operand and store
                increment(pc)                                                           // increment the program counter
            }

            totalCyclesInCurrentInstruction = currentInstruction.run(operand)           // Run the instruction, store the cycles required
            remainingCyclesInCurrentInstruction = totalCyclesInCurrentInstruction       // initialise the remainingCyclesInCurrentInstruction counter
        }

        remainingCyclesInCurrentInstruction--                                            // A cycle has passed, decrement the cycles remaining in this instruction
        cycles++                                                                         // total cycles counter increment
    }

    fun serviceInterrupt(interruptAddress: UShort) {
        resume()                                                                         // resume the CPU if halted
        stack.push(pc.get())                                                             // push the current program counter to the stack for later
        pc.set(interruptAddress)                                                         // set the program counter to service the interrupt
    }

    fun getCurrentInstructionLengthInCycles() = totalCyclesInCurrentInstruction          // return the total number of cycles in the current instruction (required for current PPU implementation)
    fun lastInstructionComplete() = (remainingCyclesInCurrentInstruction <= 0)           // return whether the current instruction has completed execution (required for current PPU implementation)

    fun interruptsEnabled() = interruptsEnabled
    fun enableInterrupts() { interruptsEnabled = true }
    fun disableInterrupts() { interruptsEnabled = false }

    fun isHalted() = halted

    fun halt() {
        if(!interruptManager.interruptsRequested()) {                                    // move to the halting state if there are no interrupts pending
            halting = true
        }
    }

    fun resume() { halted = false }                                                      // resume execution

    private fun getOperandString(opArray: UByteArray, opLength: Int) : String {
        return when (opLength) {
            0 -> ""
            1 -> "[${toHexString(opArray[0])}]"
            2 -> "[${toHexString(toUShort(opArray[1], opArray[0]))}]"
            else -> "Error: Operand Length out of range [length = $opLength] "
        }
    }

    fun getInstructionsImplemented() = opCodes.size
    fun getProgramCounter() = pc.get()
    fun getStackPointer() = sp.get()
    fun getA() = a.getUnsigned()
    fun getB() = b.getUnsigned()
    fun getC() = c.getUnsigned()
    fun getD() = d.getUnsigned()
    fun getE() = e.getUnsigned()
    fun getF() = f.getUnsigned()
    fun getH() = h.getUnsigned()
    fun getL() = l.getUnsigned()
    fun getAF() = af.get()
    fun getBC() = bc.get()
    fun getDE() = de.get()
    fun getHL() = hl.get()
    fun isZeroFlagSet() = flags.isZeroSet()
    fun isCarryFlagSet() = flags.isCarrySet()
    fun isHalfCarryFlagSet() = flags.isHalfCarrySet()
    fun isSubtractFlagSet() = flags.isNegSet()

    fun dumpState() {
        println("        ${af.name} = ${toHexString(af.get())}         Zero Flag      : ${flags.isZeroSet()}")
        println("        ${bc.name} = ${toHexString(bc.get())}         Neg Flag       : ${flags.isNegSet()}")
        println("        ${de.name} = ${toHexString(de.get())}         Half Carry Flag: ${flags.isHalfCarrySet()}")
        println("        ${hl.name} = ${toHexString(hl.get())}         Carry Flag     : ${flags.isCarrySet()}")
        println("        ${sp.name} = ${toHexString(sp.get())}")
        println("        ${pc.name} = ${toHexString(pc.get())}         MEM: FF80      : ${toHexString(bus.read8Unsigned(0xff80u))}")
    }

    fun getInstruction(opCode: UByte) = opCodes[opCode]
}