package com.vanjasretenovic.terminalbuffer.model

class Scrollback(val maxSize: Int) {
    init {
        require(maxSize > 0) { "Scrollback size must be greater than 0" }
    }

    private val rows: ArrayDeque<Row> = ArrayDeque()

    private fun validateRowIndex(row: Int) {
        require(row in 0 until rows.size) { "Scrollback row index $row out of $maxSize rows" }
    }

    operator fun get(row: Int): Row {
        validateRowIndex(row)
        return rows.elementAt(row)
    }

    fun add(row: Row) {
        if(rows.size == maxSize) { rows.removeFirst(); }
        rows.addLast(row)
    }

    fun size() = rows.size

    fun clear() { rows.clear(); }

    fun resize(newWidth: Int) {
        require(newWidth > 0) { "New width must be greater than zero" }

        for (i in 0 until rows.size) rows.elementAt(i).resize(newWidth)
    }
}