package com.ut3.hiddendoor.game

import android.graphics.Color
import android.graphics.RectF
import com.ut3.hiddendoor.game.drawable.Camera
import com.ut3.hiddendoor.game.drawable.DrawableRect
import java.util.*
import kotlin.concurrent.schedule

class GameLogic(private val gameView: GameView, private val camera: Camera): Thread() {
    var isRunning = false; private set

    private val drawable = DrawableRect(RectF(0f, 0f, 100f, 100f))

    private val gameViewCamera = Camera(camera).apply {
        moveOnScreen(300f)
        moveInGame(-10f, -10f)
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    override fun run() {
        if (isRunning) {
            camera.moveInGame(offsetX = 1f)
            gameViewCamera.moveInGame(offsetX = 0.5f)
            if (!gameViewCamera.contains(drawable)) {
                gameViewCamera.moveInGame(offsetX = 0.5f)
            }

            drawable.rect.offsetTo(drawable.rect.left + 1, drawable.rect.top)
            gameView.draw {
                clear()

                withCamera(camera) { canvas, paint ->
                    canvas.clear()
                    canvas.fill(Color.WHITE)
                    canvas.draw(paint, drawable)
                }

                withCamera(gameViewCamera) { canvas, paint ->
                    canvas.clear()
                    canvas.fill(Color.GRAY)
                    canvas.draw(paint, drawable)
                }
            }

            Timer().schedule(10) {
                this@GameLogic.run()
            }
        }
    }
}