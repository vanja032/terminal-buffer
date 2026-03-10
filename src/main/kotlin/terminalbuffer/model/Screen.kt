package com.vanjasretenovic.terminalbuffer.model

class Screen(
    val width: Int,
    val height: Int
) {
    init {
        require(width > 0) { "Screen width must be greater than zero." }
        require(height > 0) { "Screen height must be greater than zero." }
    }

    private val rows: ArrayDeque<Row> = ArrayDeque(List(height) { Row(width) })

    private fun validateRowIndex(row: Int) {
        require(row in 0 until height) { "Invalid row index $row. Row index must be in range (0, ${height - 1})" }
    }

    operator fun get(row: Int): Row {
        validateRowIndex(row)
        return rows[row]
    }

    fun scroll(): Row{
        val removed = rows.removeFirst()
        rows.addLast(Row(width))
        return removed
    }

    fun clear() {
        rows.clear()
        repeat(height) { rows.addLast(Row(width)) }
    }
}