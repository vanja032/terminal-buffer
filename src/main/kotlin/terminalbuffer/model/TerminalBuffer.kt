package com.vanjasretenovic.terminalbuffer.model

class TerminalBuffer(
    val width: Int,
    val height: Int,
    val historySize: Int
) {
    init {
        require(width > 0 && height > 0) { "Invalid terminal screen size: $width x $height" }
        require(historySize > 0) { "Invalid history size: $historySize, must be greater than zero" }
    }

    val screen = Screen(width, height)
    val scrollback = Scrollback(historySize)

    operator fun get(row: Int): Row {
        require(row in 0 until height) { "Terminal row index $row out of $height" }
        return screen[row]
    }

    private fun getCellAt(row: Int, column: Int) = screen[row][column]

    private fun setCellAt(row: Int, column: Int, cell: Cell) { screen[row][column] = cell }

    private fun createCell(character: Char?) = Cell(character)
}