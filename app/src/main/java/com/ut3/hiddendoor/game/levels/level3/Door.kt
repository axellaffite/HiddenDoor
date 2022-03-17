package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.hud.HUD
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player

class Door(
    val gameView: GameView,
    conf: Door.() -> Unit
) : AnimatedSprite(gameView.context, R.raw.door, "close") {

    var doorOpened = false

    init {
        conf()
    }

}