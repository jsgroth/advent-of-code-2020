package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day6 {
    private fun solvePart1(lines: List<String>): Int {
        val groups = parseLines(lines)

        return groups.sumOf { group ->
            group.flatMap(String::asIterable).toSet().size
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        val groups = parseLines(lines)

        return groups.sumOf { group ->
            group.flatMap(String::asIterable)
                .groupBy { it }
                .mapValues { (_, values) -> values.size }
                .filterValues { it == group.size }
                .size
        }
    }

    private fun parseLines(lines: List<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        var i = 0
        while (i < lines.size) {
            val end = lines.subList(i, lines.size).indexOf("").let { if (it < 0) lines.size else it + i }
            result.add(lines.subList(i, end))
            i = end + 1
        }
        return result.toList()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input6.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}