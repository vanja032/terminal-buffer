package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.CursorPosition
import com.vanjasretenovic.terminalbuffer.model.Row
import com.vanjasretenovic.terminalbuffer.model.TerminalBuffer
import com.vanjasretenovic.terminalbuffer.model.TerminalColor
import com.vanjasretenovic.terminalbuffer.model.TextStyle

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TerminalBufferTest {

    @Test
    fun `should initialize terminal buffer with screen and scrollback`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertNotNull(buffer.screen)
        assertNotNull(buffer.scrollback)
        assertEquals(5, buffer.width)
        assertEquals(3, buffer.height)
        assertEquals(10, buffer.historySize)
    }

    @Test
    fun `should expose screen rows through buffer index operator`() {
        val buffer = TerminalBuffer(5, 3, 10)

        val row: Row = buffer[1]

        assertEquals(5, row.width)
    }

    @Test
    fun `should throw when width is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(0, 3, 10)
        }
    }

    @Test
    fun `should throw when height is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(5, 0, 10)
        }
    }

    @Test
    fun `should throw when scrollback size is not positive`() {
        assertThrows(IllegalArgumentException::class.java) {
            TerminalBuffer(5, 3, 0)
        }
    }

    @Test
    fun `should throw when row index is out of bounds`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer[-1]
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer[3]
        }
    }

    @Test
    fun `should initialize cursor at top left position`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should set cursor within screen bounds`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(row = 2, column = 4))

        assertEquals(2, buffer.cursor.row)
        assertEquals(4, buffer.cursor.column)
    }

    @Test
    fun `should throw when setting cursor outside screen row bounds`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = -1, column = 0))
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer.setCursor(CursorPosition(row = 3, column = 0))
        }
    }

    @Test
    fun `should throw when setting cursor outside screen column bounds`() {
        val buffer = TerminalBuffer(5, 3, 10)

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
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABC")

        assertEquals("ABC  ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(0, buffer.cursor.row)
        assertEquals(3, buffer.cursor.column)
    }

    @Test
    fun `should wrap text to next line when reaching line end`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should write text across multiple lines`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFG")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals("FG   ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.cursor.row)
        assertEquals(2, buffer.cursor.column)
    }

    @Test
    fun `should scroll screen when writing past bottom`() {
        val buffer = TerminalBuffer(5, 2, 10)

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
        val buffer = TerminalBuffer(5, 2, 10)

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
        val buffer = TerminalBuffer(5, 2, 2)

        buffer.writeText("ABCDEFGHIJKLMNOPQRSTU")

        assertEquals(2, buffer.scrollback.size())
        assertEquals("FGHIJ", buffer.scrollback[0].asString())
        assertEquals("KLMNO", buffer.scrollback[1].asString())

        assertEquals("PQRST", buffer[0].asString())
        assertEquals("U    ", buffer[1].asString())
    }

    @Test
    fun `should insert text into empty line`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.insertText("ABC")

        assertEquals("ABC  ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(0, buffer.cursor.row)
        assertEquals(3, buffer.cursor.column)
    }

    @Test
    fun `should insert text in the middle of existing line`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABDE")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("C")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals(0, buffer.cursor.row)
        assertEquals(3, buffer.cursor.column)
    }

    @Test
    fun `should shift characters right when inserting text`() {
        val buffer = TerminalBuffer(6, 3, 10)

        buffer.writeText("ABEF")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("CD")

        assertEquals("ABCDEF", buffer[0].asString())
        assertEquals(0, buffer.cursor.row)
        assertEquals(4, buffer.cursor.column)
    }

    @Test
    fun `should wrap overflow to next line when inserting`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("XY")

        assertEquals("ABXYC", buffer[0].asString())
        assertEquals("DE   ", buffer[1].asString())
        assertEquals(0, buffer.cursor.row)
        assertEquals(4, buffer.cursor.column)
    }

    @Test
    fun `should cascade inserted text across multiple lines`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFGHIJ")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("XY")

        assertEquals("ABXYC", buffer[0].asString())
        assertEquals("DEFGH", buffer[1].asString())
        assertEquals("IJ   ", buffer[2].asString())
    }

    @Test
    fun `should insert after prior write-triggered scroll state`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("XY")

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
        assertEquals("FGXYH", buffer[0].asString())
        assertEquals("IJ   ", buffer[1].asString())
    }

    @Test
    fun `should respect scrollback max size during insert scrolling`() {
        val buffer = TerminalBuffer(5, 2, 1)

        buffer.writeText("ABCDEFGHIJ")
        buffer.setCursor(CursorPosition(0, 2))

        buffer.insertText("XYZ")

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
        assertEquals("FGXYZ", buffer[0].asString())
        assertEquals("HIJ  ", buffer[1].asString())
    }

    @Test
    fun `should insert text at end of line`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCD")
        buffer.setCursor(CursorPosition(0, 4))

        buffer.insertText("E")

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals(1, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should insert into current screen content after prior scroll`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")
        buffer.setCursor(CursorPosition(0, 0))

        buffer.insertText("Z")

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
        assertEquals("ZFGHI", buffer[0].asString())
        assertEquals("J    ", buffer[1].asString())
    }

    @Test
    fun `should fill line with character`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.fillLine(1, 'X')

        assertEquals("XXXXX", buffer[1].asString())
    }

    @Test
    fun `should clear line when filling with null`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.fillLine(0, null)

        assertEquals("     ", buffer[0].asString())
    }

    @Test
    fun `should not modify other lines when filling`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.fillLine(1, 'X')

        assertEquals("ABCDE", buffer[0].asString())
        assertEquals("XXXXX", buffer[1].asString())
    }

    @Test
    fun `should throw when row index is invalid`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.fillLine(3, 'X')
        }
    }

    @Test
    fun `should append empty line at bottom of screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.appendEmptyLine()

        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
    }

    @Test
    fun `should preserve screen height when appending empty line`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.appendEmptyLine()

        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
    }

    @Test
    fun `should append empty row at bottom after scrolling screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.setCursor(CursorPosition(1, 0))
        buffer.writeText("FGHIJ")

        buffer.appendEmptyLine()

        assertEquals("FGHIJ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
    }

    @Test
    fun `should not modify cursor position when appending empty line`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.setCursor(CursorPosition(1, 3))
        buffer.appendEmptyLine()

        assertEquals(1, buffer.cursor.row)
        assertEquals(3, buffer.cursor.column)
    }

    @Test
    fun `should respect scrollback max size when appending empty line`() {
        val buffer = TerminalBuffer(5, 2, 1)

        buffer.writeText("ABCDE")
        buffer.appendEmptyLine()
        buffer.writeText("FGHIJ")
        buffer.appendEmptyLine()

        assertEquals(1, buffer.scrollback.size())
        assertEquals("FGHIJ", buffer.scrollback[0].asString())
    }

    @Test
    fun `should clear all screen rows`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFG")
        buffer.clearScreen()

        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())
    }

    @Test
    fun `should preserve scrollback when clearing screen`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")
        buffer.clearScreen()

        assertEquals(1, buffer.scrollback.size())
        assertEquals("ABCDE", buffer.scrollback[0].asString())
        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
    }

    @Test
    fun `should reset cursor when clearing screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABC")
        buffer.setCursor(CursorPosition(1, 3))

        buffer.clearScreen()

        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should preserve screen dimensions when clearing screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFG")
        buffer.clearScreen()

        assertEquals(5, buffer[0].width)
        assertEquals(5, buffer[1].width)
        assertEquals(5, buffer[2].width)
    }

    @Test
    fun `should clear already empty screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.clearScreen()

        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())
        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should clear screen and scrollback`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")
        buffer.clearAll()

        assertEquals(0, buffer.scrollback.size())
        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
    }

    @Test
    fun `should reset cursor when clearing entire buffer`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDE")
        buffer.setCursor(CursorPosition(2, 3))

        buffer.clearAll()

        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should preserve screen dimensions after clearAll`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFG")
        buffer.clearAll()

        assertEquals(5, buffer[0].width)
        assertEquals(5, buffer[1].width)
        assertEquals(5, buffer[2].width)
    }

    @Test
    fun `should clear scrollback completely`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJKLMNOP")
        assertTrue(buffer.scrollback.size() > 0)

        buffer.clearAll()

        assertEquals(0, buffer.scrollback.size())
    }

    @Test
    fun `should clear already empty buffer`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.clearAll()

        assertEquals(0, buffer.scrollback.size())
        assertEquals("     ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals(0, buffer.cursor.row)
        assertEquals(0, buffer.cursor.column)
    }

    @Test
    fun `should get character from screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABC")

        assertEquals('A', buffer.getCharAt(0, 0))
        assertEquals('B', buffer.getCharAt(0, 1))
        assertEquals('C', buffer.getCharAt(0, 2))
        assertEquals(null, buffer.getCharAt(0, 3))
    }

    @Test
    fun `should get character from scrollback`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")

        assertEquals('A', buffer.getCharAt(0, 0, fromHistory = true))
        assertEquals('E', buffer.getCharAt(0, 4, fromHistory = true))
    }

    @Test
    fun `should get attributes from screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("A")

        val cell = buffer.getAttributesAt(0, 0)

        assertEquals('A', cell.character)
        assertEquals(TerminalColor.DEFAULT, cell.foreground)
        assertEquals(TerminalColor.DEFAULT, cell.background)
        assertEquals(TextStyle(), cell.style)
    }

    @Test
    fun `should get line as string from screen`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABC")

        assertEquals("ABC  ", buffer.getLineAsString(0))
        assertEquals("     ", buffer.getLineAsString(1))
    }

    @Test
    fun `should get line as string from scrollback`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")

        assertEquals("ABCDE", buffer.getLineAsString(0, fromHistory = true))
    }

    @Test
    fun `should get entire screen content as string`() {
        val buffer = TerminalBuffer(5, 3, 10)

        buffer.writeText("ABCDEFG")

        assertEquals(
            "ABCDE\nFG   \n     ",
            buffer.getScreenContent()
        )
    }

    @Test
    fun `should get full content including scrollback and screen`() {
        val buffer = TerminalBuffer(5, 2, 10)

        buffer.writeText("ABCDEFGHIJ")

        assertEquals(
            "ABCDE\nFGHIJ\n     ",
            buffer.getFullContent()
        )
    }

    @Test
    fun `should throw when screen row index is invalid`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.getLineAsString(3)
        }
    }

    @Test
    fun `should throw when scrollback row index is invalid`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.getLineAsString(0, fromHistory = true)
        }
    }

    @Test
    fun `should throw when column index is invalid`() {
        val buffer = TerminalBuffer(5, 3, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.getCharAt(0, 5)
        }
    }

    @Test
    fun `should increase terminal buffer width and preserve content`() {
        val buffer = TerminalBuffer(3, 2, 10)
        buffer.writeText("ABC")

        buffer.resize(5, 2)

        assertEquals(5, buffer.width)
        assertEquals(2, buffer.height)
        assertEquals("ABC  ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
    }

    @Test
    fun `should decrease terminal buffer width and truncate screen and scrollback rows`() {
        val buffer = TerminalBuffer(5, 2, 10)
        buffer.writeText("ABCDEFGHIJ")

        buffer.resize(3, 2)

        assertEquals(3, buffer.width)
        assertEquals(2, buffer.height)
        assertEquals("ABC", buffer.scrollback[0].asString())
        assertEquals("FGH", buffer[0].asString())
        assertEquals("   ", buffer[1].asString())
    }

    @Test
    fun `should increase terminal buffer height by appending empty rows`() {
        val buffer = TerminalBuffer(5, 2, 10)
        buffer.writeText("ABC")

        buffer.resize(5, 4)

        assertEquals(4, buffer.height)
        assertEquals("ABC  ", buffer[0].asString())
        assertEquals("     ", buffer[1].asString())
        assertEquals("     ", buffer[2].asString())
        assertEquals("     ", buffer[3].asString())
    }

    @Test
    fun `should decrease terminal buffer height and move removed rows to scrollback`() {
        val buffer = TerminalBuffer(5, 4, 10)
        buffer.fillLine(0, 'A')
        buffer.fillLine(1, 'B')
        buffer.fillLine(2, 'C')
        buffer.fillLine(3, 'D')

        buffer.resize(5, 2)

        assertEquals(2, buffer.height)
        assertEquals("AAAAA", buffer.scrollback[0].asString())
        assertEquals("BBBBB", buffer.scrollback[1].asString())
        assertEquals("CCCCC", buffer[0].asString())
        assertEquals("DDDDD", buffer[1].asString())
    }

    @Test
    fun `should clamp cursor when resizing terminal buffer`() {
        val buffer = TerminalBuffer(5, 4, 10)
        buffer.setCursor(CursorPosition(3, 4))

        buffer.resize(3, 2)

        assertEquals(1, buffer.cursor.row)
        assertEquals(2, buffer.cursor.column)
    }

    @Test
    fun `should resize scrollback rows when resizing terminal buffer width`() {
        val buffer = TerminalBuffer(5, 2, 10)
        buffer.writeText("ABCDEFGHIJ")

        buffer.resize(3, 2)

        assertEquals("ABC", buffer.scrollback[0].asString())
    }

    @Test
    fun `should throw when resizing terminal buffer to invalid size`() {
        val buffer = TerminalBuffer(5, 2, 10)

        assertThrows(IllegalArgumentException::class.java) {
            buffer.resize(0, 2)
        }

        assertThrows(IllegalArgumentException::class.java) {
            buffer.resize(5, 0)
        }
    }
}