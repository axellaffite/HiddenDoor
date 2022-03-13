package com.ut3.hiddendoor

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ut3.hiddendoor.game.logic.GameLogic
import com.ut3.hiddendoor.game.GameView

class MainActivity : AppCompatActivity() {

    lateinit var logic: GameLogic
    lateinit var gameView: GameView

    override fun onPause() {
        if (this::logic.isInitialized) {
            logic.stop()
        }

        super.onPause()
    }

    override fun onResume() {
        if (this::logic.isInitialized) {
            logic.start()
        }

        setContentView(gameView)

        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        gameView = GameView(this)
        setContentView(gameView)

        gameView.post {
            logic = GameLogic(gameView)
            logic.start()
        }
    }
}