package com.ut3.hiddendoor.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.ut3.hiddendoor.game.drawable.Drawable

class GameView(context: Context): SurfaceView(context), SurfaceHolder.Callback {
    override fun surfaceCreated(holder: SurfaceHolder) = Unit
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit
    override fun surfaceDestroyed(holder: SurfaceHolder) = Unit

    fun clear() {
        holder?.withLock { it.drawColor(0, PorterDuff.Mode.CLEAR) }
    }

    fun draw(drawable: Drawable) {
        holder?.withLock { canvas ->
            drawable.draw(canvas)
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