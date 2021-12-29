package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day8 {
    private fun solvePart1(lines: List<String>): Int {
        val instructions = parseInstructions(lines)

        val executed = Array(instructions.size) { false }

        var i = 0
        var acc = 0
        while (true) {
            if (executed[i]) {
                return acc
            }

            executed[i] = true

            val (op, value) = instructions[i]
            when (op) {
                "nop" -> {}
                "acc" -> acc += value
                "jmp" -> i += value - 1
            }
            i++
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        val instructions = parseInstructions(lines)

        return willTerminate(instructions).second
    }

    private fun willTerminate(
        instructions: List<Pair<String, Int>>,
        i: Int = 0,
        acc: Int = 0,
        swapped: Boolean = false,
        executed: Set<Int> = setOf(),
    ): Pair<Boolean, Int> {
        var index = i
        var accumulator = acc
        val newExecuted = executed.toMutableSet()
        while (index < instructions.size) {
            if (newExecuted.contains(index)) {
                return false to 0
            }

            val (op, value) = instructions[index]
            newExecuted.add(index)
            if (op == "acc") {
                accumulator += value
            } else if (op == "nop") {
                if (!swapped) {
                    val (willSwapTerminate, swapAcc) = willTerminate(
                        instructions,
                        index + value,
                        accumulator,
                        true,
                        newExecuted.toSet(),
                    )
                    if (willSwapTerminate) {
                        return true to swapAcc
                    }
                }
            } else {
                if (!swapped) {
                    val (willSwapTerminate, swapAcc) = willTerminate(
                        instructions,
                        index + 1,
                        accumulator,
                        true, newExecuted.toSet(),
                    )
                    if (willSwapTerminate) {
                        return true to swapAcc
                    }
                }

                index += value - 1
            }

            index++
        }

        return true to accumulator
    }

    private fun parseInstructions(lines: List<String>): List<Pair<String, Int>> {
        return lines.map { line ->
            val (op, valueString) = line.split(" ")
            val value = if (valueString[0] == '+') {
                valueString.substring(1).toInt()
            } else {
                -valueString.substring(1).toInt()
            }
            op to value
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input8.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}