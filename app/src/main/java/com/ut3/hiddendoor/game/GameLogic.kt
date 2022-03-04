package com.ut3.hiddendoor.game

import android.graphics.*
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.drawable.Camera
import com.ut3.hiddendoor.game.drawable.DrawableRect
import com.ut3.hiddendoor.game.drawable.tiledmap.Tileset
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import java.util.*
import kotlin.concurrent.schedule

class GameLogic(private val gameView: GameView, private val camera: Camera): Thread() {
    var isRunning = false; private set

    private val drawable = DrawableRect(RectF(0f, 0f, 100f, 100f))
    val tilemap = gameView.context.loadTiledMap(R.raw.testmap)

    private val gameViewCamera = Camera(
        screenPosition = RectF(128f, 128f, 1920f, 1080f),
        gamePosition = tilemap.rect
    ).apply { zoom = 8f }

    override fun start() {
        isRunning = true
        super.start()
    }

    private var lastDrawing = System.currentTimeMillis()
    override fun run() {
        if (isRunning) {
//            camera.moveInGame(offsetX = 1f)
//            gameViewCamera.moveInGame(offsetX = 0.5f)
//            if (!gameViewCamera.contains(drawable)) {
//                gameViewCamera.moveInGame(offsetX = 0.5f)
//            }


            gameView.draw {
                clear()

                withCamera(gameViewCamera) { canvas, paint ->
                    canvas.draw(paint, tilemap)

//                    canvas.drawVertices(
//                        Canvas.VertexMode.TRIANGLES,
//                        12,
//                        floatArrayOf(
//                            0f, 0f,
//                            256f, 0f,
//                            0f, 256f,
//                            256f, 0f,
//                            0f, 256f,
//                            256f, 256f
//                        ),
//                        0,
//                        floatArrayOf(
//                            0f, 0f,
//                            128f, 0f,
//                            0f, 128f,
//                            128f, 0f,
//                            0f, 128f,
//                            128f, 128f
//                        ),
//                        0,
//                        null,
//                        0,
//                        null,
//                        0,
//                        0,
//                        paint.apply {
//                            shader = BitmapShader(tileset.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//                        }
//                    )
                }

//                withCamera(camera) { canvas, paint ->
//                    canvas.clear()
//                    canvas.fill(Color.WHITE)
//                    canvas.draw(paint, drawable)
//                }
//
//                withCamera(gameViewCamera) { canvas, paint ->
//                    canvas.clear()
//                    canvas.fill(Color.GRAY)
//                    canvas.draw(paint, drawable)
//                }
            }

            val currentTime = System.currentTimeMillis()
            println("${1000 / (currentTime - lastDrawing)} fps")

            val offset = 16f / (currentTime - lastDrawing)
            println(offset)

            if (offset <= 1f) {
                gameViewCamera.moveInGame(offsetY = offset)
            }
            lastDrawing = currentTime

            Timer().schedule(0) {
                this@GameLogic.run()
            }
        }
    }
}