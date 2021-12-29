package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day18 {
    private fun solvePart1(lines: List<String>): Long {
        val tokenizedExpressions = lines.map(Day18::parseExpression)
        val expressionResults = tokenizedExpressions.map(Day18::evaluateExpression)
        return expressionResults.sum()
    }

    private fun solvePart2(lines: List<String>): Long {
        val tokenizedExpressions = lines.map(Day18::parseExpression)
        val expressionResults = tokenizedExpressions.map { evaluateExpression(it, true) }
        return expressionResults.sum()
    }

    private fun evaluateExpression(tokens: List<String>, part2Precedence: Boolean = false): Long {
        val operands = mutableListOf(ArrayDeque<Long>())
        val operators = mutableListOf(ArrayDeque<String>())

        for (token in tokens) {
            when (token) {
                "+", "*" -> operators.last().add(token)
                "(" -> {
                    operands.add(ArrayDeque())
                    operators.add(ArrayDeque())
                }
                ")" -> {
                    val result = evaluate(operands.removeLast(), operators.removeLast(), part2Precedence)
                    operands.last().add(result)
                }
                else -> {
                    operands.last().add(token.toLong())
                }
            }
        }

        return evaluate(operands[0], operators[0], part2Precedence)
    }

    private fun evaluate(operands: ArrayDeque<Long>, operators: ArrayDeque<String>, part2Precedence: Boolean): Long {
        if (part2Precedence) {
            return evaluatePart2(operands, operators)
        }

        while (operands.size > 1) {
            val a = operands.removeFirst()
            val b = operands.removeFirst()
            when (operators.removeFirst()) {
                "+" -> operands.addFirst(a + b)
                "*" -> operands.addFirst(a * b)
            }
        }
        return operands[0]
    }

    private fun evaluatePart2(operands: ArrayDeque<Long>, operators: ArrayDeque<String>): Long {
        while (operands.size > 1) {
            val firstPlus = operators.indexOf("+")
            if (firstPlus >= 0) {
                val a = operands.removeAt(firstPlus)
                val b = operands.removeAt(firstPlus)
                operands.add(firstPlus, a + b)
                operators.removeAt(firstPlus)
            } else {
                operands.addFirst(operands.removeFirst() * operands.removeFirst())
                operators.removeFirst()
            }
        }
        return operands[0]
    }

    private fun parseExpression(line: String): List<String> {
        return line.split(" ").flatMap { token ->
            token.filter { it == '(' }.toCharArray().map(Char::toString)
                .plus(token.filter { it != '(' && it != ')' })
                .plus(token.filter { it == ')' }.toCharArray().map(Char::toString))
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input18.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}