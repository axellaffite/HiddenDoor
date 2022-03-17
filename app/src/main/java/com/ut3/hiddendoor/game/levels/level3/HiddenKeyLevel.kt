package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.levels.Level
import com.ut3.hiddendoor.game.logic.InputState

class HiddenKeyLevel(
    gameView: GameView,
    goToNextLevel: (String) -> Unit
) : Level(
    gameView = gameView,
    soundRes = R.raw.ambiance_sound,
    tilemapResource = TILE_MAP_RESOURCE,
    goToNextLevel = goToNextLevel
) {

    companion object {
        const val TILE_MAP_RESOURCE = R.raw.hiddenkeymap
        const val NAME = "hiddenKeyLevel"
    }

    var startTime = System.currentTimeMillis()

    private val score = 25f

    private var levelFinished = false
    private val key = createEntity {
        Key(gameView,hud,tilemap ,player,preferences) { move(300f, 350f ) }
    }
    private val door = createEntity {
        Door(gameView) { move( 544f, 392f)}
    }

    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )

    override fun clean() {
        super.clean()
    }

    override fun onSaveState() {
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)
        if (!levelFinished && hud.controlButtons.isBPressed && door.doorOpened && player.rect.intersects(door.rect)) {
            updateScore()
            levelFinished = true
            goToNextLevel(NAME)
        }
    }

    override fun render() {
        super.render()
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)
                    canvas.draw(key,paint)
                    canvas.draw(door,paint)
                    canvas.draw(player, paint)
                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        hud.controlButtons.isBVisible =
            (door.rect.intersects(player.rect) && key.playerHasKey)
                    || (key.rect.intersects(player.rect) && !key.playerHasKey)
        if (door.rect.intersects(player.rect) && key.playerHasKey) {
            door.setAction("open")
            door.doorOpened = true
        }
    }

    private fun updateScore() {
        var penalty =  (System.currentTimeMillis() - startTime) / 1000 / 10
        var s = score - 2 * penalty
        preferences.scoreLevelThree = if (s >= 0f) s else 0f
    }
}