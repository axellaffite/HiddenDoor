package com.ut3.hiddendoor.game.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import androidx.core.graphics.translationMatrix

/**
 * Class that is used to efficiently display drawables.
 * This can also be used to move the camera into the virtual world
 * and also on the screen.
 *
 * @property screenPosition position of the camera on the screen
 * @property gamePosition position of the camera in the game
 */
class Camera(private val screenPosition: RectF, private val gamePosition: RectF) {

    constructor(other: Camera): this(
        screenPosition = RectF(other.screenPosition),
        gamePosition = RectF(other.gamePosition)
    )

    fun draw(canvas: Canvas, paint: Paint, block: Camera.(Canvas, Paint) -> Unit) {
        // We need to save the canvas' state to draw with the current
        //
        canvas.save()
        canvas.clipRect(screenPosition)
        canvas.concat(
            translationMatrix(
                -gamePosition.left + screenPosition.left,
                -gamePosition.top + screenPosition.top
            )
        )

        block(this, canvas, paint)
        canvas.restore()
    }

    fun Canvas.fill(color: Int) = drawColor(color)

    fun Canvas.clear() = drawColor(0, PorterDuff.Mode.CLEAR)

    fun Canvas.draw(paint: Paint, drawable: Drawable) {
        drawable.draw(gamePosition, this, paint)
    }

    fun moveOnScreen(offsetX: Float = 0f, offsetY: Float = 0f) {
        screenPosition.offset(offsetX, offsetY)
    }

    fun moveInGame(offsetX: Float = 0f, offsetY: Float = 0f) {
        gamePosition.offset(offsetX, offsetY)
    }

    fun contains(drawable: Drawable): Boolean {
        return gamePosition.contains(drawable.rect)
    }

}