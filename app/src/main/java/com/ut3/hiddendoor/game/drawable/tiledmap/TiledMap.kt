package com.ut3.hiddendoor.game.drawable.tiledmap

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.RawRes
import com.charleskorn.kaml.Yaml
import com.ut3.hiddendoor.game.drawable.Drawable
import com.ut3.hiddendoor.game.utils.Vector2i
import com.ut3.hiddendoor.game.utils.toVector2f
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

class TiledMap(private val data: TiledMapData, context: Context) : Drawable() {

    private val layersOrder: Map<String, Int> =
        data.layersOrder.mapIndexed { i, v -> v to i }.toMap()

    private data class Tile(
        val x: Int,
        val y: Int,
        val tx: Int,
        val ty: Int
    )

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
                    val top = y * data.chunkSize * data.tileSize
                    val left = x * data.chunkSize * data.tileSize
                    val bottom = top + data.chunkSize * data.tileSize
                    val right = left + data.chunkSize * data.tileSize

                    Chunk(
                        vertices = chunk.flatMapIndexed { index, _ ->
                            val topTile = top + (index / data.chunkSize * data.tileSize)
                            val leftTile = left + (index % data.chunkSize * data.tileSize)
                            val bottomTile = topTile + data.tileSize
                            val rightTile = leftTile + data.tileSize

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

    override val rect: RectF = RectF(0f, 0f, data.width * data.tileSize, data.height * data.tileSize)

    private fun drawBounds(bounds: RectF): Rect {
        return Rect(
            (bounds.left / data.chunkSize / data.tileSize).toInt(),
            (bounds.top / data.chunkSize / data.tileSize).toInt(),
            (bounds.right / data.chunkSize / data.tileSize).toInt(),
            (bounds.bottom / data.chunkSize / data.tileSize).toInt()
        )
    }

    override fun drawOnCanvas(bounds: RectF, surfaceHolder: Canvas, paint: Paint) {
//        val drawBounds = drawBounds(bounds)
//        for (y in drawBounds.top .. drawBounds.bottom) {
//            for (x in drawBounds.left .. drawBounds.right) {
//                for ((_, chunks) in layers) {
//                    chunk
//                }
//            }
//        }

        var count = 0
        var total = 0
        for ((_, chunks) in layers) {
            for (chunk in chunks) {
                if (chunk.draw(bounds, surfaceHolder, paint)) {
                    count ++
                }
                total ++
            }
        }

        println("Drawn: $count / $total")
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

    return TiledMap(Yaml.default.decodeFromString(content), this)
}