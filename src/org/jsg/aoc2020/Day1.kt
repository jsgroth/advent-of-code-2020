package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day1 {
    private fun solvePart1(lines: List<String>): Int {
        val numbers = lines.map(String::toInt)
        return numbers.first { numbers.contains(2020 - it) }.let { it * (2020 - it) }
    }

    private fun solvePart2(lines: List<String>): Int {
        val numbers = lines.map(String::toInt)
        for (n in numbers) {
            numbers.firstOrNull { m ->
                m != n && (2020 - n - m) != n && numbers.contains(2020 - n - m)
            }?.let {
                return n * it * (2020 - n - it)
            }
        }
        throw IllegalArgumentException("no solution")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input1.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}