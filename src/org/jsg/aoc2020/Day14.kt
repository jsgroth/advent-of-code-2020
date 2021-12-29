package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day14 {
    private fun solvePart1(lines: List<String>): ULong {
        val memory = mutableMapOf<ULong, ULong>()

        var zeroMasks = ULong.MAX_VALUE
        var oneMasks = 0UL

        lines.forEach { line ->
            if (line.startsWith("mask")) {
                val (newZeroMasks, newOneMasks, _) = parseBitMask(line)
                zeroMasks = newZeroMasks
                oneMasks = newOneMasks
            } else if (line.startsWith("mem")) {
                val (address, value) = parseAssignment(line)
                memory[address] = value.and(zeroMasks).or(oneMasks)
            } else {
                throw IllegalArgumentException("$line")
            }
        }

        return memory.values.sum()
    }

    private fun solvePart2(lines: List<String>): ULong {
        val memory = mutableMapOf<ULong, ULong>()

        var oneMasks = 0UL
        var floatingBits = listOf<Int>()

        lines.forEach { line ->
            if (line.startsWith("mask")) {
                val (_, newOneMasks, newFloatingBits) = parseBitMask(line)
                oneMasks = newOneMasks
                floatingBits = newFloatingBits
            } else if (line.startsWith("mem")) {
                val (address, value) = parseAssignment(line)
                val maskedAddress = address.or(oneMasks)
                setValue(memory, floatingBits, maskedAddress, value)
            } else {
                throw IllegalArgumentException("$line")
            }
        }

        return memory.values.sum()
    }

    private fun setValue(memory: MutableMap<ULong, ULong>, floatingBits: List<Int>, address: ULong, value: ULong) {
        memory[address] = value
        if (floatingBits.isEmpty()) {
            return
        }

        val firstBit = floatingBits[0]
        val restBits = floatingBits.subList(1, floatingBits.size)

        setValue(memory, restBits, address.or(1UL.shl(firstBit)), value)
        setValue(memory, restBits, address.and(1UL.shl(firstBit).inv()), value)
    }

    private fun parseBitMask(line: String): Triple<ULong, ULong, List<Int>> {
        var oneMasks = 0UL
        var zeroMasks = ULong.MAX_VALUE
        val floatingBits = mutableListOf<Int>()

        line.substringAfter("mask = ").forEachIndexed { i, bit ->
            when (bit) {
                '1' -> oneMasks = oneMasks.or(1UL.shl(35 - i))
                '0' -> zeroMasks = zeroMasks.and(1UL.shl(35 - i).inv())
                'X' -> floatingBits.add(35 - i)
            }
        }

        return Triple(zeroMasks, oneMasks, floatingBits.toList())
    }

    private fun parseAssignment(line: String): Pair<ULong, ULong> {
        return Regex("""mem\[(\d+)\] = (\d+)""").matchEntire(line)?.let { matchResult ->
            matchResult.groupValues[1].toULong() to matchResult.groupValues[2].toULong()
        } ?: throw IllegalArgumentException("$line")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input14.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}