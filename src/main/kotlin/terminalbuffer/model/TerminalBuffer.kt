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

    private fun validateRowIndex(row: Int) {
        require(row in 0 until height) { "Terminal row index $row out of $height" }
    }

    private fun validateColumnIndex(column: Int) {
        require(column in 0 until width) { "Terminal column index $column out of $width" }
    }

    private fun validateScrollbackRowIndex(row: Int) {
        require(row in 0 until scrollback.size()) { "History row index $row out of ${scrollback.size()}" }
    }

    operator fun get(row: Int): Row {
        validateRowIndex(row)
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
            appendEmptyLine()
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

    fun fillLine(index: Int, char: Char? = null) {
        validateRowIndex(index)
        for (col in 0 until width) setCellAt(index, col, createCell(char))
    }

    fun appendEmptyLine() {
        scrollback.add(screen.scroll())
    }

    fun clearScreen() {
        screen.clear()
        setCursor(CursorPosition(0, 0))
    }

    fun clearAll() {
        screen.clear()
        scrollback.clear()
        setCursor(CursorPosition(0, 0))
    }

    private fun getRowAt(index: Int, fromHistory: Boolean = false): Row {
        if (fromHistory) {
            validateScrollbackRowIndex(index)
            return scrollback[index]
        }

        validateRowIndex(index)
        return screen[index]
    }

    fun getCharAt(row: Int, column: Int, fromHistory: Boolean = false): Char? {
        validateColumnIndex(column)
        return getRowAt(row, fromHistory)[column].character
    }

    fun getAttributesAt(row: Int, column: Int, fromHistory: Boolean = false): Cell {
        validateColumnIndex(column)
        return getRowAt(row, fromHistory)[column]
    }

    fun getLineAsString(index: Int, fromHistory: Boolean = false): String {
        return getRowAt(index, fromHistory).asString()
    }

    fun getScreenContent(): String {
        return (0 until height).joinToString("\n") { screen[it].asString() }
    }

    fun getFullContent(): String {
        val history = (0 until scrollback.size()).map { scrollback[it].asString() }
        val visible = (0 until height).map { screen[it].asString() }
        return (history + visible).joinToString("\n")
    }
}