package com.vanjasretenovic.terminalbuffer.model

class Row(val width: Int) {
    init {
        require(width > 0) { "Width must be greater than zero" }
    }

    private val cells: MutableList<Cell> = MutableList(width) { Cell() }

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

    fun asString() = cells.joinToString(separator = "") { it.character?.toString() ?: " " }
}
