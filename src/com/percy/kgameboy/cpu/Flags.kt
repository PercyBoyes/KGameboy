package com.percy.kgameboy.cpu

import com.percy.kgameboy.common.Register8
import com.percy.kgameboy.utils.isSet
import com.percy.kgameboy.utils.setBit

class Flags(private val f: Register8) {
    companion object {
        private const val ZERO_BIT = 7
        private const val SUBTRACT_BIT = 6
        private const val HALF_CARRY_BIT = 5
        private const val CARRY_BIT = 4
    }

    fun clear() {
        setZero(false)
        setCarry(false)
        setNegative(false)
        setHalfCarry(false)
    }

    fun setZero(enabled: Boolean) = f.set(setBit(f.getUnsigned(), ZERO_BIT, enabled))
    fun setNegative(enabled: Boolean) = f.set(setBit(f.getUnsigned(), SUBTRACT_BIT, enabled))
    fun setHalfCarry(enabled: Boolean) = f.set(setBit(f.getUnsigned(), HALF_CARRY_BIT, enabled))
    fun setCarry(enabled: Boolean) = f.set(setBit(f.getUnsigned(), CARRY_BIT, enabled))

    fun isZeroSet() = isSet(f.getUnsigned(), ZERO_BIT)
    fun isNegSet() = isSet(f.getUnsigned(), SUBTRACT_BIT)
    fun isHalfCarrySet() = isSet(f.getUnsigned(), HALF_CARRY_BIT)
    fun isCarrySet() = isSet(f.getUnsigned(), CARRY_BIT)
}