package com.ut3.hiddendoor.game.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.RectF.intersects

abstract class Drawable(val rect: RectF) {

    protected abstract fun drawOnCanvas(surfaceHolder: Canvas, paint: Paint)

    fun draw(bounds: RectF, target: Canvas, paint: Paint) {
        if (intersects(bounds, rect)) {
            drawOnCanvas(target, paint)
        }
    }
}