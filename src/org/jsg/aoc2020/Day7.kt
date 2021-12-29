package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day7 {
    data class Bag(val name: String, val contains: List<Pair<String, Int>>)

    private fun solvePart1(lines: List<String>): Int {
        val bags = parseLines(lines)

        val bagsMap = bags.associateBy(Bag::name)

        return bags.count { canContain(it, "shiny gold", bagsMap) }
    }

    private fun solvePart2(lines: List<String>): Int {
        val bags = parseLines(lines)

        val bagsMap = bags.associateBy(Bag::name)

        return countInnerBags(bagsMap["shiny gold"]!!, bagsMap)
    }

    private fun canContain(bag: Bag, name: String, bagMap: Map<String, Bag>): Boolean {
        return bag.contains.any { (containsName, _) ->
            containsName == name || canContain(bagMap[containsName]!!, name, bagMap)
        }
    }

    private fun countInnerBags(bag: Bag, bagMap: Map<String, Bag>): Int {
        return bag.contains.sumOf { (bagName, count) ->
            count * (countInnerBags(bagMap[bagName]!!, bagMap) + 1)
        }
    }

    private fun parseLines(lines: List<String>): List<Bag> {
        return lines.map { line ->
            val (bagName, containsString) = line.split(" bags contain ")
            val allContains = containsString.split(", ")
            val containsParsed = if (allContains[0] == "no other bags.") {
                listOf()
            } else {
                allContains.map { bagString ->
                    val bagStringSplit = bagString.split(" ")
                    val count = bagStringSplit[0].toInt()
                    val name = bagStringSplit.subList(1, bagStringSplit.size - 1).joinToString(separator = " ")
                    name to count
                }
            }
            Bag(bagName, containsParsed)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input7.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}