package com.ut3.hiddendoor

import android.graphics.RectF
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ut3.hiddendoor.game.GameLogic
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.Camera

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val gameView = GameView(this)
        setContentView(gameView)

        val camera = Camera(
            screenPosition = RectF(100f,100f,300f,300f),
            gamePosition = RectF(0f, 0f, 200f, 200f)
        )

        val logic = GameLogic(gameView, camera)
        logic.start()
    }
}