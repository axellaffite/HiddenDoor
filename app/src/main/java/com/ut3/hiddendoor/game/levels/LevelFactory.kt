package com.ut3.hiddendoor.game.levels

import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel

object LevelFactory {
    fun getLevel(levelName: String, gameView: GameView) = when(levelName) {
        IntroductionLevel.NAME -> IntroductionLevel(gameView)
        else -> null
    }
}