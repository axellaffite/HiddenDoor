package com.ut3.hiddendoor.game.drawable.tiledmap

import android.graphics.*
import android.graphics.RectF.intersects
import com.ut3.hiddendoor.game.drawable.Drawable

class Chunk(
    private val vertices: FloatArray,
    private val textCoordinates: FloatArray,
    private val tileset: Tileset,
    override val rect: RectF
) : Drawable() {

    init {
        println("init chunk: $rect")
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        val texture = Paint(paint).apply {
            shader = BitmapShader(tileset.bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            isAntiAlias = false
            isDither = true
            isFilterBitmap = false
        }

        surfaceHolder.drawVertices(
            Canvas.VertexMode.TRIANGLES,
            vertices.size,
            vertices,
            0,
            textCoordinates,
            0,
            null,
            0,
            null,
            0,
            0,
            texture
        )

//        for (y in 0 until tileset.height) {
//            for (x in 0 until tileset.width) {
//                val left = x * tileset.tileSize
//                val top = y * tileset.tileSize
//                val right = left + tileset.tileSize
//                val bottom = top + tileset.tileSize
//                surfaceHolder.drawRect(
//                    Rect(left, top, right, bottom),
//                    Paint().apply { this.style = Paint.Style.STROKE; color = Color.WHITE }
//                )
//            }
//        }
    }
}