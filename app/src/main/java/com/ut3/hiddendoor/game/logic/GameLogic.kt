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
import androidx.core.content.getSystemService
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.leveltwo.LevelTwo
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
    var sensorManager : SensorManager = gameView.context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var rotationSensor : Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    init {
        sensorManager.registerListener(this,rotationSensor, SENSOR_DELAY_FASTEST)
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

    private var isAlive = AtomicBoolean(false)
    private var shouldRender = AtomicBoolean(false)
    private val thread = thread(start = false) {
        level.onLoad()
        gameLoop()
    }

    private val renderMutex = Semaphore(1)
    private val timer = Timer()


    private val level = LevelTwo(gameView)
    private var previousUpdate = 0L

    private var state = MutableInputState(null, Vector2f(0f,0f), 500f, Vector2f(0f, 0f),
        Vector3f(0f,0f,0f)
    )

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
            Sensor.TYPE_ROTATION_VECTOR -> {
                state.rotation = Vector3f(x=event.values[0],y=event.values[1],z=event.values[2])
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

}