package com.ut3.hiddendoor.game.utils

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.Sensor.*
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.getSystemService
import com.ut3.hiddendoor.game.logic.MutableInputState

class SensorsListener(
    private val view: View,
    private val state: MutableInputState
) : OnTouchListener, SensorEventListener {

    private val sensorManager = view.context.getSystemService<SensorManager>()
    var onLuminosityValueChanged: (Float) -> Unit = { }
    var onAccelerometerValueChanged: (Vector3f) -> Unit = { }
    var onOrientationValueChanged: (Vector3f) -> Unit = { }
    var isListening = false; private set

    fun startListeners(): Boolean {
        if (isListening) {
            return false
        }

        view.setOnTouchListener(this)
        sensorManager?.run {
            getDefaultSensor(TYPE_LIGHT)?.let { lightSensor ->
                registerListener(
                    this@SensorsListener,
                    lightSensor,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }

            getDefaultSensor(TYPE_ACCELEROMETER)?.let { accelerometer ->
                registerListener(
                    this@SensorsListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }

            getDefaultSensor(TYPE_GYROSCOPE)?.let { accelerometer ->
                registerListener(
                    this@SensorsListener,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }
        }

        return true
    }

    fun stopListeners() {
        view.setOnClickListener(null)
        sensorManager?.unregisterListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        state.touchEvent = motionEvent
        return true
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            TYPE_LIGHT -> {
                state.luminosity = event.values[0]
                onLuminosityValueChanged(state.luminosity)
            }

            TYPE_ACCELEROMETER -> {
                state.acceleration = Vector3f(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2]
                )

                onAccelerometerValueChanged(state.acceleration)
            }

            TYPE_GYROSCOPE -> {
                state.orientation = Vector3f(
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                )

                onOrientationValueChanged(state.orientation)
            }
        }
    }

}