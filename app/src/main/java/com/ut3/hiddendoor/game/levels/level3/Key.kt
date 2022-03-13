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
import com.ut3.hiddendoor.game.utils.Vector3f


class Key(
    val gameView: GameView,
    private val hud: HUD,
    private val tilemap: TiledMap,
    private val player: Player,
    conf: Key.() -> Unit
) : AnimatedSprite(gameView.context, R.raw.key, "silver") {

    companion object {
        const val SPEED = 8f
        const val SHAKE_THRESHOLD = 800
    }

    var dx = 0f
    var dy = 0f
    var keyAvailable = false
    var isAvailable = true

    private var lastUpdateAccelerometer = 0L

    var accelerometerLevel = Vector3f(0f, 0f, 0f)
    var lastAccelerometerLevel = Vector3f(0f, 0f, 0f)

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

    fun updatePosition(isTouchingGround: Boolean, delta: Float) {
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
        accelerometerLevel = inputState.acceleration
        if (!keyAvailable) {
            shakeDetector()
        }
    }

    override fun update(delta: Float) {
        super.update(delta)
        if (keyAvailable) {
            applyGravity(isTouchingGround(), delta)
            updatePosition(isTouchingGround(), delta)
        }
        if (isAvailable) {
            if (isTouchingGround()) {
                setAction("gold")
            }
            hud.controlButtons.isBVisible = rect.intersects(player.rect)
            if (hud.controlButtons.isBPressed && rect.intersects(player.rect)) {
                setAction("destroy")
                isAvailable = false
                hud.controlButtons.isBVisible = false
            }
        }
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        super.drawOnCanvas(bounds, surfaceHolder, paint)
    }

    private fun shakeDetector() {
        var curTime = System.currentTimeMillis()
        if (curTime - lastUpdateAccelerometer > 100) {
            val diffTime: Long = curTime - lastUpdateAccelerometer
            lastUpdateAccelerometer = curTime
            val x = accelerometerLevel.x
            val y = accelerometerLevel.y
            val z = accelerometerLevel.z
            val speed: Float =
                Math.abs(x + y + z - lastAccelerometerLevel.x - lastAccelerometerLevel.y - lastAccelerometerLevel.z) / diffTime * 10000
            if (speed > SHAKE_THRESHOLD) {
                keyAvailable = true
            }
            lastAccelerometerLevel = accelerometerLevel
        }
    }

}