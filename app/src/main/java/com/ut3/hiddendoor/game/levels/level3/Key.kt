package com.ut3.hiddendoor.game.levels.level3

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.drawable.hud.HUD
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.drawable.tiledmap.TiledMap
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Player
import com.ut3.hiddendoor.game.logic.isShaking
import com.ut3.hiddendoor.game.utils.Preferences
import com.ut3.hiddendoor.game.utils.Vector3f


class Key(
    val gameView: GameView,
    private val hud: HUD,
    private val tilemap: TiledMap,
    private val player: Player,
    private val preferences: Preferences,
    conf: Key.() -> Unit
) : AnimatedSprite(gameView.context, R.raw.key, "silver") {

    companion object {
        const val SPEED = 8f
    }

    private val reference = preferences.referenceState

    var dx = 0f
    var dy = 0f
    var keyAvailable = false
    var playerHasKey = false

    private var shakingTime = 0f;
    private var isShaking = false;

    var accelerometerLevel = Vector3f(0f, 0f, 0f)

    init {
        conf()
    }

    private fun applyGravity(isTouchingGround: Boolean, delta: Float) {
        if (!isTouchingGround) {
            dy += (12.2f * delta)
        }
    }

    private fun isTouchingGround(): Boolean {
        return tilemap.collisionTilesIntersecting(
            RectF(rect.left, rect.bottom, rect.right, rect.bottom + 1f)
        ).any { tileValue -> tileValue == 1 }
    }

    fun updatePosition(delta: Float) {
        let {
            val tmp = rect.copyOfUnderlyingRect.apply { offset(0f, dy * delta * SPEED) }
            if (tilemap.collisionTilesIntersecting(tmp).any { it == 1 }) {
                dy = 0f
            } else {
                rect = ImmutableRect(tmp)
            }
        }

        val tmp = rect.copyOfUnderlyingRect.apply { offset(dx * delta * SPEED, 0f) }
        if (!tilemap.collisionTilesIntersecting(tmp).any { it == 1 }) {
            rect = ImmutableRect(tmp)
        }

    }

    override fun handleInput(inputState: InputState) {
        if(!keyAvailable) {
            isShaking = inputState.isShaking(reference.acceleration)
        }

    }

    override fun update(delta: Float) {
        super.update(delta)
        if(isShaking && !keyAvailable) {
            shakingTime += delta
            if (shakingTime > 1) {
                keyAvailable = true
            }
        }
        if (keyAvailable) {
            applyGravity(isTouchingGround(), delta)
            updatePosition(delta)
        }
        if (!playerHasKey) {
            if (isTouchingGround()) {
                setAction("gold")
            }
            hud.controlButtons.isBVisible = rect.intersects(player.rect) && !playerHasKey
            if (hud.controlButtons.isBPressed && rect.intersects(player.rect)) {
                setAction("destroy")
                playerHasKey = true
            }
        }
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        super.drawOnCanvas(bounds, surfaceHolder, paint)
    }
}