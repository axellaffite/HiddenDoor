package com.ut3.hiddendoor.game.drawable.hud

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.InputState

class Joystick(screenSize: RectF) : Drawable, Entity {

    enum class Movement { Left, Right, None }

    private val height = screenSize.height() / 5f

    override val rect = ImmutableRect(
        20f,
        screenSize.bottom - (height) - 20f,
        20f + (height * 2.5f),
        screenSize.bottom - 20f
    )

    private val leftZone = RectF(
        rect.left,
        rect.top,
        rect.left + rect.width * 1f / 3f,
        rect.bottom
    )

    private val rightZone = RectF(
        rect.right - rect.width * 1f / 3f,
        rect.top,
        rect.right,
        rect.bottom
    )

    var direction: Movement = Movement.None; private set
    private var targetPointer = -1

    override fun handleInput(inputState: InputState) {
        val event = inputState.touchEvent
            ?.takeIf { targetPointer == -1 || targetPointer == it.actionIndex }
            ?: return

        direction = when(event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_MOVE -> when {
                // Not the right height
                event.y !in rect.top .. rect.bottom -> Movement.None
                // Left side of the button
                event.x in leftZone.left .. leftZone.right -> Movement.Left
                // Right side of the button
                event.x in rightZone.left .. rightZone.right -> Movement.Right
                else -> Movement.None
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                targetPointer = -1
                Movement.None
            }

            else -> {
                Movement.None
            }
        }

        if (direction != Movement.None) {
            targetPointer = event.actionIndex
        }
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) = surfaceHolder.withSave {
        paint.color = Color.WHITE
        paint.alpha = 95
        surfaceHolder.drawRoundRect(rect.copyOfUnderlyingRect, 16f, 16f, paint)
    }

}