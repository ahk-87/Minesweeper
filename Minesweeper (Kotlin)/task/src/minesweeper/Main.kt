package minesweeper

import kotlin.random.Random

enum class GameState { WIN, LOSE, ONGOING }

class Minesweeper(var mines: Int = 0) {

    val field = List(9) { MutableList<Char>(9) { '.' } }
    val userField = List(9) { MutableList<Char>(9) { '.' } }
    val minesPositions = mutableListOf<Pair<Int, Int>>()
    val userMineMarks = mutableListOf<Pair<Int, Int>>()

    init {
        setMines()
        setEmptyCells()
    }

    fun setMines() {
        while (mines != 0) {
            val x = Random.nextInt(0, 9)
            val y = Random.nextInt(0, 9)
            if (field[x][y] != 'X') {
                field[x][y] = 'X'
                minesPositions.add(x to y)
                mines--
            }
        }
    }

    fun setEmptyCells() {
        for (i in 0..8) {
            for (j in 0..8) {
                if (field[i][j] == '.') {
                    val numberOfMines = checkMinesAroundCell(i, j)
                    field[i][j] = if (numberOfMines == 0) '/' else numberOfMines.digitToChar()
                }
            }
        }
    }

    fun checkMinesAroundCell(x: Int, y: Int): Int {
        var count = 0
        for (i in (x - 1).coerceAtLeast(0)..(x + 1).coerceAtMost(8)) {
            for (j in (y - 1).coerceAtLeast(0)..(y + 1).coerceAtMost(8)) {
                if (field[i][j] == 'X') count++
            }
        }
        return count
    }

    fun playGame(x: Int, y: Int, mode: String): GameState {
        if (mode == "mine") {
            placeMark(x, y)
        } else if (mode == "free") {
            if (field[x][y] == 'X') {
                minesPositions.forEach { userField[it.first][it.second] = 'X' }
                printGame()
                return GameState.LOSE
            }
            openCells(x, y)
            printGame()
        }
        if (userField.sumOf { list -> list.count { it == '*' || it == '.' } } == minesPositions.size ||
            userMineMarks.size == minesPositions.size && userMineMarks.containsAll(minesPositions))
            return GameState.WIN

        return GameState.ONGOING
    }

    fun placeMark(x: Int, y: Int) {
        if (userField[x][y] == '.') {
            userMineMarks.add(x to y)
            userField[x][y] = '*'
            printGame()
        } else if (userField[x][y] == '*') {
            userMineMarks.remove(x to y)
            userField[x][y] = '.'
            printGame()
        } else
            println("There is a number here!")
    }

    private fun openCells(x: Int, y: Int) {
        if (userField[x][y] in listOf('.', '*')) {
            if (userField[x][y] == '*') userMineMarks.remove(x to y)
        } else return
        userField[x][y] = field[x][y]
        if (userField[x][y] == '/') {
            for (i in (x - 1).coerceAtLeast(0)..(x + 1).coerceAtMost(8)) {
                for (j in (y - 1).coerceAtLeast(0)..(y + 1).coerceAtMost(8)) {
                    openCells(i, j)
                }
            }
        }
    }

    fun printGame() {
        val separator = "—│—————————│"
        println(" │123456789│")
        println(separator)
        userField.forEachIndexed() { i, it ->
            println(it.joinToString("", prefix = "${i + 1}|", postfix = "|"))
        }
        println(separator)
    }
}

fun main() {

    println("How many mines do you want on the field?")
    val mines = readln().toInt()
    val game = Minesweeper(mines)
    game.printGame()
    while (true) {
        print("Set/unset mine marks or claim a cell as free: ")
        val input = readln()
        println()
        val (x, y, mode) = input.split(" ")
        val state = game.playGame(y.toInt() - 1, x.toInt() - 1, mode)
        if (state == GameState.WIN) {
            println("Congratulations! You found all the mines!")
            break
        } else if (state == GameState.LOSE) {
            println("You stepped on a mine and failed!")
            break
        }
    }
}