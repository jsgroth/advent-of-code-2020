package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.abs

object Day12 {
    data class Instruction(val direction: Char, val value: Int)

    private fun solvePart1(lines: List<String>): Int {
        val instructions = parseInstructions(lines)

        val (x, y) = simulateInstructions(instructions)
        return abs(x) + abs(y)
    }

    private fun solvePart2(lines: List<String>): Int {
        val instructions = parseInstructions(lines)

        val (x, y) = simulateInstructionsPart2(instructions)
        return abs(x) + abs(y)
    }

    private fun parseInstructions(lines: List<String>): List<Instruction> {
        return lines.map { line ->
            Instruction(line[0], line.substring(1).toInt())
        }
    }

    private fun simulateInstructions(instructions: List<Instruction>): Pair<Int, Int> {
        var x = 0
        var y = 0
        var facing = 0
        instructions.forEach { (direction, value) ->
            when (direction) {
                'N' -> y += value
                'S' -> y -= value
                'W' -> x -= value
                'E' -> y -= value
                'L' -> facing = (facing - value + 360) % 360
                'R' -> facing = (facing + value) % 360
                'F' -> moveForward(value, x, y, facing).let { (newX, newY) ->
                    x = newX
                    y = newY
                }
            }
        }
        return x to y
    }

    private fun simulateInstructionsPart2(instructions: List<Instruction>): Pair<Int, Int> {
        var x = 0
        var y = 0
        var waypointX = 10
        var waypointY = 1
        instructions.forEach { (direction, value) ->
            when (direction) {
                'N' -> waypointY += value
                'S' -> waypointY -= value
                'W' -> waypointX -= value
                'E' -> waypointX += value
                'L', 'R' -> {
                    val leftRotation = if (direction == 'R') 360 - value else value
                    val (newX, newY) = rotateLeft(x, y, waypointX, waypointY, leftRotation)
                    waypointX = newX
                    waypointY = newY
                }
                'F' -> {
                    val xDiff = waypointX - x
                    val yDiff = waypointY - y
                    x += value * xDiff
                    y += value * yDiff
                    waypointX = x + xDiff
                    waypointY = y + yDiff
                }
            }
        }
        return x to y
    }

    private fun rotateLeft(x: Int, y: Int, waypointX: Int, waypointY: Int, rotation: Int): Pair<Int, Int> {
        val xDiff = waypointX - x
        val yDiff = waypointY - y
        return when (rotation) {
            90 -> (x - yDiff) to (y + xDiff)
            180 -> (x - xDiff) to (y - yDiff)
            270 -> (x + yDiff) to (y - xDiff)
            else -> throw IllegalArgumentException("$rotation")
        }
    }

    private fun moveForward(value: Int, x: Int, y: Int, facing: Int): Pair<Int, Int> {
        val dx = if (facing == 0) 1 else if (facing == 180) -1 else 0
        val dy = if (facing == 270) 1 else if (facing == 90) -1 else 0

        return (x + value * dx) to (y + value * dy)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input12.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}