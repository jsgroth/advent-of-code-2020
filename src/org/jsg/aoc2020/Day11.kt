package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day11 {
    enum class SeatStatus {
        UNOCCUPIED,
        OCCUPIED,
        FLOOR,
    }

    private fun solvePart1(lines: List<String>): Int {
        val seatGrid = parseSeatGrid(lines)

        var currentGrid = seatGrid
        while (true) {
            val prevGrid = currentGrid
            currentGrid = simulateTurn(currentGrid)
            if (prevGrid == currentGrid) {
                return currentGrid.sumOf { row -> row.count { it == SeatStatus.OCCUPIED } }
            }
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        val seatGrid = parseSeatGrid(lines)

        var currentGrid = seatGrid
        while (true) {
            val prevGrid = currentGrid
            currentGrid = simulateTurn(
                currentGrid,
                countOccupiedFunc = ::countNearbyOccupiedPart2,
                occupiedThreshold = 5,
            )
            if (prevGrid == currentGrid) {
                return currentGrid.sumOf { row -> row.count { it == SeatStatus.OCCUPIED } }
            }
        }
    }

    private fun parseSeatGrid(lines: List<String>): List<List<SeatStatus>> {
        return lines.map { line ->
            line.map { c ->
                when (c) {
                    '.' -> SeatStatus.FLOOR
                    'L' -> SeatStatus.UNOCCUPIED
                    else -> throw IllegalArgumentException("$c")
                }
            }
        }
    }

    private fun simulateTurn(
        grid: List<List<SeatStatus>>,
        countOccupiedFunc: (List<List<SeatStatus>>, Int, Int) -> Int = ::countNearbyOccupied,
        occupiedThreshold: Int = 4,
    ): List<List<SeatStatus>> {
        return grid.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, seatStatus ->
                when (seatStatus) {
                    SeatStatus.FLOOR -> SeatStatus.FLOOR
                    SeatStatus.UNOCCUPIED -> if (countOccupiedFunc(grid, rowIndex, colIndex) == 0) {
                        SeatStatus.OCCUPIED
                    } else {
                        SeatStatus.UNOCCUPIED
                    }
                    SeatStatus.OCCUPIED -> if (countOccupiedFunc(grid, rowIndex, colIndex) >= occupiedThreshold) {
                        SeatStatus.UNOCCUPIED
                    } else {
                        SeatStatus.OCCUPIED
                    }
                }
            }
        }
    }


    private fun countNearbyOccupied(grid: List<List<SeatStatus>>, row: Int, col: Int): Int {
        return (-1..1).sumOf { drow ->
            (-1..1).sumOf { dcol ->
                val result = if (
                    !(drow == 0 && dcol == 0) &&
                    grid.indices.contains(row + drow) &&
                    grid[0].indices.contains(col + dcol) &&
                    grid[row + drow][col + dcol] == SeatStatus.OCCUPIED
                ) {
                    1
                } else {
                    0
                }
                result
            }
        }
    }

    private fun countNearbyOccupiedPart2(grid: List<List<SeatStatus>>, row: Int, col: Int): Int {
        return listOf(
            -1 to -1,
            -1 to 0,
            -1 to 1,
            0 to 1,
            1 to 1,
            1 to 0,
            1 to -1,
            0 to -1,
        ).count { (drow, dcol) -> isOccupiedInDirection(grid, row, col, drow, dcol) }
    }

    private fun isOccupiedInDirection(grid: List<List<SeatStatus>>, row: Int, col: Int, drow: Int, dcol: Int): Boolean {
        var i = row + drow
        var j = col + dcol
        while (grid.indices.contains(i) && grid[0].indices.contains(j)) {
            if (grid[i][j] == SeatStatus.OCCUPIED) {
                return true
            }
            if (grid[i][j] == SeatStatus.UNOCCUPIED) {
                return false
            }

            i += drow
            j += dcol
        }
        return false
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input11.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}