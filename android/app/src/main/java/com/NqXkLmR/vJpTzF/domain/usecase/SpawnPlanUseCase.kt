package com.NqXkLmR.vJpTzF.domain.usecase

import com.NqXkLmR.vJpTzF.domain.model.Lane
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor
import com.NqXkLmR.vJpTzF.domain.model.RoadItem
import com.NqXkLmR.vJpTzF.domain.model.RoadItemKind
import kotlin.random.Random

class SpawnPlanUseCase(private val random: Random = Random.Default) {

    operator fun invoke(
        targetColor: ParcelColor,
        cartLane: Lane,
        firstId: Long
    ): List<RoadItem> {
        val lanes = Lane.values()
        val targetLane = lanes[random.nextInt(lanes.size)]
        val wave = ArrayList<RoadItem>(lanes.size)
        var id = firstId
        for (lane in lanes) {
            if (lane == targetLane) {
                wave.add(
                    RoadItem(
                        id = id++,
                        lane = lane,
                        kind = RoadItemKind.PARCEL,
                        color = targetColor,
                        progress = 0f
                    )
                )
                continue
            }
            if (lane == cartLane) {
                continue
            }
            val crate = random.nextInt(100) < 45
            val decoy = if (crate) {
                RoadItem(id++, lane, RoadItemKind.CRATE, null, 0f)
            } else {
                RoadItem(id++, lane, RoadItemKind.PARCEL, otherColor(targetColor), 0f)
            }
            wave.add(decoy)
        }
        return wave
    }

    private fun otherColor(target: ParcelColor): ParcelColor {
        val options = ParcelColor.values().filter { it != target }
        return options[random.nextInt(options.size)]
    }
}
