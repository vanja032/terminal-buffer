package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.CursorPosition
import com.vanjasretenovic.terminalbuffer.model.Row
import com.vanjasretenovic.terminalbuffer.model.TerminalBuffer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class TerminalBufferTest {

    @Test
    fun `should initialize terminal buffer with screen and scrollback`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        assertNotNull(buffer.screen)
        assertNotNull(buffer.scrollback)
        assertEquals(5, buffer.width)
        assertEquals(3, buffer.height)
        assertEquals(10, buffer.historySize)
    }

    @Test
    fun `should expose screen rows through buffer index operator`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        val row: Row = buffer[1]

        assertEquals(5, row.width)
    }

    @Test
    fun `should throw when width is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(width = 0, height = 3, historySize = 10)
        }
    }

    @Test
    fun `should throw when height is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(width = 5, height = 0, historySize = 10)
        }
    }

    @Test
    fun `should throw when scrollback size is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(width = 5, height = 3, historySize = 0)
        }
    }

    @Test
    fun `should throw when row index is out of bounds`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer[-1]
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer[3]
        }
    }

    @Test
    fun `should initialize cursor at top left position`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should set cursor within screen bounds`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        buffer.setCursor(CursorPosition(row = 2, column = 4))

        assertEquals(2, buffer.cursor.row)
        assertEquals(4, buffer.cursor.column)
    }

    @Test
    fun `should throw when setting cursor outside screen row bounds`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = -1, column = 0))
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = 3, column = 0))
        }
    }

    @Test
    fun `should throw when setting cursor outside screen column bounds`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = 0, column = -1))
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = 0, column = 5))
        }
    }

    @Test
    fun `should move cursor up`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(2, 2))
        buffer.moveCursorUp()

        assertEquals(1, buffer.cursor.row)
    }

    @Test
    fun `should clamp cursor when moving above screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(0, 2))
        buffer.moveCursorUp(5)

        assertEquals(0, buffer.cursor.row)
    }

    @Test
    fun `should move cursor down`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(0, 2))
        buffer.moveCursorDown()

        assertEquals(1, buffer.cursor.row)
    }

    @Test
    fun `should clamp cursor when moving below screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(2, 2))
        buffer.moveCursorDown(5)

        assertEquals(2, buffer.cursor.row)
    }

    @Test
    fun `should move cursor left`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(1, 3))
        buffer.moveCursorLeft()

        assertEquals(2, buffer.cursor.column)
    }

    @Test
    fun `should clamp cursor when moving left beyond screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(1, 0))
        buffer.moveCursorLeft(5)

        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should move cursor right`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(1, 1))
        buffer.moveCursorRight()

        assertEquals(2, buffer.cursor.column)
    }

    @Test
    fun `should clamp cursor when moving right beyond screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(1, 4))
        buffer.moveCursorRight(5)

        assertEquals(4, buffer.cursor.column)
    }

    @Test
    fun `should write text within single line`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        buffer.writeText("ABC")

        assertEquals("ABC  ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(0, buffer.cursor.row)
        assertEquals(3, buffer.cursor.column)
    }

    @Test
    fun `should wrap text to next line when reaching line end`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        buffer.writeText("ABCDE")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should write text across multiple lines`() {
        val buffer = TerminalBuffer(width = 5, height = 3, historySize = 10)

        buffer.writeText("ABCDEFG")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals("FG   ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(2, buffer.cursor.column)
    }

    @Test
    fun `should scroll screen when writing past bottom`() {
        val buffer = TerminalBuffer(width = 5, height = 2, historySize = 10)

        buffer.writeText("ABCDEFGHIJK")

        assertEquals("FGHIJ", buffer[0].asString())
        assertEquals("K    ", buffer[1].asString())

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(1, buffer.cursor.column)
    }

    @Test
    fun `should preserve multiple scrolled rows in scrollback`() {
        val buffer = TerminalBuffer(width = 5, height = 2, historySize = 10)

        buffer.writeText("ABCDEFGHIJKLMNOP")

        assertEquals(2, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
        assertEquals("FGHIJ", buffer.scrollback[1].asString())

        assertEquals("KLMNO", buffer[0].asString())
        assertEquals("P    ", buffer[1].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(1, buffer.cursor.column)
    }

    @Test
    fun `should respect scrollback max size while scrolling`() {
        val buffer = TerminalBuffer(width = 5, height = 2, historySize = 2)

        buffer.writeText("ABCDEFGHIJKLMNOPQRSTU")

        assertEquals(2, buffer.scrollback.size())
        assertEquals("FGHIJ", buffer.scrollback[0].asString())
        assertEquals("KLMNO", buffer.scrollback[1].asString())

        assertEquals("PQRST", buffer[0].asString())
        assertEquals("U    ", buffer[1].asString())
    }
}