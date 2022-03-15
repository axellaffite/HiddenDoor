package com.ut3.hiddendoor.game.levels.leveltwo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaPlayer
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.hud.HUD
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.logic.isUpsideDown
import com.ut3.hiddendoor.game.utils.Preferences
import com.ut3.hiddendoor.game.utils.preferences

class LevelTwo(private val gameView: GameView) : EntityManager() {

    companion object {
        const val NAME = "level2"
        const val TILE_MAP_RESOURCE = R.raw.level2
    }

    private val angleReference = preferences(gameView.context) { angleReference }

    private val tilemap = gameView.context.loadTiledMap(TILE_MAP_RESOURCE)
    private lateinit var sound : MediaPlayer
    private val hud : HUD = createHud(gameView)
    private val player = createEntity { Player(gameView, tilemap, hud) {moveTo(tilemap.tileSize*3,tilemap.tileSize*3)} }
    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )

    private var isUpsideDown = false

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

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)
        isUpsideDown = inputState.isUpsideDown(angleReference)
    }

    override fun update(delta: Float) {
        super.update(delta)

        player.flipUpsideDown(isUpsideDown)
        if (isUpsideDown) {
            player.changeRotation(Player.ROTATION.REVERSED)
        } else {
            player.changeRotation(Player.ROTATION.STRAIGHT)
        }
    }

    override fun onSaveState() {
    }

    override fun render() {
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)

                    canvas.drawRect(
                        0f,
                        0f,
                        canvas.width.toFloat(),
                        canvas.height.toFloat(),
                        Paint().apply {
                            color = 0
                        }
                    )
                }
            }

            hud.draw(gameView.rect, canvas, paint)
        }
    }

}