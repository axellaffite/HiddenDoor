package com.ut3.hiddendoor.game

import android.content.Context
import android.graphics.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.ut3.hiddendoor.game.drawable.cameras.Camera

class GameView(context: Context): SurfaceView(context) {
    private val drawingContext = DrawingContext()

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        keepScreenOn = true
    }

    fun draw(paint: Paint = Paint(), block: DrawingContext.(Canvas, Paint) -> Unit) {
        holder?.withLock { canvas ->
            drawingContext.use(paint, canvas, block)
        }
    }

    val rect get() = Rect(0, 0, width, height)

    inner class DrawingContext {
        private var paint: Paint = Paint()
        private var canvas: Canvas = Canvas()

        fun use(paint: Paint, canvas: Canvas, block: DrawingContext.(Canvas, Paint) -> Unit) {
            this.paint = paint
            this.canvas = canvas
            block(canvas, paint)
        }

        fun withCamera(camera: Camera, block: Camera.(Canvas, Paint) -> Unit) {
            val tmpPaint = Paint(paint)
            camera.draw(canvas, tmpPaint, block)
        }

        fun Canvas.clear() = drawColor(0, PorterDuff.Mode.CLEAR)
    }
}

fun <T> SurfaceHolder.withLock(body: (Canvas) -> T) {
    var canvas: Canvas? = null
    try {
        canvas = lockHardwareCanvas()
        body(canvas)
    } catch (e: Exception) {

    } finally {
        runCatching {
            canvas?.let { unlockCanvasAndPost(it) }
        }
    }
}