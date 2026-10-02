package com.NqXkLmR.vJpTzF.domain.usecase

import com.NqXkLmR.vJpTzF.domain.model.Lane
import com.NqXkLmR.vJpTzF.domain.model.ParcelColor
import com.NqXkLmR.vJpTzF.domain.model.PickOutcome
import com.NqXkLmR.vJpTzF.domain.model.RoadItem
import com.NqXkLmR.vJpTzF.domain.model.RoadItemKind

class EvaluatePickUseCase {

    operator fun invoke(
        item: RoadItem,
        cartLane: Lane,
        targetColor: ParcelColor?,
        invulnerable: Boolean
    ): PickOutcome {
        if (item.lane != cartLane) {
            return PickOutcome.IGNORED
        }
        if (item.kind == RoadItemKind.CRATE) {
            return if (invulnerable) PickOutcome.IGNORED else PickOutcome.CRASH
        }
        return if (item.color != null && item.color == targetColor) {
            PickOutcome.CORRECT
        } else {
            PickOutcome.WRONG_COLOR
        }
    }
}
