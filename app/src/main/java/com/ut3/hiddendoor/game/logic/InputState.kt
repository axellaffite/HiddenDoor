package com.ut3.hiddendoor.game.logic

import android.view.MotionEvent
import com.ut3.hiddendoor.game.utils.Vector2f
import com.ut3.hiddendoor.game.utils.Vector3f

interface InputState {
    val touchEvent: MotionEvent?
    val luminosity: Float
    val acceleration: Vector2f
    val orientation: Vector2f
    val rotation: Vector3f
    val upsideDown : Boolean
}

data class MutableInputState(
    override var touchEvent: MotionEvent?,
    override var acceleration: Vector2f,
    override var luminosity: Float,
    override var orientation: Vector2f,
    override var rotation: Vector3f,
    override var upsideDown : Boolean
): InputState