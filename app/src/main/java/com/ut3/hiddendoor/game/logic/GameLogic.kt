package com.ut3.hiddendoor.game.logic

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_LIGHT
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.getSystemService
import android.app.Activity
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.leveltwo.LevelTwo
import com.ut3.hiddendoor.game.utils.Vector2f
import com.ut3.hiddendoor.game.utils.Vector3f
import com.ut3.hiddendoor.game.levels.HomeLevel
import com.ut3.hiddendoor.game.levels.LevelFactory
import com.ut3.hiddendoor.game.utils.Preferences
import com.ut3.hiddendoor.game.utils.SensorsListener
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.math.atan2
import kotlin.math.roundToInt


class GameLogic(activity: Activity, private val gameView: GameView, levelToLoad: String? = null) : Logic, SensorEventListener {

    companion object {
        private const val TARGET_FPS = 30L
        private const val FRAME_INTERVAL = 1000L / TARGET_FPS
        private const val X_AXIS_INDEX = 0
        private const val Y_AXIS_INDEX = 1
        private const val Z_AXIS_AXIS = 2
        private const val ORIENTATION_UNKNOWN = -1
        private const val ONE_EIGHTY_OVER_PI = 57.29577957855f
        private const val UPSIDE_DOWN_ANGLE = 270
    }
    var sensorManager : SensorManager = gameView.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var rotationSensor : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val preferences = Preferences(gameView.context)
    private var state = MutableInputState()
    private val sensorsListener = SensorsListener(gameView, state)

    private var previousUpdate = 0L
            registerListener(listener, lightSensor, SENSOR_DELAY_FASTEST)
        }
    }
    private var rotationDeg: Int = 0
    private var rotationRoundedClockwise: Int = 0

    private fun calculateRoundedRotation(newRotationDeg: Int): Int {
        return if (newRotationDeg <= 45 || newRotationDeg > 315) { // round to 0
            0  // portrait
        } else if (newRotationDeg in 46..135) { // round to 90
            90  // clockwise landscape
        } else if (newRotationDeg in 136..225) { // round to 180
            180  // upside down portrait
        } else if (newRotationDeg in 226..315) { // round to 270
            270  // anticlockwise landscape
        } else {
            0
        }
    }
    private fun calculateNewRotationDegree(event: SensorEvent): Int {
        val values = event.values
        var newRotationDeg = ORIENTATION_UNKNOWN
        val x = -values[X_AXIS_INDEX]
        val y = -values[Y_AXIS_INDEX]
        val z = -values[Z_AXIS_AXIS]
        val magnitude = x * x + y * y
        // Don't trust the angle if the magnitude is small compared to the y value
        if (magnitude * 4 >= z * z) {
            val angle = atan2((-y).toDouble(), x.toDouble()).toFloat() * ONE_EIGHTY_OVER_PI
            newRotationDeg = 90 - angle.roundToInt()
            // normalize to 0 - 359 range
            while (newRotationDeg >= 360) {
                newRotationDeg -= 360
            }
            while (newRotationDeg < 0) {
                newRotationDeg += 360
            }
        }
        return newRotationDeg
    }

    private var isAlive = AtomicBoolean(false)
    private var gameThread = generateThread()

    private val timer = Timer()

    private val level = LevelFactory.getLevel(
        levelToLoad ?: preferences.currentLevel,
        gameView,
        activity = activity,
        gameLogic = this
    )
        ?: throw IllegalStateException("Unable to load level ${preferences.currentLevel}")

    private fun generateThread() = thread(start = false) {
        gameLoop()
    }

    fun start() {
        previousUpdate = System.currentTimeMillis()
        level.onLoad()

        sensorsListener.startListeners()
        isAlive.set(true)

        assert(!gameThread.isAlive)
        gameThread = generateThread()
        gameThread.start()
    }

    fun stop() {
        sensorsListener.stopListeners()
        isAlive.set(false)

        gameThread.join()
        level.clean()
        assert(!gameThread.isAlive)
    }

    private fun gameLoop() {
        if (isAlive.get()) {
            val currentTime = System.currentTimeMillis()
            val deltaMs = (currentTime - previousUpdate)
            val deltaS = deltaMs / 1000f
            previousUpdate = currentTime

            level.handleInput(state)
            level.update(deltaS)
            level.postUpdate(deltaS)
            level.render()

            timer.schedule(0) {
                gameLoop()
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val newRotationDeg = calculateNewRotationDegree(event)
        println("Rotation: $newRotationDeg")
        if (newRotationDeg != rotationDeg) {
            rotationRoundedClockwise = calculateRoundedRotation(newRotationDeg)
        }
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                state.upsideDown = rotationRoundedClockwise == UPSIDE_DOWN_ANGLE
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}