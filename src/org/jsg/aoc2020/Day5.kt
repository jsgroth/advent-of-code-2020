package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day5 {
    private fun solvePart1(lines: List<String>): Int {
        val seats = lines.map(::findSeat)

        val seatIds = seats.map { (row, col) -> 8 * row + col }

        return seatIds.maxOrNull()!!
    }

    private fun solvePart2(lines: List<String>): Int {
        val seats = lines.map(::findSeat)

        val seatIds = seats.map { (row, col) -> 8 * row + col }.sorted()

        for (i in 0 until seatIds.size - 1) {
            if (seatIds[i + 1] == seatIds[i] + 2) {
                return seatIds[i] + 1
            }
        }

        throw IllegalArgumentException("no solution found")
    }

    private fun findSeat(line: String): Pair<Int, Int> {
        var bRow = 0
        var eRow = 128
        for (i in 0 until 7) {
            if (line[i] == 'F') {
                eRow -= (eRow - bRow) / 2
            } else {
                bRow += (eRow - bRow) / 2
            }
        }

        var bCol = 0
        var eCol = 8
        for (i in 7 until 10) {
            if (line[i] == 'R') {
                bCol += (eCol - bCol) / 2
            } else {
                eCol -= (eCol - bCol) / 2
            }
        }

        return bRow to bCol
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input5.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}