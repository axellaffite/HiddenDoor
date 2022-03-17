package com.ut3.hiddendoor.game.levels

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.ut3.hiddendoor.MainActivity
import com.ut3.hiddendoor.ScoreActivity
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.levels.level3.HiddenKeyLevel
import com.ut3.hiddendoor.game.levels.leveltwo.LevelTwo
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.GameLogic
import com.ut3.hiddendoor.game.utils.Preferences

object LevelFactory {
    fun getLevel(levelName: String, gameView: GameView, gameLogic: GameLogic, activity: Activity): EntityManager? {
        val nextLevel = goToNextLevel(activity, gameLogic)
        return when(levelName) {
            HomeLevel.NAME -> HomeLevel(gameView, goToScore(activity) ) { levelToLoad ->
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


            IntroductionLevel.NAME -> IntroductionLevel(gameView, nextLevel)

            LevelTwo.NAME -> LevelTwo(gameView, nextLevel)

            HiddenKeyLevel.NAME -> HiddenKeyLevel(gameView, nextLevel)

            else -> null
        }
    }

    private fun getLevelName(levelToLoad: Int): String {
        return when (levelToLoad) {
            1 -> IntroductionLevel.NAME
            2 -> LevelTwo.NAME
            3 -> HiddenKeyLevel.NAME
            else -> HomeLevel.NAME
        }
    }

    private fun goToNextLevel(activity: Activity, gameLogic: GameLogic): (level: String) -> Unit {
        return { level ->
            val nextLevel = when (level) {
                IntroductionLevel.NAME -> LevelTwo.NAME
                LevelTwo.NAME -> HiddenKeyLevel.NAME
                HiddenKeyLevel.NAME -> HomeLevel.NAME
                else -> HomeLevel.NAME
            }

            Handler(Looper.getMainLooper()).post {
                gameLogic.stop()
                Preferences(activity).currentLevel = nextLevel

                val intent = Intent(activity, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }

    fun goToScore(activity: Activity): () -> Unit {
        return {
            Handler(Looper.getMainLooper()).post {

                val intent = Intent(activity, ScoreActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
                }
                activity.startActivity(intent)
                activity.finish()
            }
        }
    }
}