package com.NqXkLmR.vJpTzF.domain.model

enum class Lane(val index: Int) {
    LEFT(0),
    CENTER(1),
    RIGHT(2);

    fun shifted(delta: Int): Lane {
        val target = (index + delta).coerceIn(0, values().size - 1)
        return of(target)
    }

    companion object {
        fun of(index: Int): Lane = values().firstOrNull { it.index == index } ?: CENTER
    }
}
