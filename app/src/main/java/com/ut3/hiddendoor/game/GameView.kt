package com.ut3.hiddendoor.game

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.ut3.hiddendoor.game.drawable.Camera
import com.ut3.hiddendoor.game.drawable.Drawable

class GameView(context: Context): SurfaceView(context) {
    private val drawingContext = DrawingContext()

    fun draw(paint: Paint = Paint(), block: DrawingContext.() -> Unit) {
        holder?.withLock { canvas ->
            drawingContext.use(paint, canvas, block)
        }
    }

    val rect get() = Rect(0, 0, width, height)

    inner class DrawingContext {
        private var paint: Paint = Paint()
        private var canvas: Canvas = Canvas()

        fun use(paint: Paint, canvas: Canvas, block: DrawingContext.() -> Unit) {
            this.paint = paint
            this.canvas = canvas
            block()
        }

        fun withCamera(camera: Camera, block: Camera.(Canvas, Paint) -> Unit) {
            camera.draw(canvas, paint, block)
        }

        fun clear() = canvas.drawColor(0, PorterDuff.Mode.CLEAR)

        fun fill(color: Int) = canvas.drawColor(color)

        fun Canvas.draw(paint: Paint, drawable: Drawable) {
            drawable.draw(RectF(0f, 0f, width.toFloat(), height.toFloat()), this, paint)
        }

        fun draw(drawable: Drawable) {
            canvas.draw(paint, drawable)
        }
    }
}

fun <T> SurfaceHolder.withLock(body: (Canvas) -> T) {
    var canvas: Canvas? = null
    try {
        canvas = lockCanvas()
        body(canvas)
    } catch (e: Exception) {

    } finally {
        runCatching {
            canvas?.let { unlockCanvasAndPost(it) }
        }
    }
}