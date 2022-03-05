package com.ut3.hiddendoor.game

import android.annotation.SuppressLint
import android.graphics.*
import android.view.KeyEvent
import android.view.MotionEvent
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.drawable.Camera
import com.ut3.hiddendoor.game.drawable.DrawableRect
import com.ut3.hiddendoor.game.drawable.tiledmap.Tileset
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.utils.Vector2f
import java.util.*
import kotlin.concurrent.schedule

class GameLogic(private val gameView: GameView): Thread() {
    var isRunning = false; private set

    private val tilemap = gameView.context.loadTiledMap(R.raw.testmap)

    private val gameViewCamera = Camera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat())
    ).apply { zoom = 1f }


    var dx = 0f
    var dy = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun start() {
        isRunning = true
        super.start()

        gameView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dx = when {
                        event.x < v.width / 4 -> -1f
                        event.x > v.width * 3f / 4f -> 1f
                        else -> 0f
                    }

                    dy = when {
                        event.y < v.height / 4 -> -1f
                        event.y > v.height * 3f / 4f -> 1f
                        else -> 0f
                    }

                    true
                }

                MotionEvent.ACTION_UP -> {
                    dx = 0f
                    dy = 0f
                    true
                }

                else -> false
            }
        }
    }

    private val timer = Timer()
    private var lastDrawing = System.currentTimeMillis()
    private var player = RectF(200f, 200f, 216f, 216f)
    private var py = 0f
    override fun run() {
        if (isRunning) {
            val currentTime = System.currentTimeMillis()
            val delta = (currentTime - lastDrawing) / 1000f


            py += (12.2f * delta).coerceAtMost(15f)
            player = let {
                val tmp = RectF(player).apply { offset(0f, py * delta * 16f) }
                if (tilemap.collisionTilesIntersecting(tmp).any { it == 1 }) {
                    py = 0f
                    player
                } else {
                    tmp
                }
            }
            val tmpPlayer = RectF(player).apply { offset(dx * delta * 64f, dy * delta * 64f) }
            if (!tilemap.collisionTilesIntersecting(tmpPlayer).any { it == 1 }) {
                player = tmpPlayer
            }
            gameViewCamera.centerOn(Vector2f(player.centerX(), player.centerY()))


            gameView.draw { canvas, paint ->
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 40f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(gameViewCamera) { canvas, paint ->
                    canvas.draw(tilemap, paint)

                    paint.color = Color.RED
                    canvas.drawRect(player, paint)
                }

                paint.color = Color.WHITE
                canvas.drawText("${1000 / (currentTime - lastDrawing)} fps", 10f, 10f, paint)
            }

//            println("${1000 / ((currentTime - lastDrawing).coerceAtLeast(1))} fps\"")
            lastDrawing = currentTime

            timer.schedule(0) {
                this@GameLogic.run()
            }
        }
    }
}