package com.ut3.hiddendoor.game

import android.os.Handler

class GameLogic(private val view: GameView): Thread() {
    var isRunning = false; private set

    override fun start() {
        isRunning = true
        super.start()
    }

    override fun run() {
        if (isRunning) {
            view.clear()
            view.draw()
        }
    }
}