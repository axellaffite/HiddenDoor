package com.ut3.hiddendoor.game.drawable

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.RectF.intersects
import androidx.core.graphics.transform
import com.ut3.hiddendoor.game.drawable.tiledmap.TiledMap
import com.ut3.hiddendoor.game.drawable.tiledmap.Tileset

interface Drawable {
    val rect: RectF

    fun Canvas.draw(bounds: RectF, drawable: Drawable, paint: Paint) {
        drawable.draw(bounds, this, paint)
    }

    fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint)

    fun draw(bounds: RectF, target: Canvas, paint: Paint): Boolean {
        val intersects = intersects(bounds, rect)
        if (intersects) {
            drawOnCanvas(bounds, target, paint)
            return true
        }

        return false
    }
}