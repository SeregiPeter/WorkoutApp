package com.example.workoutapp.sensormanagement

import android.hardware.SensorEvent
import android.hardware.SensorManager

interface MeasurementStrategy {
    fun registerSensors(sensorManager: SensorManager)
    fun unregisterSensors(sensorManager: SensorManager)
    fun onSensorChanged(event: SensorEvent?)
}