package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.drawable.draw
import com.ut3.hiddendoor.game.drawable.tiledmap.TiledMap
import com.ut3.hiddendoor.game.logic.Entity

class Key(
    private val x: Int,
    private val y: Int,
    private val tilemap: TiledMap,
) : Drawable, Entity {


    override val rect = let {
        val left = x * tilemap.tileSize
        val top = y * tilemap.tileSize
        val right = left + tilemap.tileSize
        val bottom = top + tilemap.tileSize

        ImmutableRect(left, top, right, bottom)
    }

    override fun update(delta: Float) {
        super.update(delta)
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        surfaceHolder.draw(bounds,this,paint)
    }

}