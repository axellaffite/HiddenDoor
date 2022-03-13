package com.ut3.hiddendoor

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.HomeLevel
import com.ut3.hiddendoor.game.logic.GameLogic

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        gameView = GameView(this)
        setContentView(gameView)
    }

}