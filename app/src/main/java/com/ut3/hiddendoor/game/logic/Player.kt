package com.ut3.hiddendoor.game.logic

import android.graphics.RectF
import android.media.MediaPlayer
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.drawable.hud.HUD
import com.ut3.hiddendoor.game.drawable.hud.Joystick
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.drawable.tiledmap.TiledMap
import com.ut3.hiddendoor.game.utils.Vector2f

class Player(
    gameView: GameView,
    private val tilemap: TiledMap,
    private val hud: HUD,
    configuration: Player.() -> Unit = {}
): Entity,
    Drawable,
    AnimatedSprite(gameView.context, R.raw.character, "idle")
{

    companion object {
        const val SPEED = 12f
    }

    private val runSound = MediaPlayer.create(gameView.context, R.raw.feet_49)
    private var isRunning = false
    private var movement = Joystick.Movement.None
    private var isJumping = false
    private var isDead = false
    private var reactToEnvironment = true
    var dx = 0f
    var dy = 0f

    init {
        reset()
        configuration()
    }

    private fun reset() {
        moveTo(200f, 200f)

        isDead = false
        reactToEnvironment = true
        movement = Joystick.Movement.None
        isJumping = false
        dx = 0f
        dy = 0f
    }

    override fun handleInput(inputState: InputState) {
        if (reactToEnvironment) {
            val event = inputState.touchEvent
            if (event == null) {
                movement = Joystick.Movement.None
                isJumping = false
                return
            }

            movement = hud.joystick.direction
        }
    }

    override fun update(delta: Float) {
        val lastSprite = actionIndexOffset
        super<AnimatedSprite>.update(delta)
        if (shouldBeDead()) {
            die()
            if (isAnimationFinished) {
                reset()
            }
            return
        }

        if (reactToEnvironment) {
            val isTouchingGround = isTouchingGround()
            applyGravity(isTouchingGround, delta)
            moveIfRequired(isTouchingGround, delta)
            jump { hud.controlButtons.isAPressed && isTouchingGround }

            dx = dx.coerceIn(-8f, 8f)
            dy = dy.coerceIn(-16f, 16f)

            updatePosition(isTouchingGround, delta)

            if (lastSprite != actionIndexOffset && isRunning) {
                runSound.start()
            }
        }
    }

    private fun isTouchingGround(): Boolean {
        return tilemap.collisionTilesIntersecting(
            RectF(rect.left, rect.bottom, rect.right, rect.bottom + 1f)
        ).any { tileValue -> tileValue == 1 }
    }

    private fun shouldBeDead(): Boolean {
        return isDead || tilemap.collisionTilesIntersecting(rect.copyOfUnderlyingRect)
            .any { it == 0 }
    }

    fun die() {
        if (!isDead) {
            reactToEnvironment = false
            isDead = true
            setAction("hit", isBitmapReversed)
        }
    }

    private fun moveIfRequired(touchingGround: Boolean, delta: Float) {
        if (movement == Joystick.Movement.None) {
            dx /= 2f
            if (dx < 0.5f) {
                dx = 0f
            }
        }

        var dm = when (movement) {
            Joystick.Movement.Right -> 1f
            Joystick.Movement.Left -> -1f
            else -> 0f
        }

        if (!touchingGround) {
            dm /= 2f
        }

        dx += dm * 64f * delta

        when (movement) {
            Joystick.Movement.Right -> run()
            Joystick.Movement.Left -> run(reverse = true)
            Joystick.Movement.None -> {
                setAction("idle", isBitmapReversed)
                isRunning = false
            }
        }
    }

    private fun run(reverse: Boolean = false) {
        isRunning = true
        setAction("run", reverse)
    }

    private fun jump(predicate: () -> Boolean = { true }) {
        if (predicate()) {
            dy -= 6f
        } else {
            isJumping = false
        }
    }

    private fun applyGravity(isTouchingGround: Boolean, delta: Float) {
        if (!isTouchingGround) {
            dy += (12.2f * delta)
        }
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

    fun center() = Vector2f(rect.centerX, rect.centerY)

}