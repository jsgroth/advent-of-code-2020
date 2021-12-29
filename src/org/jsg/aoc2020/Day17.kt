package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day17 {
    private fun solvePart1(lines: List<String>): Int {
        val initialState = parseInput(lines)

        val finalState = (1..6).fold(initialState) { prevState, _ -> simulateTurn(prevState) }

        return finalState.size
    }

    private fun solvePart2(lines: List<String>): Int {
        val initialState = parseInput(lines).map { (x, y, z) -> listOf(x, y, z, 1) }.toSet()

        val finalState = (1..6).fold(initialState) { prevState, _ -> simulateTurnPart2(prevState) }

        return finalState.size
    }

    private fun simulateTurn(active: Set<Triple<Int, Int, Int>>): Set<Triple<Int, Int, Int>> {
        val newActive = mutableSetOf<Triple<Int, Int, Int>>()

        val minX = active.minOf { it.first }
        val maxX = active.maxOf { it.first }
        val minY = active.minOf { it.second }
        val maxY = active.maxOf { it.second }
        val minZ = active.minOf { it.third }
        val maxZ = active.maxOf { it.third }

        ((minX - 1)..(maxX + 1)).forEach { x ->
            ((minY - 1)..(maxY + 1)).forEach { y ->
                ((minZ - 1)..(maxZ + 1)).forEach { z ->
                    val litNeighbors = (-1..1).sumOf { dx ->
                        (-1..1).sumOf { dy ->
                            (-1..1).count { dz ->
                                !(dx == 0 && dy == 0 && dz == 0) &&
                                        active.contains(Triple(x + dx, y + dy, z + dz))
                            }
                        }
                    }
                    if (litNeighbors == 3 || (active.contains(Triple(x, y, z)) && litNeighbors == 2)) {
                        newActive.add(Triple(x, y, z))
                    }
                }
            }
        }

        return newActive.toSet()
    }

    private fun simulateTurnPart2(active: Set<List<Int>>): Set<List<Int>> {
        val newActive = mutableSetOf<List<Int>>()

        val minX = active.minOf { it[0] }
        val maxX = active.maxOf { it[0] }
        val minY = active.minOf { it[1] }
        val maxY = active.maxOf { it[1] }
        val minZ = active.minOf { it[2] }
        val maxZ = active.maxOf { it[2] }
        val minW = active.minOf { it[3] }
        val maxW = active.maxOf { it[3] }

        ((minX - 1)..(maxX + 1)).forEach { x ->
            ((minY - 1)..(maxY + 1)).forEach { y ->
                ((minZ - 1)..(maxZ + 1)).forEach { z ->
                    ((minW - 1)..(maxW + 1)).forEach { w ->
                        val litNeighbors = (-1..1).sumOf { dx ->
                            (-1..1).sumOf { dy ->
                                (-1..1).sumOf { dz ->
                                    (-1..1).count { dw ->
                                        !(dx == 0 && dy == 0 && dz == 0 && dw == 0) &&
                                                active.contains(listOf(x + dx, y + dy, z + dz, w + dw))
                                    }
                                }
                            }
                        }
                        if (litNeighbors == 3 || (active.contains(listOf(x, y, z, w)) && litNeighbors == 2)) {
                            newActive.add(listOf(x, y, z, w))
                        }
                    }
                }
            }
        }

        return newActive.toSet()
    }

    private fun parseInput(lines: List<String>): Set<Triple<Int, Int, Int>> {
        return lines.flatMapIndexed { i, line ->
            line.mapIndexed { j, c ->
                if (c == '#') Triple(j, i, 1) else null
            }.filterNotNull()
        }.toSet()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input17.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}