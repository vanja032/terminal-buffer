package com.vanjasretenovic.terminalbuffer.model

class Screen(
    val initialWidth: Int,
    val initialHeight: Int
) {
    var width: Int
        private set
    var height: Int
        private set

    init {
        require(initialWidth > 0) { "Screen width must be greater than zero." }
        require(initialHeight > 0) { "Screen height must be greater than zero." }
        width = initialWidth
        height = initialHeight
    }

    private val rows: ArrayDeque<Row> = ArrayDeque(List(initialHeight) { Row(initialWidth) })

    private fun validateRowIndex(row: Int) {
        require(row in 0 until height) { "Invalid row index $row. Row index must be in range (0, ${height - 1})" }
    }

    operator fun get(row: Int): Row {
        validateRowIndex(row)
        return rows.elementAt(row)
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

    fun resize(newWidth: Int, newHeight: Int): List<Row> {
        require(newWidth > 0) { "New width must be greater than zero" }
        require(newHeight > 0) { "New height must be greater than zero" }

        for (i in 0 until rows.size) rows.elementAt(i).resize(newWidth)

        val removed: MutableList<Row> = mutableListOf()
        when {
            newHeight > height -> repeat(newHeight - height) { rows.addLast(Row(newWidth)) }
            newHeight < height -> repeat(height - newHeight) { removed.addLast(rows.removeFirst()) }
        }

        width = newWidth
        height = newHeight

        return removed
    }
}