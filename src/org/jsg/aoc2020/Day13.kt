package org.jsg.aoc2020

import java.nio.file.Files
import java.nio.file.Path

object Day13 {
    private fun solvePart1(lines: List<String>): Int {
        val earliestDepartureTime = lines[0].toInt()
        val busIds = lines[1].split(",").filter { it != "x" }.map(String::toInt)
        val busIdDepartureTimes = busIds.map { busId ->
            busId to ((earliestDepartureTime / busId) + 1) * busId
        }
        val (minBusId, minDepartureTime) = busIdDepartureTimes.minByOrNull(Pair<Int, Int>::second)!!
        return minBusId * (minDepartureTime - earliestDepartureTime)
    }

    private fun solvePart2(lines: List<String>): Long {
        val busIdIndexPairs = lines[1].split(",").mapIndexed { i, token ->
            if (token == "x") null else token.toInt() to i
        }.filterNotNull()

        val congruences = busIdIndexPairs.map { (busId, index) ->
            (-index).mod(busId) to busId
        }

        return solveCongruences(congruences)
    }

    private fun solveCongruences(congruences: List<Pair<Int, Int>>): Long {
        val n = congruences.fold(1L) { p, (_, ni) -> p * ni }

        return congruences.sumOf { (ai, ni) ->
            val yi = n / ni
            val (_, zi, _) = extendedEuclidean(yi, ni.toLong())
            ai * yi * zi
        }.mod(n)
    }

    private fun extendedEuclidean(a: Long, b: Long): Triple<Long, Long, Long> {
        if (a < b) {
            return extendedEuclidean(b, a).let { (r, s, t) -> Triple(r, t, s) }
        }

        var (prevR, prevS, prevT) = Triple(a, 1L, 0L)
        var (r, s, t) = Triple(b, 0L, 1L)
        while (prevR % r != 0L) {
            val q = prevR / r

            val (tr, ts, tt) = Triple(r, s, t)

            r = prevR - r * q
            s = prevS - s * q
            t = prevT - t * q

            prevR = tr
            prevS = ts
            prevT = tt
        }

        return Triple(r, s, t)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val lines = Files.readAllLines(Path.of("input13.txt"), Charsets.UTF_8)
        val solution1 = solvePart1(lines)
        println(solution1)
        val solution2 = solvePart2(lines)
        println(solution2)
    }
}