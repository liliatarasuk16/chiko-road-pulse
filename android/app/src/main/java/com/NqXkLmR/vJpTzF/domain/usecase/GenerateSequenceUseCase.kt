package com.NqXkLmR.vJpTzF.domain.usecase

import com.NqXkLmR.vJpTzF.domain.model.ParcelColor
import kotlin.random.Random

class GenerateSequenceUseCase(private val random: Random = Random.Default) {

    operator fun invoke(length: Int): List<ParcelColor> {
        val palette = ParcelColor.values()
        val sequence = ArrayList<ParcelColor>(length)
        var previous: ParcelColor? = null
        var repeats = 0
        var index = 0
        while (index < length) {
            val candidate = palette[random.nextInt(palette.size)]
            if (candidate == previous && repeats >= 1) {
                continue
            }
            repeats = if (candidate == previous) repeats + 1 else 0
            previous = candidate
            sequence.add(candidate)
            index++
        }
        return sequence
    }
}
