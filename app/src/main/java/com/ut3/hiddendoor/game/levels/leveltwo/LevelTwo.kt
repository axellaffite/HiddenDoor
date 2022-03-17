package com.ut3.hiddendoor.game.levels.leveltwo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.levels.Level
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.logic.isUpsideDown
import com.ut3.hiddendoor.game.utils.preferences

class LevelTwo(
    gameView: GameView,
    goToNextLevel: (String) -> Unit
) : Level(
    gameView = gameView,
    soundRes = R.raw.ambiance_sound,
    tilemapResource = TILE_MAP_RESOURCE,
    goToNextLevel = goToNextLevel
) {

    companion object {
        const val NAME = "level2"
        const val TILE_MAP_RESOURCE = R.raw.level2
    }

    private val angleReference = preferences(gameView.context) { angleReference }

    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )

    private var isUpsideDown = false
    private val score : Int = 50

    override fun clean() {
        super.clean()
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)
        isUpsideDown = inputState.isUpsideDown(angleReference)
    }

    override fun update(delta: Float) {
        super.update(delta)

        player.flipUpsideDown(isUpsideDown)
        player.changeRotation(
            if (isUpsideDown) { Player.ROTATION.REVERSED }
            else { Player.ROTATION.STRAIGHT }
        )

        if (player.isTouchingLevel3) {
            val s = score - 5 * player.deathNumber
            println("test bug $s")
            preferences.scoreLevelTwo = if (s >= 0) s else 0
            goToNextLevel(NAME)
        }
    }

    override fun onSaveState() {
    }

    override fun render() {
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.parseColor("#34202b"))

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