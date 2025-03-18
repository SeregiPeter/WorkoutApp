package com.example.workoutapp.sensormanagement

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class ProximityStrategy(
    private val onRepCountChanged: (Int) -> Unit
) : MeasurementStrategy, SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var proximitySensor: Sensor? = null
    private var isNear = false
    private var repCount = 0

    override fun registerSensors(sensorManager: SensorManager) {
        this.sensorManager = sensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        proximitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun unregisterSensors(sensorManager: SensorManager) {
        sensorManager.unregisterListener(this)
        this.sensorManager = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_PROXIMITY) {
                val isCurrentlyNear = it.values[0] < it.sensor.maximumRange

                if (!isNear && isCurrentlyNear) {
                    repCount++
                    onRepCountChanged(repCount)
                }
                isNear = isCurrentlyNear
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
