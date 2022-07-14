package com.percy.kgameboy.common

interface Register16 {
    val name: String
    fun set(value: UShort)
    fun get() : UShort
}