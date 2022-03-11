package com.ut3.hiddendoor.game.logic

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_LIGHT
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.view.MotionEvent
import android.view.View
import androidx.core.content.getSystemService
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.LevelFactory
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.utils.Preferences
import com.ut3.hiddendoor.game.utils.Vector2f
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


class GameLogic(private val gameView: GameView): Logic, View.OnTouchListener {

    companion object {
        private const val TARGET_FPS = 30L
        private const val FRAME_INTERVAL = 1000L / TARGET_FPS
    }

    private val preferences = Preferences(gameView.context)

    init {
        gameView.setOnTouchListener(this)
        gameView.context.getSystemService<SensorManager>()?.run {
            val lightSensor = getDefaultSensor(TYPE_LIGHT)
            val listener = object : SensorEventListener {
                override fun onAccuracyChanged(sensor: Sensor?, i: Int) = Unit
                override fun onSensorChanged(sensorEvent: SensorEvent) {
                    state.luminosity = sensorEvent.values[0]
                }
            }

            registerListener(listener, lightSensor, SENSOR_DELAY_FASTEST)
        }
    }

    private var previousUpdate = 0L
    private var isAlive = AtomicBoolean(false)
    private var shouldRender = AtomicBoolean(false)
    private val thread = thread(start = false) {
        level.onLoad()
        gameLoop()
    }

    private val renderMutex = Semaphore(1)
    private val timer = Timer()

    private val level = LevelFactory.getLevel(preferences.currentLevel, gameView)
        ?: throw IllegalStateException("Unable to load level ${preferences.currentLevel}")

    private var state = MutableInputState(null, Vector2f(0f,0f), 500f, Vector2f(0f, 0f))

    private fun scheduleRenderTask() {
        if (shouldRender.get()) {
//            println("unlocking mutex")
            runCatching { renderMutex.release() }

            timer.schedule(0) {
                scheduleRenderTask()
            }
        } else {
            println("should not render")
        }
    }

    fun start() {
        previousUpdate = System.currentTimeMillis()
        isAlive.set(true)
        shouldRender.set(true)

        scheduleRenderTask()
        thread.start()
    }

    fun stop() {
        isAlive.set(false)
        shouldRender.set(false)

        thread.interrupt()
        runCatching { renderMutex.release() }
    }

    private fun gameLoop() {
        if (isAlive.get()) {
            val currentTime = System.currentTimeMillis()
            val deltaMs = (currentTime - previousUpdate)
            val deltaS = deltaMs / 1000f
            previousUpdate = currentTime

//            println("fps: ${1f / deltaS}")


            level.handleInput(state)
            level.update(deltaS)
            level.postUpdate(deltaS)

//            renderMutex.acquire()
            level.render()

            timer.schedule(0) {
                gameLoop()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        state.touchEvent = event
        return true
    }

}