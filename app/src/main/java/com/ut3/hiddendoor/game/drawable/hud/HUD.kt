package com.ut3.hiddendoor.game.drawable.hud

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Level

class HUD(gameView: GameView) : Entity, Drawable {

    override val rect: RectF = gameView.rect
    val joystick = Joystick(gameView.rect)
    val controlButtons = ControlButtons(gameView)
    private var fps = 0f

    override fun handleInput(inputState: InputState) {
        joystick.handleInput(inputState)
        controlButtons.handleInput(inputState)
    }

    override fun update(delta: Float) {
        joystick.update(delta)
        fps = (1f / delta)
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        val buttonPaint = Paint(paint).apply { alpha = 200 }
        surfaceHolder.draw(bounds, joystick, buttonPaint)
        surfaceHolder.draw(bounds, controlButtons, buttonPaint)

        paint.color = Color.WHITE
        paint.textSize = 50f
        surfaceHolder.drawText("${fps.toInt()} fps", 50f, 50f, paint)
    }

}

fun Level.createHud(gameView: GameView, config: HUD.() -> Unit = {}) =
    createEntity { HUD(gameView).apply(config) }