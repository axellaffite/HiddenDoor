package com.ut3.hiddendoor.game.logic

import com.ut3.hiddendoor.game.GameView

abstract class Level(protected val gameView: GameView): Logic {

    private val entities = mutableListOf<Entity>()

    fun clean() {
        onClean()
        entities.clear()
    }

    final override fun handleInput(inputState: InputState) {
        entities.forEach { it.handleInput(inputState) }
        onHandleInput(inputState)
    }

    final override fun update(delta: Float) {
        entities.forEach { it.update(delta) }
        onUpdate(delta)
        postUpdate(delta)
    }

    final override fun postUpdate(delta: Float) {
        entities.forEach { it.postUpdate(delta) }
        onPostUpdate(delta)
    }

    protected abstract fun onHandleInput(inputState: InputState)

    /**
     * Hook that must be overridden to perform update within your level.
     *
     * This is what is commonly used to update your entities or your game logic.
     * Note though that entities' onUpdate are automatically called if you created
     * them with the [createEntity] function.
     *
     * This is guarantied to be called before the [onPostUpdate] hook.
     *
     * @param delta the delta time between now and the last update
     */
    protected abstract fun onUpdate(delta: Float)

    /**
     * Hook that must be overridden to perform operations after the logic has
     * been updated within your application.
     *
     * For example, to center a camera on a drawable after this drawable has been
     * updated, so this ensure the execution order.
     *
     * @param delta the delta time between now and the last update
     */
    protected open fun onPostUpdate(delta: Float) = Unit

    /**
     * Hook that should be overridden to setup everything that is needed for
     * this level to work.
     */
    abstract fun onLoad()

    /**
     * Hook that should be overridden to clean everything that has been used
     * during this level.
     */
    abstract fun onClean()

    /**
     * Hook that will be called during auto-save or when the game is being closed.
     */
    abstract fun onSaveState()

    /**
     * Used to create an Entity, this function also registers the entity
     * to be called with the classic lifecycle hooks that are defined in
     * the [Logic] interface.
     *
     * @param block block that is responsible for the entity creation
     * @return the entity created in the given [block]
     */
    fun <T: Entity> createEntity(block: () -> T): T {
        return block().also { entities.add(it) }
    }

}