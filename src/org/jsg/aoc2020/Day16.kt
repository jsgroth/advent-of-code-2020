package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day16 {
    data class Rule(val name: String, val range1: IntRange, val range2: IntRange)

    data class Ticket(val fieldValues: List<Int>)

    private fun solvePart1(lines: List<String>): Int {
        val (rules, _, nearbyTickets) = parseInput(lines)

        return nearbyTickets.sumOf { ticket ->
            ticket.fieldValues.sumOf { fieldValue ->
                if (!rules.any { rule -> rule.range1.contains(fieldValue) || rule.range2.contains(fieldValue) }) {
                    fieldValue
                } else {
                    0
                }
            }
        }
    }

    private fun solvePart2(lines: List<String>): Long {
        val (rules, yourTicket, nearbyTickets) = parseInput(lines)

        val validTickets = nearbyTickets.filter { ticket ->
            ticket.fieldValues.all { fieldValue ->
                rules.any { rule -> rule.range1.contains(fieldValue) || rule.range2.contains(fieldValue) }
            }
        }

        val reorderedRules = rules.sortedBy { rule ->
            rules.indices.count { i ->
                validTickets.all { ticket ->
                    val fieldValue = ticket.fieldValues[i]
                    rule.range1.contains(fieldValue) || rule.range2.contains(fieldValue)
                }
            }
        }

        val ruleOrdering = findRuleOrdering(reorderedRules, validTickets)!!

        val ruleToIndex = ruleOrdering.entries.associate { (k, v) -> v to k }

        val departureRules = rules.filter { it.name.startsWith("departure")}

        return departureRules.fold(1L) { product, rule ->
            product * yourTicket.fieldValues[ruleToIndex[rule]!!]
        }
    }

    private fun findRuleOrdering(
        rules: List<Rule>,
        tickets: List<Ticket>,
        foundRules: Map<Int, Rule> = mapOf(),
        foundRulesSet: Set<Rule> = setOf(),
    ): Map<Int, Rule>? {
        if (foundRules.size == rules.size) {
            return foundRules
        }

        val i = rules.indices.first { !foundRules.containsKey(it) }
        val fieldValues = tickets.map { it.fieldValues[i] }
        for (j in rules.indices) {
            val rule = rules[j]
            if (foundRulesSet.contains(rule)) {
                continue
            }

            if (fieldValues.all { rule.range1.contains(it) || rule.range2.contains(it) }) {
                val newFoundRules = foundRules.plus(i to rule)
                val newFoundRulesSet = foundRulesSet.plus(rule)
                findRuleOrdering(rules, tickets, newFoundRules, newFoundRulesSet)?.let { return it }
            }
        }

        return null
    }

    private fun parseInput(lines: List<String>): Triple<List<Rule>, Ticket, List<Ticket>> {
        val rules = lines.takeWhile(String::isNotBlank).map { line ->
            val nameEnd = line.indexOf(':')
            val name = line.substring(0, nameEnd)
            val (range1String, _, range2String) = line.substring(nameEnd + 2).split(" ")
            val range1 = range1String.split("-").let { it[0].toInt()..it[1].toInt() }
            val range2 = range2String.split("-").let { it[0].toInt()..it[1].toInt() }
            Rule(name, range1, range2)
        }

        val yourTicket = Ticket(lines.dropWhile(String::isNotBlank).drop(2)[0].split(",").map(String::toInt))

        val nearbyTickets = lines.dropWhile(String::isNotBlank).drop(5).map { line ->
            Ticket(line.split(",").map(String::toInt))
        }

        return Triple(rules, yourTicket, nearbyTickets)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input16.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}