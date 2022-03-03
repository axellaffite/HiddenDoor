package com.ut3.hiddendoor.game.drawable

import android.graphics.*

class DrawableRect(rect: RectF) : Drawable(rect) {
    override fun drawOnCanvas(surfaceHolder: Canvas, paint: Paint) {
        paint.color = Color.RED
        surfaceHolder.drawRect(rect, paint)
    }
}