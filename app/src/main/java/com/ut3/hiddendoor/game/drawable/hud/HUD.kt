package com.ut3.hiddendoor.game.drawable.hud

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.drawable.draw
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.EntityManager

/**
 * Interface that is shown above all other drawable on screen.
 * This interface allows the user to perform input actions on the game.
 *
 * @param gameView target on which this should be drawn
 */
class HUD(gameView: GameView) : Entity, Drawable, EntityManager() {

    override val rect = ImmutableRect(gameView.rect)

    /** Used to display FPS at each frame */
    private var fps = 0f
    val joystick = createEntity { Joystick(gameView.rect) }
    val controlButtons = createEntity { ControlButtons(gameView) }

    override fun onLoad() = Unit
    override fun onSaveState() = Unit

    override fun update(delta: Float) {
        super<EntityManager>.update(delta)

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

/**
 * Utility function to register a HUD as an automatically-updated entity.
 *
 * @param gameView target on which the [HUD] should be drawn
 * @param config
 * @return
 */
fun EntityManager.createHud(gameView: GameView, config: HUD.() -> Unit = {}) =
    createEntity { HUD(gameView).apply(config) }