package com.ut3.hiddendoor.game.drawable.hud

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.Circle
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.utils.Vector2f

/**
 * Class responsible for handling the control buttons displayed on the [HUD].
 * This only contains the A and B buttons.
 *
 * @param gameView target on which those buttons should be drawn
 */
class ControlButtons(gameView: GameView) : Drawable, Entity {

    /** Is A button visible */
    var isAVisible = true

    /** Is B button visible */
    var isBVisible = true

    /** Is A button pressed (computed during the [handleInput] hook) */
    var isAPressed = false; private set

    /** Is B button pressed (computed during the [handleInput] hook) */
    var isBPressed = false; private set

    /**
     * For multi-touch purposes.
     * This indicates which pointer has been used to perform the previous action.
     */
    private var targetPointer = -1

    /** Button size on screen */
    private val buttonSize = gameView.height / 10f

    /** 'A' button description */
    private val aButton = Circle(
        centerX = gameView.width - buttonSize - 20f,
        centerY = gameView.height - buttonSize - 20f,
        radius = buttonSize,
        color = Color.RED,
        alpha = 200
    )

    /** 'B' button description */
    private val bButton = aButton.copy(centerX = aButton.centerX - buttonSize * 2f - 20f, color = Color.YELLOW)

    override val rect = ImmutableRect(
        aButton.rect.left,
        aButton.rect.top,
        bButton.rect.right,
        bButton.rect.bottom,
    )

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        if (isAVisible) {
            aButton.drawOnCanvas(bounds, surfaceHolder, paint)
        }

        if (isBVisible) {
            bButton.drawOnCanvas(bounds, surfaceHolder, paint)
        }
    }

    override fun handleInput(inputState: InputState) {
        // Gather only event that matches the previous targetPointer
        // if it was set (!= -1)
        val event = inputState.touchEvent
            ?.takeIf { targetPointer == -1 || it.actionIndex == targetPointer }
            ?: return

        if (
            event.actionMasked == MotionEvent.ACTION_UP ||
            event.actionMasked == MotionEvent.ACTION_POINTER_UP ||
            event.actionMasked == MotionEvent.ACTION_MOVE
        ) {
            isAPressed = false
            isBPressed = false
        } else {
            val (x,y) = event.getX(event.actionIndex) to event.getY(event.actionIndex)
            isAPressed = isAVisible && aButton.contains(Vector2f(x, y))
            isBPressed = isBVisible && !isAPressed && bButton.contains(Vector2f(x, y))
        }
    }

}