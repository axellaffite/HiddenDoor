package com.ut3.hiddendoor.game.drawable.cameras

import android.graphics.RectF
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.Level
import com.ut3.hiddendoor.game.utils.Vector2f

class TrackingCamera(
    screenPosition: RectF,
    gamePosition: RectF,
    private val toFollow: () -> Vector2f
): Camera(screenPosition, gamePosition), Entity {

    override fun postUpdate(delta: Float) {
        centerOn(toFollow())
    }

}

fun Level.createTrackingCamera(screenPosition: RectF, gamePosition: RectF, track: () -> Vector2f) =
    createEntity { TrackingCamera(screenPosition, gamePosition, track) }