package com.ut3.hiddendoor.game.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.RectF.intersects
import androidx.core.graphics.transform

abstract class Drawable {
    abstract val rect: RectF
    protected abstract fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint)

    fun draw(bounds: RectF, target: Canvas, paint: Paint): Boolean {
        if (intersects(bounds, rect)) {
            drawOnCanvas(bounds, target, paint)
            return true
        }

        return false
    }
}