package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.Cell
import com.vanjasretenovic.terminalbuffer.model.TerminalColor
import com.vanjasretenovic.terminalbuffer.model.TextStyle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CellTest {

    @Test
    fun `should create empty cell with default attributes`() {
        val cell = Cell()

        assertNull(cell.character)
        assertEquals(TerminalColor.DEFAULT, cell.foreground)
        assertEquals(TerminalColor.DEFAULT, cell.background)
        assertEquals(TextStyle(), cell.style)
        assertTrue(cell.isEmpty)
    }

    @Test
    fun `should create non-empty cell with custom attributes`() {
        val style = TextStyle(
            bold = true,
            italic = true,
            underline = false
        )

        val cell = Cell(
            character = 'A',
            foreground = TerminalColor.GREEN,
            background = TerminalColor.BLACK,
            style = style
        )

        assertEquals('A', cell.character)
        assertEquals(TerminalColor.GREEN, cell.foreground)
        assertEquals(TerminalColor.BLACK, cell.background)
        assertEquals(style, cell.style)
        assertFalse(cell.isEmpty)
    }

    @Test
    fun `should treat space as non-empty character`() {
        val cell = Cell(character = ' ')

        assertEquals(' ', cell.character)
        assertFalse(cell.isEmpty)
    }
}