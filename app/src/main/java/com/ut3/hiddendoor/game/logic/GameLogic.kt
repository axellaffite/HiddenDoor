package com.ut3.hiddendoor.game.logic

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_LIGHT
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_FASTEST
import android.view.MotionEvent
import android.view.View
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.levels.level3.HiddenKeyLevel
import com.ut3.hiddendoor.game.utils.Vector2f
import com.ut3.hiddendoor.game.utils.Vector3f
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule
import kotlin.concurrent.thread


class GameLogic(private val gameView: GameView): Logic, View.OnTouchListener, SensorEventListener {

    companion object {
        private const val TARGET_FPS = 30L
        private const val FRAME_INTERVAL = 1000L / TARGET_FPS
    }
    var sensorManager: SensorManager

    init {
        sensorManager = gameView.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gameView.setOnTouchListener(this)
        var accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lightSensor = sensorManager.getDefaultSensor(TYPE_LIGHT)
        sensorManager.registerListener(this, lightSensor, SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this , accelerometer, SensorManager.SENSOR_DELAY_GAME)
    }

    private var isAlive = AtomicBoolean(false)
    private var shouldRender = AtomicBoolean(false)
    private val thread = thread(start = false) {
        level.onLoad()
        gameLoop()
    }

    private val renderMutex = Semaphore(1)
    private val timer = Timer()


    //private val level = IntroductionLevel(gameView)
    private val level = HiddenKeyLevel(gameView)
    private var previousUpdate = 0L

    private var state = MutableInputState(null, Vector3f(0f,0f, 0f), 500f, Vector2f(0f, 0f))

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

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            TYPE_LIGHT -> {
                state.luminosity = event.values[0]
            }
            Sensor.TYPE_ACCELEROMETER -> {
                state.acceleration = Vector3f(x=event.values[0],y=event.values[1],z=event.values[2])
            }
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}