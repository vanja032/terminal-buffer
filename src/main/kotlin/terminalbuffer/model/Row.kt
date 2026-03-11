package com.vanjasretenovic.terminalbuffer.model

class Row(val initialWidth: Int) {
    var width: Int
        private set

    init {
        require(initialWidth > 0) { "Width must be greater than zero" }
        width = initialWidth
    }

    private val cells: MutableList<Cell> = MutableList(initialWidth) { Cell() }

    private fun validateColumnIndex(column: Int){
        require(column in 0 until width) { "Invalid column index $column. Column index must be in range (0,${width - 1})" }
    }

    operator fun get(column: Int): Cell {
        validateColumnIndex(column)
        return cells[column]
    }

    operator fun set(column: Int, cell: Cell) {
        validateColumnIndex(column)
        cells[column] = cell
    }

    fun fill(cell: Cell = Cell()) {
        for(i in cells.indices) {
            cells[i] = cell
        }
    }

    fun resize(newWidth: Int) {
        require(newWidth > 0) { "New width must be greater than zero" }
        when {
            newWidth > width -> { repeat(newWidth - width) { cells.addLast(Cell()) } }
            newWidth < width -> { repeat( width - newWidth) { cells.removeLast() } }
        }
        width = newWidth
    }

    fun asString() = cells.joinToString(separator = "") { it.character?.toString() ?: " " }
}
