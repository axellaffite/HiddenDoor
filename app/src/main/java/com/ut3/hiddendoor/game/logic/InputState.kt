package com.ut3.hiddendoor.game.logic

import android.view.MotionEvent
import com.ut3.hiddendoor.game.utils.Vector2f
import com.ut3.hiddendoor.game.utils.Vector3f

interface InputState {
    val touchEvent: MotionEvent?
    val luminosity: Float
    val acceleration: Vector3f
    val orientation: Vector3f
}

data class MutableInputState(
    override var touchEvent: MotionEvent?,
    override var acceleration: Vector3f,
    override var luminosity: Float,
    override var orientation: Vector3f
): InputState