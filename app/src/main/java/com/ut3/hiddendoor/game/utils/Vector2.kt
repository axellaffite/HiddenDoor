package com.ut3.hiddendoor.game.utils

interface Vector2<T: Number> {
    val x: T
    val y: T
}

data class Vector2f(
    override val x: Float,
    override val y: Float
): Vector2<Float>

data class Vector2i(
    override val x: Int,
    override val y: Int
): Vector2<Int>

fun Vector2i.toVector2f() = Vector2f(x = x.toFloat(), y = y.toFloat())
