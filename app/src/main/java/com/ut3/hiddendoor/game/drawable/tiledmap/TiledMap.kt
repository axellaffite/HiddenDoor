package com.ut3.hiddendoor.game.drawable.tiledmap

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.RawRes
import androidx.core.graphics.times
import com.charleskorn.kaml.Yaml
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.utils.Vector2i
import com.ut3.hiddendoor.game.utils.toVector2f
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.lang.Integer.min
import kotlin.math.ceil

class TiledMap(
    private val data: TiledMapData,
    context: Context
) : Drawable() {

    private data class Tile(
        val x: Int,
        val y: Int,
        val tx: Int,
        val ty: Int
    )

    val tileSize = data.tileSize

    private val tileset = Tileset(data.tileset, data.chunkSize, data.tileSize.toInt(), context)

    private val layers = let {
        val res = data.layers.mapValues { (_, values) ->
            values.mapIndexed { i, v ->
                val interpretedValue = v.split(".").map(String::toInt)
                val tx = interpretedValue[0]
                val ty = interpretedValue.getOrElse(1) { if (tx == -1) -1 else 0 }
                Tile(x = i % data.width, y = i / data.width, tx = tx, ty = ty)
            }.groupBy { it.y / data.chunkSize }
                .mapValues { (_, v) -> v.groupBy { it.x / data.chunkSize } }
        }

        res.mapValues { (_, layer) ->
            layer.flatMap { (y, lines) ->
                lines.map { (x, chunk) ->
                    // Chunk bounds in pixels
                    val top = y * data.chunkSize * data.tileSize
                    val left = x * data.chunkSize * data.tileSize
                    val bottom = top + data.chunkSize * data.tileSize
                    val right = left + data.chunkSize * data.tileSize

                    // Maximum x index in the current chunk
                    // (to avoid drawing issues on non-properly divided tilemap)
                    // For example, in a tilemap with a width of 50 and a chunk size of 16,
                    // the last chunk will have a width of 2.
                    val width = min(data.chunkSize, (data.width - (x * data.chunkSize)))

                    Chunk(
                        vertices = chunk.flatMapIndexed { index, _ ->
                            // Current tile bounds in pixels
                            val topTile = top + (index / width * data.tileSize)
                            val leftTile = left + (index % width * data.tileSize)
                            val bottomTile = topTile + data.tileSize
                            val rightTile = leftTile + data.tileSize

                            // Constructed as 2 triangles
                            listOf(
                                // Upper left triangle
                                leftTile, topTile,
                                rightTile, topTile,
                                leftTile, bottomTile,
                                // Bottom right triangle
                                leftTile, bottomTile,
                                rightTile, topTile,
                                rightTile, bottomTile
                            )
                        }.toFloatArray(),
                        textCoordinates = chunk.flatMap { tile ->
                            // Texture coordinates
                            val position = Vector2i(x = tile.tx, y = tile.ty).toVector2f()
                            val leftTCoords = position.x * data.tileSize
                            val topTCoords = position.y * data.tileSize
                            val rightTCoords = leftTCoords + data.tileSize
                            val bottomTCoords = topTCoords + data.tileSize

                            listOf(
                                // Upper left triangle
                                leftTCoords, topTCoords,
                                rightTCoords, topTCoords,
                                leftTCoords, bottomTCoords,
                                // Bottom right triangle
                                leftTCoords, bottomTCoords,
                                rightTCoords, topTCoords,
                                rightTCoords, bottomTCoords
                            )
                        }.toFloatArray(),
                        tileset = tileset,
                        rect = RectF(left, top, right, bottom)
                    )
                }
            }
        }
    }

    private val collisions = data.collisions.chunked(data.width)

    override val rect: RectF = RectF(
        0f,
        0f,
        data.width * data.tileSize,
        data.height * data.tileSize
    )

    fun collisionTilesIntersecting(rect: RectF): List<Int> {
        val coordinates = rect.times(1f / data.tileSize)
        val left = coordinates.left.toInt()
        val right = ceil(coordinates.right).toInt()
        val top = coordinates.top.toInt()
        val bottom = ceil(coordinates.bottom).toInt()

        return (top until bottom).flatMap { y ->
            (left until right).map { x ->
                collisions.getOrNull(y)?.getOrNull(x)
            }
        }.filterNotNull()
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
        runBlocking {
            layers.flatMap { (_, chunks) ->
                chunks.map { chunk ->
                    async {
                        chunk.draw(bounds, surfaceHolder, paint)
                    }
                }
            }.awaitAll()
        }
    }

}

@Serializable
data class TiledMapData(
    val tileset: String,
    val chunkSize: Int = 16,
    val tileSize: Float,
    val width: Int,
    val height: Int,
    val layersOrder: List<String>,
    val layers: Map<String, List<String>>,
    val collisions: List<Int> = emptyList()
)

@Throws(Resources.NotFoundException::class)
fun Context.loadTiledMap(@RawRes res: Int): TiledMap {
    val content = runCatching { resources.openRawResource(res).reader().readText() }
        .getOrElse {
            Log.e("TILED MAP", "Unable to tiled map file, reason: $it")
            throw it
        }

    return TiledMap(
        Yaml.default.decodeFromString(content),
        this
    )
}