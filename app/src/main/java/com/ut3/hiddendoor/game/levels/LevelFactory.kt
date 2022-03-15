package com.ut3.hiddendoor.game.levels

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.ut3.hiddendoor.MainActivity
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.levels.level3.HiddenKeyLevel
import com.ut3.hiddendoor.game.levels.leveltwo.LevelTwo
import com.ut3.hiddendoor.game.logic.GameLogic
import com.ut3.hiddendoor.game.utils.Preferences

object LevelFactory {
    fun getLevel(levelName: String, gameView: GameView, gameLogic: GameLogic, activity: Activity) = when(levelName) {
        HomeLevel.NAME -> HomeLevel(gameView) { levelToLoad ->
            Handler(Looper.getMainLooper()).post {
                gameLogic.stop()

                Preferences(activity).currentLevel = getLevelName(levelToLoad)

                val intent = Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
        HiddenKeyLevel.NAME -> HiddenKeyLevel(gameView)

        IntroductionLevel.NAME -> IntroductionLevel(gameView, activity)

        LevelTwo.NAME -> LevelTwo(gameView)
        else -> null
    }

    private fun getLevelName(levelToLoad: Int): String {
        return when (levelToLoad) {
            1 -> IntroductionLevel.NAME
            2 -> LevelTwo.NAME
            3 -> HiddenKeyLevel.NAME
            else -> IntroductionLevel.NAME
        }
    }
}