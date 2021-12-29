package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day15 {
    private const val iterationsPart1 = 2020
    private const val iterationsPart2 = 30000000

    private fun solve(lines: List<String>, iterations: Int): Int {
        val startingNumbers = lines[0].split(",").map(String::toInt)

        val lastIndexArray = Array(iterations) { -1 }
        var lastNumber = -1
        startingNumbers.forEachIndexed { i, number ->
            if (i > 0) {
                lastIndexArray[lastNumber] = i - 1
            }
            lastNumber = number
        }

        for (i in startingNumbers.size until iterations) {
            var prevIndex = lastIndexArray[lastNumber]
            if (prevIndex == -1) {
                prevIndex = i - 1
            }

            lastIndexArray[lastNumber] = i - 1
            lastNumber = (i - 1) - prevIndex
        }

        return lastNumber
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input15.txt"), Charsets.UTF_8)
        val solution1 = solve(lines, iterationsPart1)
        println(solution1)
        val solution2 = solve(lines, iterationsPart2)
        println(solution2)
    }
}