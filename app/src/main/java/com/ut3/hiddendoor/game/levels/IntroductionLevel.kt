package com.ut3.hiddendoor.game.levels

import android.graphics.*
import androidx.core.graphics.withSave
import com.ut3.hiddendoor.R
import com.ut3.hiddendoor.game.GameView
import com.ut3.hiddendoor.game.drawable.sprites.AnimatedSprite
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.drawable.ImmutableRect
import com.ut3.hiddendoor.game.drawable.cameras.createTrackingCamera
import com.ut3.hiddendoor.game.drawable.drawRect
import com.ut3.hiddendoor.game.drawable.hud.Joystick
import com.ut3.hiddendoor.game.drawable.hud.createHud
import com.ut3.hiddendoor.game.drawable.tiledmap.loadTiledMap
import com.ut3.hiddendoor.game.logic.Entity
import com.ut3.hiddendoor.game.logic.InputState
import com.ut3.hiddendoor.game.logic.Level
import com.ut3.hiddendoor.game.utils.Vector2f


class IntroductionLevel(gameView: GameView) : Level(gameView) {

    companion object {
        const val TILE_MAP_RESOURCE = R.raw.testmap
    }

    private val player = createEntity {
        object : Entity, Drawable, AnimatedSprite(gameView.context, R.raw.character, "idle") {
            private var movement = Joystick.Movement.None
            private var isJumping = false
            val speed = 16f
            var dx = 0f
            var dy = 0f
            private var isDead = false
            private var reactToEnvironment = true

            init {
                reset()
            }

            fun reset() {
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
                }
            }

            private fun isTouchingGround(): Boolean {
                return tilemap.collisionTilesIntersecting(
                    RectF(rect.left, rect.bottom, rect.right, rect.bottom + 1f)
                ).any { tileValue -> tileValue == 1 }
            }

            private fun shouldBeDead(): Boolean {
                return isDead || tilemap.collisionTilesIntersecting(rect.copyOfUnderlyingRect).any { it == 0 }
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

                when(movement) {
                    Joystick.Movement.Right -> setAction("run")
                    Joystick.Movement.Left -> {
                        setAction("run", reverse = true)
                    }
                    Joystick.Movement.None -> setAction("idle", isBitmapReversed)
                }
            }

            private fun die() {
                if (!isDead) {
                    reactToEnvironment = false
                    isDead = true
                    setAction("hit", isBitmapReversed)
                }
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
                    val tmp = rect.copyOfUnderlyingRect.apply { offset(0f, dy * delta * speed) }
                    if (tilemap.collisionTilesIntersecting(tmp).any { it == 1 }) {
                        dy = 0f
                    } else {
                        rect = ImmutableRect(tmp)
                    }
                }

                val tmp = rect.copyOfUnderlyingRect.apply { offset(dx * delta * speed, 0f) }
                if (!tilemap.collisionTilesIntersecting(tmp).any { it == 1 }) {
                    rect = ImmutableRect(tmp)
                }
            }

            fun center() = Vector2f(rect.centerX, rect.centerY)

//            override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
//                surfaceHolder.drawRect(rect, paint)
//            }

        }
    }

    private val sprite = createEntity {
        object: AnimatedSprite(gameView.context, R.raw.lever, "off") {
            init {
                move(200f, 448f)
            }
        }
    }

    private var fps = 0
    private val tilemap = gameView.context.loadTiledMap(TILE_MAP_RESOURCE)
    private val camera = createTrackingCamera(
        screenPosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        gamePosition = RectF(0f, 0f, gameView.width.toFloat(), gameView.height.toFloat()),
        track = player::center
    )

    private val hud = createHud(gameView) {
        controlButtons.isBVisible = false
    }


    override fun onLoad() {
    }

    override fun onClean() {
    }

    override fun onSaveState() {
        TODO("Not yet implemented")
    }

    override fun onHandleInput(inputState: InputState) = Unit

    override fun onUpdate(delta: Float) {
        fps = (1f / delta).toInt()
    }

    override fun render() {
        gameView.draw { canvas, paint ->
            canvas.withSave {
                val scaleFactor = ((gameView.width / tilemap.tileSize) / 18f)
                canvas.scale(scaleFactor, scaleFactor, gameView.width / 2f, gameView.height / 2f)
                canvas.drawColor(Color.BLUE)

                withCamera(camera) { canvas, paint ->
                    canvas.draw(tilemap, paint)

                    paint.color = Color.RED
                    canvas.draw(player, paint)
                    canvas.draw(sprite, paint)
                }
            }


            hud.draw(gameView.rect, canvas, paint)
        }
    }

}