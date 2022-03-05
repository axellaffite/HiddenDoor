package com.ut3.hiddendoor.game.logic

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.IntroductionLevel
import com.ut3.hiddendoor.game.utils.Vector2f
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule
import kotlin.concurrent.thread

class GameLogic(private val gameView: GameView): Logic, View.OnTouchListener {

    companion object {
        private const val TARGET_FPS = 50L
        private const val FRAME_INTERVAL = 1000L / TARGET_FPS
    }

    init {
        gameView.setOnTouchListener(this)
    }

    private var isAlive = AtomicBoolean(false)
    private var shouldRender = AtomicBoolean(false)
    private val thread = thread(start = false) {
        level.onLoad()
        gameLoop()
    }

    private val renderMutex = Semaphore(1)
    private val timer = Timer()


    private val level = IntroductionLevel(gameView)
    private var previousUpdate = 0L

    private var state = MutableInputState(null, Vector2f(0f,0f), 0f, Vector2f(0f, 0f))

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

        runCatching { renderMutex.release() }
        thread.join()
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

            renderMutex.acquire()
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