package com.ut3.hiddendoor.game.logic

import android.view.MotionEvent
import com.ut3.hiddendoor.game.utils.Vector2f
import com.ut3.hiddendoor.game.utils.Vector3f
import com.ut3.hiddendoor.game.utils.length

interface InputState {
    val touchEvent: MotionEvent?
    val luminosity: Float
    val acceleration: Vector3f
    val orientation: Vector3f
}

data class MutableInputState(
    override var touchEvent: MotionEvent? = null,
    override var acceleration: Vector3f = Vector3f(0f, 0f, 0f),
    override var luminosity: Float = 0f,
    override var orientation: Vector3f = Vector3f(0f, 0f, 0f)
): InputState

fun InputState.isShaking(accelerationReference: Vector3f) =
    acceleration.length >= (accelerationReference.length * 3f / 4f)