package com.example.workoutapp.sensormanagement

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DownUpMovementStrategy(
    private val onRepCountChanged: (Int) -> Unit,
    private val onUprightChanged: (Boolean) -> Unit
) : MeasurementStrategy, SensorEventListener {

    private var moveCount = 0
    private var isMovingUp = false
    private var isUpright = true
    private var upStartTime = 0L
    private var downStartTime = 0L

    override fun registerSensors(sensorManager: SensorManager) {
        sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun unregisterSensors(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val newIsUpright = it.values[1] > 5.0f
                    if (newIsUpright != isUpright) {
                        isUpright = newIsUpright
                        onUprightChanged(isUpright)
                    }
                }
                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    if (!isUpright) return

                    val currentTime = System.currentTimeMillis()
                    val yAcceleration = it.values[1]

                    if (yAcceleration > 1.5f) {
                        if (!isMovingUp) upStartTime = currentTime
                        isMovingUp = true
                    }

                    if (yAcceleration < -1.5f && isMovingUp) {
                        val upDuration = currentTime - upStartTime
                        if (upDuration >= 500) {
                            downStartTime = currentTime
                        }
                        isMovingUp = false
                    }

                    if (!isMovingUp && downStartTime > 0) {
                        val downDuration = currentTime - downStartTime
                        if (downDuration >= 500) {
                            moveCount++
                            downStartTime = 0
                            onRepCountChanged(moveCount)
                        }
                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
