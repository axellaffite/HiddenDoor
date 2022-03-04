package com.ut3.hiddendoor.game.drawable

import android.graphics.*

class DrawableRect(override val rect: RectF) : Drawable() {
    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        paint.color = Color.RED
        surfaceHolder.drawRect(rect, paint)
    }
}