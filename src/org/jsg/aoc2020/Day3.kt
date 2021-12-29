package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day3 {
    private fun solvePart1(lines: List<String>): Int {
        val geography = parseLines(lines)

        return geography.indices.count { i ->
            geography[i][(3 * i) % geography[0].size]
        }
    }

    private fun solvePart2(lines: List<String>): Int{
        val geography = parseLines(lines)

        val toCheck = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)

        return toCheck.map { (dx, dy) ->
            var i = 0
            var j = 0
            var trees = 0
            while (i < geography.size) {
                if (geography[i][j % geography[0].size]) {
                    trees++
                }
                i += dy
                j += dx
            }
            trees
        }.fold(1) { p, n -> p * n }
    }

    private fun parseLines(lines: List<String>): List<List<Boolean>> {
        return lines.map { line ->
            line.map { it == '#' }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input3.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}