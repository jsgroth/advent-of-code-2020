package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day4 {
    enum class PassportField(val fieldName: String) {
        BIRTH_YEAR("byr") {
            override fun validate(fieldValue: String): Boolean {
                return fieldValue.matches(Regex("""\d{4}""")) &&
                        (1920..2002).contains(fieldValue.toInt())
            }
        },
        ISSUE_YEAR("iyr") {
            override fun validate(fieldValue: String): Boolean {
                return fieldValue.matches(Regex("""\d{4}""")) &&
                        (2010..2020).contains(fieldValue.toInt())
            }
        },
        EXPIRATION_YEAR("eyr") {
            override fun validate(fieldValue: String): Boolean {
                return fieldValue.matches(Regex("""\d{4}""")) &&
                        (2020..2030).contains(fieldValue.toInt())
            }
        },
        HEIGHT("hgt") {
            override fun validate(fieldValue: String): Boolean {
                if (!fieldValue.matches(Regex("""\d{2,3}(cm|in)"""))) {
                    return false
                }
                val heightNum = fieldValue.substring(0, fieldValue.length - 2).toInt()
                return if (fieldValue.substring(fieldValue.length - 2, fieldValue.length) == "cm") {
                    (150..193).contains(heightNum)
                } else {
                    (59..76).contains(heightNum)
                }
            }
        },
        HAIR_COLOR("hcl") {
            override fun validate(fieldValue: String): Boolean {
                return fieldValue.matches(Regex("""#[0-9a-f]{6}"""))
            }
        },
        EYE_COLOR("ecl") {
            override fun validate(fieldValue: String): Boolean {
                return setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(fieldValue)
            }
        },
        PASSPORT_ID("pid") {
            override fun validate(fieldValue: String): Boolean {
                return fieldValue.matches(Regex("""\d{9}"""))
            }
        }
        ;

        abstract fun validate(fieldValue: String): Boolean
    }

    private fun solvePart1(lines: List<String>): Int {
        val passports = parsePassports(lines)

        val mustHave = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")

        return passports.count { passport ->
            mustHave.all { fieldName -> passport.find { it.first == fieldName } != null }
        }
    }

    private fun solvePart2(lines: List<String>): Int {
        val passports = parsePassports(lines)

        return passports.count { passport ->
            PassportField.values().all { field ->
                passport.find { it.first == field.fieldName && field.validate(it.second) } != null
            }
        }
    }

    private fun parsePassports(lines: List<String>): List<List<Pair<String, String>>> {
        val passports = mutableListOf<List<Pair<String, String>>>()
        var i = 0
        while (i < lines.size) {
            val end = lines.subList(i, lines.size).indexOf("").let { if (it < 0) lines.size else it + i }
            val passport = lines.subList(i, end).flatMap { line ->
                line.split(" ").map { it.split(":") }
            }
                .map { it[0] to it[1] }
            passports.add(passport)

            i = end + 1
        }
        return passports.toList()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input4.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}