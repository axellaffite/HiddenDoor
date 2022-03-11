package com.ut3.hiddendoor.game.levels

import android.graphics.Color
import android.graphics.RectF
import android.media.MediaPlayer
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.cameras.Camera
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.levels.introduction.Bridge
import com.ut3.hiddendoor.game.levels.introduction.Lever
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.utils.Vector2f

class HomeLevel(private val gameView: GameView) : EntityManager() {

    companion object {
        const val TILE_MAP_RESOURCE = R.raw.home
        const val NAME = "home"
    }

    private lateinit var sound: MediaPlayer

    private var fps = 0
    private val tilemap = gameView.context.loadTiledMap(TILE_MAP_RESOURCE)
    private val hud = createHud(gameView) { controlButtons.isBVisible = false }
    private val player = createEntity { Player(gameView, tilemap, hud) }
    private val bridge = createEntity {
        Bridge(x = 18, y = 29, blockCount = 8, tilemap = tilemap, player = player)
    }
    private val lever = createEntity { Lever(gameView, hud, player, bridge) { move(200f, 448f) } }

    private val camera = Camera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, tilemap.tileSize*90, tilemap.tileSize*90)
    ).apply {
        centerOn(Vector2f(tilemap.rect.width/2,tilemap.rect.height/2))
    }

    override fun onLoad() {
        sound = MediaPlayer.create(gameView.context, R.raw.ambiance_sound).apply {
            isLooping = true
            start()
        }
    }

    override fun onSaveState() {
        TODO("save state of the level")
    }

    override fun render() {
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 75f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)

                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }
}