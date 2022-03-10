package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.levels.introduction.IntroductionLevel
import com.ut3.hiddendoor.game.logic.EntityManager
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player

class HiddenKeyLevel(private val gameView: GameView) : EntityManager(){
    companion object {
        const val TILE_MAP_RESOURCE = R.raw.testmap
    }
    private var fps = 0
    private val tilemap = gameView.context.loadTiledMap(IntroductionLevel.TILE_MAP_RESOURCE)
    private val hud = createHud(gameView) { controlButtons.isBVisible = false }
    private val player = createEntity { Player(gameView, tilemap, hud) }
    private val key = createEntity {
        Key(x=0,y=0,tilemap= tilemap)
    }

    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )


    override fun onLoad() {
        TODO("Not yet implemented")
    }

    override fun onSaveState() {
        TODO("Not yet implemented")
    }

    override fun handleInput(inputState: InputState) {
        super.handleInput(inputState)
    }

    override fun render() {
        super.render()
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)
                    // Add key
                    canvas.draw(key,paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)


                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }
}