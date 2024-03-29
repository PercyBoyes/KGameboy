package com.percy.kgameboy.cpu.instructions

import com.percy.kgameboy.bus.DefaultBusImpl
import com.percy.kgameboy.cpu.Flags
import com.percy.kgameboy.common.Register16
import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.SystemLogger
import com.percy.kgameboy.utils.toHexString

class CBPrefix(a: Register8, b: Register8, c: Register8, d: Register8,
               e: Register8, f: Register8, h: Register8, l: Register8,
               hl: Register16, bus: DefaultBusImpl, flags: Flags, private val logger: SystemLogger) : Instruction {
    override val name = "CBPrefix"
    override val description = "The next instruction belongs to the CB Prefix"
    override val length = 2
    private val cycles = 4
    override val opCode = 0xcbu.toUByte()

    override fun run(operand: UByteArray) : Int {
        val i = opCodes[operand[0]] ?: InvalidInstruction(operand[0])
        logger.log(SystemLogger.Component.CPU, "        [${toHexString(i.opCode)}] ${i.name}")
        return cycles + i.run(operand)
    }

    override fun getState(operand: UByteArray): String {
        val i = opCodes[operand[0]] ?: InvalidInstruction(operand[0])
        return i.getState(operand)
    }

    private val opCodes : Map<UByte,Instruction> = mapOf(
        0x00u.toUByte() to RLC(b, flags, 0x00u),
        0x01u.toUByte() to RLC(c, flags, 0x01u),
        0x02u.toUByte() to RLC(d, flags, 0x02u),
        0x03u.toUByte() to RLC(e, flags, 0x03u),
        0x04u.toUByte() to RLC(h, flags, 0x04u),
        0x05u.toUByte() to RLC(l, flags, 0x05u),
        0x06u.toUByte() to RLCah(hl, flags, bus, 0x06u),
        0x07u.toUByte() to RLC(a, flags, 0x07u),
        0x08u.toUByte() to RRC(b, flags, 0x08u),
        0x09u.toUByte() to RRC(c, flags, 0x09u),
        0x0au.toUByte() to RRC(d, flags, 0x0au),
        0x0bu.toUByte() to RRC(e, flags, 0x0bu),
        0x0cu.toUByte() to RRC(h, flags, 0x0cu),
        0x0du.toUByte() to RRC(l, flags, 0x0du),
        0x0eu.toUByte() to RRCah(hl, flags, bus, 0x0eu),
        0x0fu.toUByte() to RRC(a, flags, 0x0fu),
        0x10u.toUByte() to RLr(b, flags, 0x10u),
        0x11u.toUByte() to RLr(c, flags, 0x11u),
        0x12u.toUByte() to RLr(d, flags, 0x12u),
        0x13u.toUByte() to RLr(e, flags, 0x13u),
        0x14u.toUByte() to RLr(h, flags, 0x14u),
        0x15u.toUByte() to RLr(l, flags, 0x15u),
        0x16u.toUByte() to RLah(hl, flags, bus, 0x16u),
        0x17u.toUByte() to RLr(a, flags, 0x17u),
        0x18u.toUByte() to RRr(b, flags, 0x18u),
        0x19u.toUByte() to RRr(c, flags, 0x19u),
        0x1au.toUByte() to RRr(d, flags, 0x1au),
        0x1bu.toUByte() to RRr(e, flags, 0x1bu),
        0x1cu.toUByte() to RRr(h, flags, 0x1cu),
        0x1du.toUByte() to RRr(l, flags, 0x1du),
        0x1eu.toUByte() to RRah(hl, flags, bus, 0x1eu),
        0x1fu.toUByte() to RRr(a, flags, 0x1fu),
        0x20u.toUByte() to SLAr(b, flags, 0x20u),
        0x21u.toUByte() to SLAr(c, flags, 0x21u),
        0x22u.toUByte() to SLAr(d, flags, 0x22u),
        0x23u.toUByte() to SLAr(e, flags, 0x23u),
        0x24u.toUByte() to SLAr(h, flags, 0x24u),
        0x25u.toUByte() to SLAr(l, flags, 0x25u),
        0x26u.toUByte() to SLAah(hl, flags, bus, 0x26u),
        0x27u.toUByte() to SLAr(a, flags, 0x27u),
        0x28u.toUByte() to SRA(b, flags, 0x28u),
        0x29u.toUByte() to SRA(c, flags, 0x29u),
        0x2au.toUByte() to SRA(d, flags, 0x2au),
        0x2bu.toUByte() to SRA(e, flags, 0x2bu),
        0x2cu.toUByte() to SRA(h, flags, 0x2cu),
        0x2du.toUByte() to SRA(l, flags, 0x2du),
        0x2eu.toUByte() to SRAhl(hl, flags, bus, 0x2eu),
        0x2fu.toUByte() to SRA(a, flags, 0x2fu),
        0x30u.toUByte() to SWAP(b, flags, 0x30u),
        0x31u.toUByte() to SWAP(c, flags, 0x31u),
        0x32u.toUByte() to SWAP(d, flags, 0x32u),
        0x33u.toUByte() to SWAP(e, flags, 0x33u),
        0x34u.toUByte() to SWAP(h, flags, 0x34u),
        0x35u.toUByte() to SWAP(l, flags, 0x35u),
        0x36u.toUByte() to SWAPahl(hl, flags, bus, 0x36u),
        0x37u.toUByte() to SWAP(a, flags, 0x37u),
        0x38u.toUByte() to SRLr(b, flags, 0x38u),
        0x39u.toUByte() to SRLr(c, flags, 0x39u),
        0x3au.toUByte() to SRLr(d, flags, 0x3au),
        0x3bu.toUByte() to SRLr(e, flags, 0x3bu),
        0x3cu.toUByte() to SRLr(h, flags, 0x3cu),
        0x3du.toUByte() to SRLr(l, flags, 0x3du),
        0x3eu.toUByte() to SRLah(hl, flags, bus, 0x3eu),
        0x3fu.toUByte() to SRLr(a, flags, 0x3fu),
        0x40u.toUByte() to BITr(b, 0, flags, 0x40u),
        0x41u.toUByte() to BITr(c, 0, flags, 0x41u),
        0x42u.toUByte() to BITr(d, 0, flags, 0x42u),
        0x43u.toUByte() to BITr(e, 0, flags, 0x43u),
        0x44u.toUByte() to BITr(h, 0, flags, 0x44u),
        0x45u.toUByte() to BITr(l, 0, flags, 0x45u),
        0x46u.toUByte() to BITahl(hl, 0, bus, flags, 0x46u),
        0x47u.toUByte() to BITr(a, 0, flags, 0x47u),
        0x48u.toUByte() to BITr(b, 1, flags, 0x48u),
        0x49u.toUByte() to BITr(c, 1, flags, 0x49u),
        0x4au.toUByte() to BITr(d, 1, flags, 0x4au),
        0x4bu.toUByte() to BITr(e, 1, flags, 0x4bu),
        0x4cu.toUByte() to BITr(h, 1, flags, 0x4cu),
        0x4du.toUByte() to BITr(l, 1, flags, 0x4du),
        0x4eu.toUByte() to BITahl(hl, 1, bus, flags, 0x4eu),
        0x4fu.toUByte() to BITr(a, 1, flags, 0x4fu),
        0x50u.toUByte() to BITr(b, 2, flags, 0x50u),
        0x51u.toUByte() to BITr(c, 2, flags, 0x51u),
        0x52u.toUByte() to BITr(d, 2, flags, 0x52u),
        0x53u.toUByte() to BITr(e, 2, flags, 0x53u),
        0x54u.toUByte() to BITr(h, 2, flags, 0x54u),
        0x55u.toUByte() to BITr(l, 2, flags, 0x55u),
        0x56u.toUByte() to BITahl(hl, 2, bus, flags, 0x56u),
        0x57u.toUByte() to BITr(a, 2, flags, 0x57u),
        0x58u.toUByte() to BITr(b, 3, flags, 0x58u),
        0x59u.toUByte() to BITr(c, 3, flags, 0x59u),
        0x5au.toUByte() to BITr(d, 3, flags, 0x5au),
        0x5bu.toUByte() to BITr(e, 3, flags, 0x5bu),
        0x5cu.toUByte() to BITr(h, 3, flags, 0x5cu),
        0x5du.toUByte() to BITr(l, 3, flags, 0x5du),
        0x5eu.toUByte() to BITahl(hl, 3, bus, flags, 0x5eu),
        0x5fu.toUByte() to BITr(a, 3, flags, 0x5fu),
        0x60u.toUByte() to BITr(b, 4, flags, 0x60u),
        0x61u.toUByte() to BITr(c, 4, flags, 0x61u),
        0x62u.toUByte() to BITr(d, 4, flags, 0x62u),
        0x63u.toUByte() to BITr(e, 4, flags, 0x63u),
        0x64u.toUByte() to BITr(h, 4, flags, 0x64u),
        0x65u.toUByte() to BITr(l, 4, flags, 0x65u),
        0x66u.toUByte() to BITahl(hl, 4, bus, flags, 0x66u),
        0x67u.toUByte() to BITr(a, 4, flags, 0x67u),
        0x68u.toUByte() to BITr(b, 5, flags, 0x68u),
        0x69u.toUByte() to BITr(c, 5, flags, 0x69u),
        0x6au.toUByte() to BITr(d, 5, flags, 0x6au),
        0x6bu.toUByte() to BITr(e, 5, flags, 0x6bu),
        0x6cu.toUByte() to BITr(h, 5, flags, 0x6cu),
        0x6du.toUByte() to BITr(l, 5, flags, 0x6du),
        0x6eu.toUByte() to BITahl(hl, 5, bus, flags, 0x6eu),
        0x6fu.toUByte() to BITr(a, 5, flags, 0x6fu),
        0x70u.toUByte() to BITr(b, 6, flags, 0x70u),
        0x71u.toUByte() to BITr(c, 6, flags, 0x71u),
        0x72u.toUByte() to BITr(d, 6, flags, 0x72u),
        0x73u.toUByte() to BITr(e, 6, flags, 0x73u),
        0x74u.toUByte() to BITr(h, 6, flags, 0x74u),
        0x75u.toUByte() to BITr(l, 6, flags, 0x75u),
        0x76u.toUByte() to BITahl(hl, 6, bus, flags, 0x76u),
        0x77u.toUByte() to BITr(a, 6, flags, 0x77u),
        0x78u.toUByte() to BITr(b, 7, flags, 0x78u),
        0x79u.toUByte() to BITr(c, 7, flags, 0x79u),
        0x7au.toUByte() to BITr(d, 7, flags, 0x7au),
        0x7bu.toUByte() to BITr(e, 7, flags, 0x7bu),
        0x7cu.toUByte() to BITr(h, 7, flags, 0x7cu),
        0x7du.toUByte() to BITr(l, 7, flags, 0x7du),
        0x7eu.toUByte() to BITahl(hl, 7, bus, flags, 0x7eu),
        0x7fu.toUByte() to BITr(a, 7, flags, 0x7fu),
        0x80u.toUByte() to RESr(b, 0, 0x80u),
        0x81u.toUByte() to RESr(c, 0, 0x81u),
        0x82u.toUByte() to RESr(d, 0, 0x82u),
        0x83u.toUByte() to RESr(e, 0, 0x83u),
        0x84u.toUByte() to RESr(h, 0, 0x84u),
        0x85u.toUByte() to RESr(l, 0, 0x85u),
        0x86u.toUByte() to RESahl(hl, 0, bus, 0x86u),
        0x87u.toUByte() to RESr(a, 0, 0x87u),
        0x88u.toUByte() to RESr(b, 1, 0x88u),
        0x89u.toUByte() to RESr(c, 1, 0x89u),
        0x8au.toUByte() to RESr(d, 1, 0x8au),
        0x8bu.toUByte() to RESr(e, 1, 0x8bu),
        0x8cu.toUByte() to RESr(h, 1, 0x8cu),
        0x8du.toUByte() to RESr(l, 1, 0x8du),
        0x8eu.toUByte() to RESahl(hl, 1, bus, 0x8eu),
        0x8fu.toUByte() to RESr(a, 1, 0x8fu),
        0x90u.toUByte() to RESr(b, 2, 0x90u),
        0x91u.toUByte() to RESr(c, 2, 0x91u),
        0x92u.toUByte() to RESr(d, 2, 0x92u),
        0x93u.toUByte() to RESr(e, 2, 0x93u),
        0x94u.toUByte() to RESr(h, 2, 0x94u),
        0x95u.toUByte() to RESr(l, 2, 0x95u),
        0x96u.toUByte() to RESahl(hl, 2, bus, 0x96u),
        0x97u.toUByte() to RESr(a, 2, 0x97u),
        0x98u.toUByte() to RESr(b, 3, 0x98u),
        0x99u.toUByte() to RESr(c, 3, 0x99u),
        0x9au.toUByte() to RESr(d, 3, 0x9au),
        0x9bu.toUByte() to RESr(e, 3, 0x9bu),
        0x9cu.toUByte() to RESr(h, 3, 0x9cu),
        0x9du.toUByte() to RESr(l, 3, 0x9du),
        0x9eu.toUByte() to RESahl(hl, 3, bus, 0x9eu),
        0x9fu.toUByte() to RESr(a, 3, 0x9fu),
        0xa0u.toUByte() to RESr(b, 4, 0xa0u),
        0xa1u.toUByte() to RESr(c, 4, 0xa1u),
        0xa2u.toUByte() to RESr(d, 4, 0xa2u),
        0xa3u.toUByte() to RESr(e, 4, 0xa3u),
        0xa4u.toUByte() to RESr(h, 4, 0xa4u),
        0xa5u.toUByte() to RESr(l, 4, 0xa5u),
        0xa6u.toUByte() to RESahl(hl, 4, bus, 0xa6u),
        0xa7u.toUByte() to RESr(a, 4, 0xa7u),
        0xa8u.toUByte() to RESr(b, 5, 0xa8u),
        0xa9u.toUByte() to RESr(c, 5, 0xa9u),
        0xaau.toUByte() to RESr(d, 5, 0xaau),
        0xabu.toUByte() to RESr(e, 5, 0xabu),
        0xacu.toUByte() to RESr(h, 5, 0xacu),
        0xadu.toUByte() to RESr(l, 5, 0xadu),
        0xaeu.toUByte() to RESahl(hl, 5, bus, 0xaeu),
        0xafu.toUByte() to RESr(a, 5, 0xafu),
        0xb0u.toUByte() to RESr(b, 6, 0xb0u),
        0xb1u.toUByte() to RESr(c, 6, 0xb1u),
        0xb2u.toUByte() to RESr(d, 6, 0xb2u),
        0xb3u.toUByte() to RESr(e, 6, 0xb3u),
        0xb4u.toUByte() to RESr(h, 6, 0xb4u),
        0xb5u.toUByte() to RESr(l, 6, 0xb5u),
        0xb6u.toUByte() to RESahl(hl, 6, bus, 0xb6u),
        0xb7u.toUByte() to RESr(a, 6, 0xb7u),
        0xb8u.toUByte() to RESr(b, 7, 0xb8u),
        0xb9u.toUByte() to RESr(c, 7, 0xb9u),
        0xbau.toUByte() to RESr(d, 7, 0xbau),
        0xbbu.toUByte() to RESr(e, 7, 0xbbu),
        0xbcu.toUByte() to RESr(h, 7, 0xbcu),
        0xbdu.toUByte() to RESr(l, 7, 0xbdu),
        0xbeu.toUByte() to RESahl(hl, 7, bus, 0xbeu),
        0xbfu.toUByte() to RESr(a, 7, 0xbfu),
        0xc0u.toUByte() to SETr(b, 0, 0xc0u),
        0xc1u.toUByte() to SETr(c, 0, 0xc1u),
        0xc2u.toUByte() to SETr(d, 0, 0xc2u),
        0xc3u.toUByte() to SETr(e, 0, 0xc3u),
        0xc4u.toUByte() to SETr(h, 0, 0xc4u),
        0xc5u.toUByte() to SETr(l, 0, 0xc5u),
        0xc6u.toUByte() to SETahl(hl, 0, bus, 0xc6u),
        0xc7u.toUByte() to SETr(a, 0, 0xc7u),
        0xc8u.toUByte() to SETr(b, 1, 0xc8u),
        0xc9u.toUByte() to SETr(c, 1, 0xc9u),
        0xcau.toUByte() to SETr(d, 1, 0xcau),
        0xcbu.toUByte() to SETr(e, 1, 0xcbu),
        0xccu.toUByte() to SETr(h, 1, 0xccu),
        0xcdu.toUByte() to SETr(l, 1, 0xcdu),
        0xceu.toUByte() to SETahl(hl, 1, bus, 0xceu),
        0xcfu.toUByte() to SETr(a, 1, 0xcfu),
        0xd0u.toUByte() to SETr(b, 2, 0xd0u),
        0xd1u.toUByte() to SETr(c, 2, 0xd1u),
        0xd2u.toUByte() to SETr(d, 2, 0xd2u),
        0xd3u.toUByte() to SETr(e, 2, 0xd3u),
        0xd4u.toUByte() to SETr(h, 2, 0xd4u),
        0xd5u.toUByte() to SETr(l, 2, 0xd5u),
        0xd6u.toUByte() to SETahl(hl, 2, bus, 0xd6u),
        0xd7u.toUByte() to SETr(a, 2, 0xd7u),
        0xd8u.toUByte() to SETr(b, 3, 0xd8u),
        0xd9u.toUByte() to SETr(c, 3, 0xd9u),
        0xdau.toUByte() to SETr(d, 3, 0xdau),
        0xdbu.toUByte() to SETr(e, 3, 0xdbu),
        0xdcu.toUByte() to SETr(h, 3, 0xdcu),
        0xddu.toUByte() to SETr(l, 3, 0xddu),
        0xdeu.toUByte() to SETahl(hl, 3, bus, 0xdeu),
        0xdfu.toUByte() to SETr(a, 3, 0xdfu),
        0xe0u.toUByte() to SETr(b, 4, 0xe0u),
        0xe1u.toUByte() to SETr(c, 4, 0xe1u),
        0xe2u.toUByte() to SETr(d, 4, 0xe2u),
        0xe3u.toUByte() to SETr(e, 4, 0xe3u),
        0xe4u.toUByte() to SETr(h, 4, 0xe4u),
        0xe5u.toUByte() to SETr(l, 4, 0xe5u),
        0xe6u.toUByte() to SETahl(hl, 4, bus, 0xe6u),
        0xe7u.toUByte() to SETr(a, 4, 0xe7u),
        0xe8u.toUByte() to SETr(b, 5, 0xe8u),
        0xe9u.toUByte() to SETr(c, 5, 0xe9u),
        0xeau.toUByte() to SETr(d, 5, 0xeau),
        0xebu.toUByte() to SETr(e, 5, 0xebu),
        0xecu.toUByte() to SETr(h, 5, 0xecu),
        0xedu.toUByte() to SETr(l, 5, 0xedu),
        0xeeu.toUByte() to SETahl(hl, 5, bus, 0xeeu),
        0xefu.toUByte() to SETr(a, 5, 0xefu),
        0xf0u.toUByte() to SETr(b, 6, 0xf0u),
        0xf1u.toUByte() to SETr(c, 6, 0xf1u),
        0xf2u.toUByte() to SETr(d, 6, 0xf2u),
        0xf3u.toUByte() to SETr(e, 6, 0xf3u),
        0xf4u.toUByte() to SETr(h, 6, 0xf4u),
        0xf5u.toUByte() to SETr(l, 6, 0xf5u),
        0xf6u.toUByte() to SETahl(hl, 6, bus, 0xf6u),
        0xf7u.toUByte() to SETr(a, 6, 0xf7u),
        0xf8u.toUByte() to SETr(b, 7, 0xf8u),
        0xf9u.toUByte() to SETr(c, 7, 0xf9u),
        0xfau.toUByte() to SETr(d, 7, 0xfau),
        0xfbu.toUByte() to SETr(e, 7, 0xfbu),
        0xfcu.toUByte() to SETr(h, 7, 0xfcu),
        0xfdu.toUByte() to SETr(l, 7, 0xfdu),
        0xfeu.toUByte() to SETahl(hl, 7, bus, 0xfeu),
        0xffu.toUByte() to SETr(a, 7, 0xffu)
    )

    fun getInstruction(opCode: UByte) = opCodes[opCode]
}