package com.ut3.hiddendoor.game.drawable.tiledmap

import android.content.Context
import android.graphics.BitmapFactory
import com.ut3.hiddendoor.game.utils.Vector2i

class Tileset(filename: String, private val chunkSize: Int, val tileSize: Int, context: Context) {

    val bitmap = context.resources.let { resources ->
        val id = resources.getIdentifier(filename, "drawable", context.packageName)

        BitmapFactory.decodeResource(resources, id, BitmapFactory.Options().apply { inScaled = false })
            ?: throw IllegalStateException("Unable to load tileset: ${filename}")
    }

    val width = bitmap.width / tileSize
    val height = bitmap.height / tileSize

    /**
     * Computes the position of the given [index] in the current tileset.
     * The position isn't multiplied by the [chunk size][chunkSize].
     *
     * Example:
     * current tileset properties :
     *  - width = 4
     *  - chunkSize = 16
     *
     * index = 50
     *
     * Result -> Vector2i(x=2, y=12)
     *
     *
     * @param index index of the tile
     * @return the x and y position of the tile in the current tileset (not * by [tileSize])
     */
    fun positionGivenIndex(index: Int) = Vector2i(x = index % width, y = index / tileSize)
}