package com.ut3.hiddendoor

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.homelevel.HomeLevel

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        gameView = GameView(this, levelToLoad = HomeLevel.NAME)
        setContentView(gameView)
    }

}