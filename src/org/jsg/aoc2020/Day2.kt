package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day2 {
    private fun solvePart1(lines: List<String>): Int {
        return lines.count { line ->
            val (rangeString, c, password) = line.split(Regex(":? "))
            val range = rangeString.split("-").let{ it[0].toInt()..it[1].toInt() }
            range.contains(password.count { it == c[0] })
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        return lines.count { line ->
            val (rangeString, c, password) = line.split(Regex(":? "))
            val (a, b) = rangeString.split("-").map(String::toInt)
            (password[a-1] == c[0] || password[b-1] == c[0]) && password[a-1] != password[b-1]
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input2.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}