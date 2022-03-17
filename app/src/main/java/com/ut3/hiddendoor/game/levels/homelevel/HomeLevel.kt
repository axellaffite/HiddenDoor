package com.ut3.hiddendoor.game.levels.homelevel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.media.MediaPlayer
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.ScoreActivity
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.TextPopUp
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.levels.LevelFactory
import com.ut3.hiddendoor.game.levels.level3.Door
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.utils.Vector2f
import kotlinx.coroutines.withTimeout

class HomeLevel(
    private val gameView: GameView,
    private val goToScore: () -> Unit,
    private val launchNewActivity: (Int) -> Unit
) : EntityManager() {

    companion object {
        const val TILE_MAP_RESOURCE = R.raw.home
        const val NAME = "home"
    }

    private lateinit var sound: MediaPlayer
    private var popup : TextPopUp? = null
    private var levelTouched = -1
    private var quitHome = false
    private val tilemap = gameView.context.loadTiledMap(TILE_MAP_RESOURCE)
    private val hud = createHud(gameView) { controlButtons.isBVisible = false }
    private val door = createEntity {
        Door(gameView) { move( 144f, 489f)}
    }
    private val player = createEntity { Player(gameView, tilemap, hud) { setPosition(tilemap.initialPlayerPosition, tilemap.tileSize) } }
    private var levelLaunched = false

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
        runCatching{
            sound.stop()
            sound.release()
        }

    }

    override fun onSaveState() {
        TODO("save state of the level")
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)

        if (!levelLaunched && levelTouched != -1 && hud.controlButtons.isBPressed) {
            levelLaunched = true
            launchNewActivity(levelTouched)
        }

        if (!quitHome && door.rect.intersects(player.rect) && !door.doorOpened && hud.controlButtons.isBPressed){
            door.setAction("open")
            door.doorOpened = true
            quitHome = true
            goToScore()
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        levelTouched = when {
            player.isTouchingLevel1 -> 1
            player.isTouchingLevel2 -> 2
            player.isTouchingLevel3 -> 3
            player.isTouchingLevel4 -> 4
            else -> -1
        }
        popup = levelTouched.takeIf { it != -1 }?.let { TextPopUp("Play level $it", Vector2f(player.rect.left, player.rect.top)) }
        hud.controlButtons.isBVisible = popup != null || (door.rect.intersects(player.rect) && !door.doorOpened)
    }

    override fun render() {
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 17f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)
                    canvas.draw(door,paint)
                    paint.color = Color.RED
                    canvas.draw(player, paint)
                    popup?.let {
                        canvas.draw(it, paint)
                    }
                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }
}