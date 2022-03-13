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
        const val TILE_MAP_RESOURCE = R.raw.hiddenkeymap
        const val NAME = "hiddenKeyLevel"
    }
    private val tilemap = gameView.context.loadTiledMap(HiddenKeyLevel.TILE_MAP_RESOURCE)
    private val hud = createHud(gameView) { controlButtons.isBVisible = false }
    private val player = createEntity { Player(gameView, tilemap, hud) }
    private val key = createEntity {
        Key(gameView,hud,tilemap ,player) { move(300f, 350f ) }
    }
    private val door = createEntity {
        Door(gameView,hud,tilemap,player) { move( 544f, 392f)}
    }

    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )


    override fun onLoad() {
    }

    override fun onSaveState() {
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
                    canvas.draw(door,paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)

                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }
}