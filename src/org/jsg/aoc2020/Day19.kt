package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day19 {
    sealed interface Rule

    data class SingleCharRule(val id: Int, val char: Char) : Rule

    data class CompoundRule(val id: Int, val ruleIds: List<Int>) : Rule

    data class OrRule(val id: Int, val leftRuleIds: List<Int>, val rightRuleIds: List<Int>) : Rule

    data class Rule8(val subId: Int = 42) : Rule

    data class Rule11(val leftSubId: Int = 42, val rightSubId: Int = 31) : Rule

    data class Input(
        val singleCharRules: List<SingleCharRule>,
        val compoundRules: List<CompoundRule>,
        val orRules: List<OrRule>,
        val testStrings: List<String>,
    )

    private fun solvePart1(lines: List<String>): Int {
        val (singleCharRules, compoundRules, orRules, testStrings) = parseInput(lines)

        val rulesMap = singleCharRules.map { it.id to it }
            .plus(compoundRules.map { it.id to it })
            .plus(orRules.map { it.id to it })
            .toMap()

        return testStrings.count { testString ->
            testMatch(testString, 0, rulesMap).contains(testString.length)
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        val (singleCharRules, compoundRules, orRules, testStrings) = parseInput(lines)

        val rulesMap = singleCharRules.asSequence().map { it.id to it }
            .plus(compoundRules.filter { it.id != 8 && it.id != 11 }.map { it.id to it })
            .plus(orRules.map { it.id to it })
            .plus(8 to Rule8())
            .plus(11 to Rule11()).toList()
            .toMap()

        return testStrings.count { testString ->
            testMatch(testString, 0, rulesMap).contains(testString.length)
        }
    }

    private fun testMatch(
        s: String,
        ruleId: Int,
        rules: Map<Int, Rule>,
        i: Int = 0,
    ): Set<Int> {
        if (i >= s.length) {
            return setOf()
        }

        return when (val rule = rules[ruleId]!!) {
            is SingleCharRule -> if (s[i] == rule.char) setOf(i + 1) else setOf()
            is CompoundRule -> testCompoundMatch(s, rule.ruleIds, rules, i)
            is OrRule -> {
                testCompoundMatch(s, rule.leftRuleIds, rules, i).plus(testCompoundMatch(s, rule.rightRuleIds, rules, i))
            }
            is Rule8 -> testRule8(s, rule, rules, i)
            is Rule11 -> testRule11(s, rule, rules, i)
        }
    }

    private fun testCompoundMatch(
        s: String,
        ruleIds: List<Int>,
        rules: Map<Int, Rule>,
        i: Int,
    ): Set<Int> {
        val first = testMatch(s, ruleIds[0], rules, i)
        return ruleIds.drop(1).fold(first) { runningSet, subRuleId ->
            runningSet.flatMap { j ->
                testMatch(s, subRuleId, rules, j)
            }.toSet()
        }
    }

    private fun testRule8(
        s: String,
        rule: Rule8,
        rules: Map<Int, Rule>,
        i: Int,
    ): Set<Int> {
        if (i >= s.length) {
            return setOf()
        }

        val subMatches = testMatch(s, rule.subId, rules, i)
        return subMatches.plus(subMatches.flatMap { subMatch ->
            testRule8(s, rule, rules, subMatch)
        }.toSet())
    }

    private fun testRule11(
        s: String,
        rule: Rule11,
        rules: Map<Int, Rule>,
        i: Int,
    ): Set<Int> {
        if (i >= s.length) {
            return setOf()
        }

        val leftMatches = testMatch(s, rule.leftSubId, rules, i)
        if (leftMatches.isEmpty()) {
            return setOf()
        }

        val midMatches = leftMatches.plus(testRule11(s, rule, rules, leftMatches.first()))

        return midMatches.flatMap { midMatch ->
            testMatch(s, rule.rightSubId, rules, midMatch)
        }.toSet()
    }

    private fun parseInput(lines: List<String>): Input {
        val singleCharRules = mutableListOf<SingleCharRule>()
        val compoundRules = mutableListOf<CompoundRule>()
        val orRules = mutableListOf<OrRule>()

        lines.takeWhile(String::isNotBlank).forEach { line ->
            val ruleId = line.takeWhile { it != ':' }.toInt()
            val split = line.split(" ").drop(1)
            if (split.size == 1 && split[0][0] == '"') {
                singleCharRules.add(SingleCharRule(ruleId, split[0][1]))
            } else if (split.contains("|")) {
                val pipeIndex = split.indexOf("|")
                val left = split.take(pipeIndex).map(String::toInt)
                val right = split.drop(pipeIndex + 1).map(String::toInt)
                orRules.add(OrRule(ruleId, left, right))
            } else {
                compoundRules.add(CompoundRule(ruleId, split.map(String::toInt)))
            }
        }

        val testStrings = lines.dropWhile(String::isNotBlank).drop(1)

        return Input(singleCharRules, compoundRules, orRules, testStrings)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input19.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}