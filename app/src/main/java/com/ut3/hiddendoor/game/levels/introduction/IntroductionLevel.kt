package com.ut3.hiddendoor.game.levels.introduction

import android.graphics.*
import android.media.MediaPlayer
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.utils.Preferences
import com.ut3.hiddendoor.game.utils.Vector2i
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.system.measureTimeMillis


class IntroductionLevel(private val gameView: GameView) : EntityManager() {

    companion object {
        const val TILE_MAP_RESOURCE = R.raw.testmap
        const val NAME = "introduction"
    }

    private val preferences = Preferences(gameView.context)
    private val luminosityReference = preferences.luminosityReference
    private lateinit var sound: MediaPlayer
    private var luminosityLevel = 0f
    private var nightAlpha = 0

    private var fps = 0
    private val tilemap = gameView.context.loadTiledMap(TILE_MAP_RESOURCE)
    private val hud = createHud(gameView) { controlButtons.isBVisible = false }
    private val player = createEntity { Player(gameView, tilemap, hud) }
    private val bridge = createEntity {
        Bridge(x = 18, y = 29, blockCount = 8, tilemap = tilemap, player = player)
    }
    private val lever = createEntity { Lever(gameView, hud, player, bridge, ::nightAlpha) { move(200f, 448f) } }

    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )

    override fun onLoad() {
        sound = MediaPlayer.create(gameView.context, R.raw.ambiance_sound).apply {
            isLooping = true
            start()
        }
    }

    override fun clean() {
        super.clean()
        sound.stop()
    }

    override fun onSaveState() {
        TODO("save state of the level")
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)

        luminosityLevel = inputState.luminosity
    }

    override fun update(delta: Float) {
        super.update(delta)

        val threshold = luminosityReference / 2
        nightAlpha = ((threshold - (luminosityLevel * 4f / 3f)).coerceAtLeast(0f) * (255f / threshold)).toInt()
        println("$threshold $luminosityLevel $nightAlpha")
        fps = (1f / delta).toInt()
    }

    override fun render() {
        println("rendering")
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)
                    canvas.draw(bridge, paint)

                    canvas.drawRect(
                        0f,
                        0f,
                        canvas.width.toFloat(),
                        canvas.height.toFloat(),
                        Paint().apply {
                            color = 0
                            alpha = nightAlpha
                        }
                    )

                    canvas.draw(lever, paint)
                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }

}