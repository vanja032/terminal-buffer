package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.Cell
import com.vanjasretenovic.terminalbuffer.model.Row

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class RowTest {

    @Test
    fun `should initialize row with fixed width and empty cells`() {
        val row = Row(5)

        assertEquals(5, row.width)

        for (i in 0 until row.width) {
            assertEquals(Cell(), row[i])
        }
    }

    @Test
    fun `should throw when width is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            Row(0)
        }
    }

    @Test
    fun `should get and set cell by column index`() {
        val row = Row(5)
        val cell = Cell(character = 'A')

        row[2] = cell

        assertEquals(cell, row[2])
    }

    @Test
    fun `should throw when column index is out of bounds`() {
        val row = Row(5)

        assertThrows(IllegalArgumentException::class.java) {
            row[-1]
        }

        assertThrows(IllegalArgumentException::class.java) {
            row[5]
        }

        assertThrows(IllegalArgumentException::class.java) {
            row[5] = Cell(character = 'X')
        }
    }

    @Test
    fun `should fill row and convert it to string`() {
        val row = Row(4)
        val filledCell = Cell(character = 'X')

        row.fill(filledCell)

        assertEquals("XXXX", row.asString())
    }
}