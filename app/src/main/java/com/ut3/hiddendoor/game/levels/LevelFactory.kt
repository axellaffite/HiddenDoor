package com.ut3.hiddendoor.game.levels

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.ut3.hiddendoor.MainActivity
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.levels.level3.HiddenKeyLevel
import com.ut3.hiddendoor.game.logic.GameLogic

object LevelFactory {
    fun getLevel(levelName: String, gameView: GameView, gameLogic: GameLogic, activity: Activity) = when(levelName) {
        IntroductionLevel.NAME -> IntroductionLevel(gameView, activity)
        HomeLevel.NAME -> HomeLevel(gameView) {
            Handler(Looper.getMainLooper()).post {
                gameLogic.stop()

                val intent = Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
        HiddenKeyLevel.NAME -> HiddenKeyLevel(gameView)
        else -> null
    }
}