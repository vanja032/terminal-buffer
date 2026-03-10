package terminalbuffer.model

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
}