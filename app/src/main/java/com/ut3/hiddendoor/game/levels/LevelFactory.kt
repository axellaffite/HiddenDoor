package com.ut3.hiddendoor.game.levels

import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.levels.level3.HiddenKeyLevel

object LevelFactory {
    fun getLevel(levelName: String, gameView: GameView) = when(levelName) {
        IntroductionLevel.NAME -> IntroductionLevel(gameView)
        HomeLevel.NAME -> HomeLevel(gameView)
        HiddenKeyLevel.NAME -> HiddenKeyLevel(gameView)
        else -> null
    }
}