package com.example.workoutapp.sensormanagement

object MeasurementStrategyFactory {
    fun create(
        method: String,
        onRepCountChanged: (Int) -> Unit,
        onUprightChanged: (Boolean) -> Unit
    ): MeasurementStrategy {
        return when (method) {
            "downUpMovement" -> DownUpMovementStrategy(onRepCountChanged, onUprightChanged)
            "proximity" -> ProximityStrategy(onRepCountChanged)
            else -> throw IllegalArgumentException("Unknown measurement method: $method")
        }
    }
}
