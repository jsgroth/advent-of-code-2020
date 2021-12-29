package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day10 {
    private fun solvePart1(lines: List<String>): Int {
        val joltageRatings = lines.map(String::toInt).sorted()

        var oneJoltDiffs = 0
        var threeJoltDiffs = 1
        for (i in joltageRatings.indices) {
            val lastValue = if (i > 0) joltageRatings[i - 1] else 0
            if (joltageRatings[i] - lastValue == 1) {
                oneJoltDiffs++
            } else if (joltageRatings[i] - lastValue == 3) {
                threeJoltDiffs++
            }
        }

        return oneJoltDiffs * threeJoltDiffs
    }

    private fun solvePart2(lines: List<String>): Long {
        val numbers = lines.map(String::toInt).toSet()
        val target = numbers.maxOrNull()!! + 3

        return countCombinations(numbers, target)
    }

    private fun countCombinations(numbers: Set<Int>, target: Int): Long {
        val counts = mutableMapOf<Int, Long>(
            0 to 1,
            -1 to 0,
            -2 to 0,
        )

        for (i in 1..target-3) {
            if (numbers.contains(i)) {
                counts[i] = (1..3).sumOf { j -> counts[i - j]!! }
            } else {
                counts[i] = 0
            }
        }

        return counts[target - 3]!!
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input10.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}