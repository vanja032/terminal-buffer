package com.vanjasretenovic.terminalbuffer.model

class TerminalBuffer(
    val width: Int,
    val height: Int,
    val historySize: Int
) {
    val screen: Screen
    val scrollback: Scrollback
    var cursor: CursorPosition = CursorPosition(0, 0)
        private set

    init {
        require(width > 0 && height > 0) { "Invalid terminal screen size: $width x $height" }
        require(historySize > 0) { "Invalid history size: $historySize, must be greater than zero" }

        screen = Screen(width, height)
        scrollback = Scrollback(historySize)
    }

    operator fun get(row: Int): Row {
        require(row in 0 until height) { "Terminal row index $row out of $height" }
        return screen[row]
    }

    private fun getCellAt(row: Int, column: Int) = screen[row][column]

    private fun setCellAt(row: Int, column: Int, cell: Cell) { screen[row][column] = cell }

    private fun getCurrentCell() = getCellAt(cursor.row, cursor.column)

    private fun setCurrentCell(cell: Cell) { setCellAt(cursor.row, cursor.column, cell) }

    private fun createCell(character: Char?) = Cell(character)

    private fun validateCursorPosition(cursorPosition: CursorPosition) {
        require(cursorPosition.row in 0 until height) { "Invalid cursor position: ${cursorPosition.row}, ${cursorPosition.column}" }
        require(cursorPosition.column in 0 until width) { "Invalid cursor position: ${cursorPosition.row}, ${cursorPosition.column}" }
    }

    fun setCursor(cursorPosition: CursorPosition) {
        validateCursorPosition(cursorPosition)
        cursor = cursorPosition
    }

    fun moveCursorUp(n: Int = 1) {
        require(n >= 0) { "Movement $n must be non-negative" }
        cursor = cursor.copy(row = (cursor.row - n).coerceAtLeast(0))
    }

    fun moveCursorDown(n: Int = 1) {
        require(n >= 0) { "Movement $n must be non-negative" }
        cursor = cursor.copy(row = (cursor.row + n).coerceAtMost(height - 1))
    }

    fun moveCursorLeft(n: Int = 1) {
        require(n >= 0) { "Movement $n must be non-negative" }
        cursor = cursor.copy(column = (cursor.column - n).coerceAtLeast(0))
    }

    fun moveCursorRight(n: Int = 1) {
        require(n >= 0) { "Movement $n must be non-negative" }
        cursor = cursor.copy(column = (cursor.column + n).coerceAtMost(width - 1))
    }

    fun moveCursorOnStartLine() { cursor = cursor.copy(column = 0) }

    fun writeChar(char: Char) {
        setCurrentCell(createCell(char))

        if (cursor.column == width - 1) newLine()
        else moveCursorRight()
    }

    private fun newLine() {
        moveCursorOnStartLine()

        if (cursor.row < height - 1) {
            moveCursorDown()
        }
        else {
            scrollback.add(screen.scroll())
        }
    }

    fun writeText(text: String) {
        for (char in text) writeChar(char)
    }

    fun insertText(text: String) {
        var index = 0
        var tmpText: String = text
        var savedCursor = CursorPosition(0, 0)

        while (index < tmpText.length) {
            if(!getCurrentCell().isEmpty) tmpText += getCurrentCell().character
            writeChar(tmpText[index++])
            if(index == text.length) savedCursor = cursor.copy()
        }

        cursor = savedCursor.copy()
    }
}