package com.ut3.hiddendoor.game.utils

interface Vector3<T: Number> {
    val x: T
    val y: T
    val z: T
}

data class Vector3f(
    override val x: Float,
    override val y: Float,
    override val z: Float
): Vector3<Float>

data class Vector3i(
    override val x: Int,
    override val y: Int,
    override val z: Int
): Vector3<Int>

fun Vector3i.toVector3f() = Vector3f(x = x.toFloat(), y = y.toFloat(),z = z.toFloat())
operator fun Vector3f.times(amount: Float) = Vector3f(x * amount, y * amount, z * amount)

operator fun Vector3i.times(amount: Int) = Vector3i(x * amount, y * amount, z * amount)
operator fun Vector3i.times(amount: Float) = Vector3f(x * amount, y * amount, z * amount)