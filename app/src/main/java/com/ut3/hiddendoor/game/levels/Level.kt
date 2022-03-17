package com.ut3.hiddendoor.game.levels

import android.media.MediaPlayer
import androidx.annotation.CallSuper
import androidx.annotation.RawRes
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.hud.HomeButton
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.loadBitmapKeepSize
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.utils.Preferences

abstract class Level(
    protected val gameView: GameView,
    @RawRes private val soundRes: Int,
    @RawRes tilemapResource: Int,
    val goToNextLevel: (String) -> Unit
): EntityManager() {

    protected val tilemap = gameView.context.loadTiledMap(tilemapResource)
    protected val hud = createHud(gameView,goToNextLevel) { controlButtons.isBVisible = false }
    protected val player = createEntity { Player(gameView, tilemap, hud) { setPosition(tilemap.initialPlayerPosition, tilemap.tileSize) } }
    protected val preferences = Preferences(gameView.context)

    protected lateinit var sound: MediaPlayer

    @CallSuper
    override fun onLoad() {
        hud.homeVisible = true
        sound = MediaPlayer.create(gameView.context, soundRes).apply {
            isLooping = true
            start()
        }
    }
    override fun clean() {
        super.clean()
        sound.stop()
        sound.release()
    }

}