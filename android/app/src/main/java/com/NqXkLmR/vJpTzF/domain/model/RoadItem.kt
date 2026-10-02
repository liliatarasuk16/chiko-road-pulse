package com.NqXkLmR.vJpTzF.domain.model

data class RoadItem(
    val id: Long,
    val lane: Lane,
    val kind: RoadItemKind,
    val color: ParcelColor?,
    val progress: Float,
    val resolved: Boolean = false
)
