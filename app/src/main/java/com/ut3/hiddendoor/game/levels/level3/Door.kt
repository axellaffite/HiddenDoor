package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.hud.HUD
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.drawable.tiledmap.TiledMap
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player

class Door(
    val gameView: GameView, private val hud: HUD,
    private val tilemap: TiledMap,
    private val player: Player,
    private val key: Key,
    conf: Door.() -> Unit
) : AnimatedSprite(gameView.context, R.raw.door, "close") {

    var doorOpened = false

    init {
        conf()
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)
    }

    override fun update(delta: Float) {
        super.update(delta)
        hud.controlButtons.isBVisible = (rect.intersects(player.rect) && !doorOpened && key.playerHasKey) || (key.rect.intersects(player.rect) && !key.playerHasKey)
        if (hud.controlButtons.isBPressed && rect.intersects(player.rect) && key.playerHasKey) {
            setAction("open")
            doorOpened = true
        }
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        super.drawOnCanvas(bounds, surfaceHolder, paint)
    }

}