package com.ut3.hiddendoor.game.drawable

import android.graphics.Canvas
import android.view.SurfaceHolder

fun interface Drawable {
    fun draw(surfaceHolder: Canvas)
}