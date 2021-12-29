package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day9 {
    private const val windowSize = 25

    private fun solvePart1(lines: List<String>): Long {
        return lines.map(String::toLong).windowed(windowSize + 1).first { window ->
            !checkWindow(window.dropLast(1), window.last())
        }.last()
    }

    private fun solvePart2(lines: List<String>): Long {
        val firstInvalidNumber = solvePart1(lines)

        val numbers = lines.map(String::toLong)

        val range = findContiguousRange(numbers, firstInvalidNumber)!!
        val rangeNums = numbers.subList(range.first, range.last + 1)

        return rangeNums.minOrNull()!! + rangeNums.maxOrNull()!!
    }

    private fun findContiguousRange(
        numbers: List<Long>,
        target: Long,
        start: Int = 0,
        inRange: Boolean = false,
    ): IntRange? {
        if (start == numbers.size) {
            return null
        }
        val currentNum = numbers[start]
        if (currentNum == target) {
            return if (inRange) {
                start..start
            } else {
                null
            }
        }
        if (currentNum > target) {
            return null
        }

        findContiguousRange(numbers, target - currentNum, start + 1, inRange = true)?.let {
            return start..it.last
        }
        return if (!inRange) {
            findContiguousRange(numbers, target, start + 1)
        } else {
            null
        }
    }

    private fun checkWindow(window: List<Long>, target: Long): Boolean {
        return window.any { n ->
            window.minus(n).contains(target - n)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input9.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}