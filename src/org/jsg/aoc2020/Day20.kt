package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day20 {
    data class Tile(val id: Int, val points: List<List<Boolean>>) {
        fun getTopEdge(): List<Int> {
            val edge = points[0].indices.filter { i -> points[0][i] }
            return canonicalizeEdge(edge, points[0].size)
        }

        fun getLeftEdge(): List<Int> {
            val edge = points.indices.filter { i -> points[i][0] }
            return canonicalizeEdge(edge, points.size)
        }

        fun getRightEdge(): List<Int> {
            val edge = points.indices.filter { i -> points[i].last() }
            return canonicalizeEdge(edge, points.size)
        }

        fun getBottomEdge(): List<Int> {
            val edge = points[0].indices.filter { i -> points.last()[i] }
            return canonicalizeEdge(edge, points[0].size)
        }
    }

    private val seaDragonPattern = """
                  # 
#    ##    ##    ###
 #  #  #  #  #  #   
    """.trimIndent()

    private fun solvePart1(lines: List<String>): Long {
        val tiles = parseTiles(lines)

        val edges = tiles.associate { tile -> tile.id to generateEdges(tile) }

        val edgeGroups = generateEdgeGroups(edges)

        val corners = tiles.filter { tile ->
            val tileEdges = edges[tile.id]!!
            tileEdges.count { edgeGroups[it]!!.size == 1 } == 2
        }

        return corners.fold(1L) { p, corner -> p * corner.id }
    }

    private fun solvePart2(lines: List<String>): Int {
        val tiles = parseTiles(lines)

        val edges = tiles.associate { tile -> tile.id to generateEdges(tile) }

        val edgeGroups = generateEdgeGroups(edges)

        val assembledTiles = assembleTiles(tiles, edges, edgeGroups)
        val combinedTile = combineTiles(assembledTiles)

        val seaDragon = seaDragonPattern.split("\n").map { line ->
            line.map { it == '#' }
        }

        val seaDragonCount = countSeaDragons(combinedTile, seaDragon)

        return combinedTile.points.sumOf { row ->  row.count { it } } -
                seaDragonCount * seaDragon.sumOf { row -> row.count { it } }
    }

    private fun generateEdges(tile: Tile): List<List<Int>> {
        return listOf(tile.getLeftEdge(), tile.getTopEdge(), tile.getRightEdge(), tile.getBottomEdge())
    }

    private fun canonicalizeEdge(edge: List<Int>, size: Int): List<Int> {
        val reversed = edge.map { size - 1 - it }.reversed()
        val (sum1, sum2) = (0 until size).fold(0L to 0L) { (sum1, sum2), i ->
            (10 * sum1 + if (edge.contains(i)) 1 else 0) to (10 * sum2 + if (reversed.contains(i)) 1 else 0)
        }
        return if (sum1 <= sum2) edge else reversed
    }

    private fun generateEdgeGroups(edges: Map<Int, List<List<Int>>>): Map<List<Int>, List<Int>> {
        val edgeGroups = mutableMapOf<List<Int>, MutableList<Int>>()
        edges.entries.forEach { (tileId, edgeList) ->
            edgeList.forEach { edge ->
                if (!edgeGroups.containsKey(edge)) {
                    edgeGroups[edge] = mutableListOf()
                }
                edgeGroups[edge]!!.add(tileId)
            }
        }
        return edgeGroups
    }

    private fun assembleTiles(
        tiles: List<Tile>,
        edges: Map<Int, List<List<Int>>>,
        edgeGroups: Map<List<Int>, List<Int>>,
    ): List<List<Tile>> {
        val tileMap = tiles.associateBy(Tile::id)

        val result = mutableListOf(mutableListOf<Tile>())

        val corners = edges.filter { (_, tileEdges) ->
            tileEdges.count { edgeGroups[it]!!.size == 1 } == 2
        }

        val topLeft = tileMap[corners.keys.first()]!!

        result[0].add(fitTile(topLeft, result, edgeGroups)!!)

        val placedTileIds = mutableSetOf(topLeft.id)

        while (result.sumOf(List<Tile>::size) < tiles.size) {
            tiles.filter { tile -> !placedTileIds.contains(tile.id) }.forEach { tile ->
                fitTile(tile, result, edgeGroups)?.let { fittedTile ->
                    placedTileIds.add(fittedTile.id)

                    result.last().add(fittedTile)
                    if (edgeGroups[fittedTile.getRightEdge()]!!.size == 1) {
                        result.add(mutableListOf())
                    }
                }
            }
        }

        result.removeLast()

        return result
    }

    private fun fitTile(
        tile: Tile,
        result: List<List<Tile>>,
        edgeGroups: Map<List<Int>, List<Int>>
    ): Tile? {
        var adjustedTile = tile
        (1..4).forEach { _ ->
            if (isTileFitted(adjustedTile, result, edgeGroups)) {
                return adjustedTile
            }
            adjustedTile = rotateTile(adjustedTile)
        }

        adjustedTile = flipTile(adjustedTile)

        (1..4).forEach { _ ->
            if (isTileFitted(adjustedTile, result, edgeGroups)) {
                return adjustedTile
            }
            adjustedTile = rotateTile(adjustedTile)
        }

        return null
    }

    private fun isTileFitted(
        tile: Tile,
        result: List<List<Tile>>,
        edgeGroups: Map<List<Int>, List<Int>>
    ): Boolean {
        val topEdge = tile.getTopEdge()
        if (result.size == 1) {
            if (edgeGroups[topEdge]!!.size > 1) {
                return false
            }
        } else {
            val aboveTile = result[result.size - 2][result.last().size]
            if (aboveTile.getBottomEdge() != topEdge) {
                return false
            }
        }

        val leftEdge = tile.getLeftEdge()
        if (result.last().isEmpty()) {
            if (edgeGroups[leftEdge]!!.size > 1) {
                return false
            }
        } else {
            val leftTile = result.last().last()
            if (leftTile.getRightEdge() != leftEdge) {
                return false
            }
        }

        return true
    }

    private fun rotateTile(tile: Tile): Tile {
        return adjustTile(tile) { (i, j, length) -> (length - 1 - j) to i }
    }

    private fun flipTile(tile: Tile): Tile {
        return adjustTile(tile) { (i, j, length) -> (length - 1 - i) to j }
    }

    private fun adjustTile(tile: Tile, adjustmentFunc: (Triple<Int, Int, Int>) -> Pair<Int, Int>): Tile {
        val length = tile.points.size

        val newPoints = Array(length) { Array(length) { false } }
        tile.points.indices.forEach { i ->
            tile.points[0].indices.forEach { j ->
                val (ni, nj) = adjustmentFunc(Triple(i, j, length))
                newPoints[ni][nj] = tile.points[i][j]
            }
        }

        return Tile(tile.id, newPoints.map(Array<Boolean>::toList))
    }

    private fun combineTiles(tiles: List<List<Tile>>): Tile {
        val tileLength = tiles[0][0].points.size - 2
        val length = tiles.size * tileLength
        val newPoints = Array(length) { Array(length) { false } }

        tiles.forEachIndexed { i, row ->
            row.forEachIndexed { j, tile ->
                for (tileI in 1..tileLength) {
                    for (tileJ in 1..tileLength) {
                        val value = tile.points[tileI][tileJ]
                        newPoints[i * tileLength + tileI - 1][j * tileLength + tileJ - 1] = value
                    }
                }
            }
        }

        return Tile(0, newPoints.map(Array<Boolean>::toList))
    }

    private fun countSeaDragons(tile: Tile, seaDragon: List<List<Boolean>>): Int {
        var adjustedTile = tile
        (1..4).forEach { _ ->
            val seaDragonCount = countSeaDragonsAtOrientation(adjustedTile, seaDragon)
            if (seaDragonCount > 0) {
                return seaDragonCount
            }
            adjustedTile = rotateTile(adjustedTile)
        }

        adjustedTile = flipTile(adjustedTile)

        (1..4).forEach { _ ->
            val seaDragonCount = countSeaDragonsAtOrientation(adjustedTile, seaDragon)
            if (seaDragonCount > 0) {
                return seaDragonCount
            }
            adjustedTile = rotateTile(adjustedTile)
        }

        throw IllegalArgumentException("no sea dragons found")
    }

    private fun countSeaDragonsAtOrientation(tile: Tile, seaDragon: List<List<Boolean>>): Int {
        var count = 0
        for (i in 0..(tile.points.size - seaDragon.size)) {
            for (j in 0.. (tile.points[0].size - seaDragon[1].size)) {
                if (isSeaDragon(tile, seaDragon, i, j)) {
                    count++
                }
            }
        }
        return count
    }

    private fun isSeaDragon(tile: Tile, seaDragon: List<List<Boolean>>, i: Int, j: Int): Boolean {
        for (di in seaDragon.indices) {
            for (dj in seaDragon[di].indices) {
                if (seaDragon[di][dj] && !tile.points[i + di][j + dj]) {
                    return false
                }
            }
        }
        return true
    }

    private fun parseTiles(lines: List<String>): List<Tile> {
        if (lines.isEmpty()) {
            return listOf()
        }

        val id = lines[0].split(" ")[1].dropLast(1).toInt()

        val points = lines.drop(1).takeWhile(String::isNotBlank).map { line ->
            line.map { it == '#' }
        }

        return listOf(Tile(id, points)).plus(parseTiles(lines.dropWhile(String::isNotBlank).drop(1)))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input20.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}