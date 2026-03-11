package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.Cell
import com.vanjasretenovic.terminalbuffer.model.Screen

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScreenTest {

    @Test
    fun `should initialize screen with correct dimensions`() {
        val screen = Screen(5, 3)

        assertEquals(5, screen[0].width)
        assertEquals(5, screen[1].width)
        assertEquals(5, screen[2].width)
    }

    @Test
    fun `should throw when screen dimensions are invalid`() {
        assertThrows(IllegalArgumentException::class.java) {
            Screen(0, 5)
        }

        assertThrows(IllegalArgumentException::class.java) {
            Screen(5, 0)
        }
    }

    @Test
    fun `should access rows by index`() {
        val screen = Screen(5, 3)

        val row = screen[1]

        assertNotNull(row)
        assertEquals(5, row.width)
    }

    @Test
    fun `should throw when accessing invalid row index`() {
        val screen = Screen(5, 3)

        assertThrows(IllegalArgumentException::class.java) {
            screen[-1]
        }

        assertThrows(IllegalArgumentException::class.java) {
            screen[3]
        }
    }

    @Test
    fun `should scroll screen and return removed row`() {
        val screen = Screen(5, 3)

        screen[0][0] = Cell('A')

        val removed = screen.scroll()

        assertEquals('A', removed[0].character)
        assertEquals(5, screen[2].width)
    }

    @Test
    fun `should clear screen`() {
        val screen = Screen(5, 3)

        screen[0][0] = Cell('X')
        screen.clear()

        assertEquals("     ", screen[0].asString())
        assertEquals("     ", screen[1].asString())
        assertEquals("     ", screen[2].asString())
    }

    @Test
    fun `should increase screen width and keep existing content`() {
        val screen = Screen(3, 2)
        screen[0][0] = Cell('A')
        screen[0][1] = Cell('B')
        screen[0][2] = Cell('C')

        val removedRows = screen.resize(5, 2)

        assertEquals(0, removedRows.size)
        assertEquals("ABC  ", screen[0].asString())
        assertEquals("     ", screen[1].asString())
    }

    @Test
    fun `should decrease screen width and truncate existing rows`() {
        val screen = Screen(5, 2)
        screen[0][0] = Cell('A')
        screen[0][1] = Cell('B')
        screen[0][2] = Cell('C')
        screen[0][3] = Cell('D')
        screen[0][4] = Cell('E')

        val removedRows = screen.resize(3, 2)

        assertEquals(0, removedRows.size)
        assertEquals("ABC", screen[0].asString())
        assertEquals("   ", screen[1].asString())
    }

    @Test
    fun `should increase screen height by appending empty rows at bottom`() {
        val screen = Screen(3, 2)
        screen[0][0] = Cell('A')
        screen[1][0] = Cell('B')

        val removedRows = screen.resize(3, 4)

        assertEquals(0, removedRows.size)
        assertEquals("A  ", screen[0].asString())
        assertEquals("B  ", screen[1].asString())
        assertEquals("   ", screen[2].asString())
        assertEquals("   ", screen[3].asString())
    }

    @Test
    fun `should decrease screen height by removing rows from top`() {
        val screen = Screen(3, 4)
        screen[0][0] = Cell('A')
        screen[1][0] = Cell('B')
        screen[2][0] = Cell('C')
        screen[3][0] = Cell('D')

        val removedRows = screen.resize(3, 2)

        assertEquals(2, removedRows.size)
        assertEquals("A  ", removedRows[0].asString())
        assertEquals("B  ", removedRows[1].asString())
        assertEquals("C  ", screen[0].asString())
        assertEquals("D  ", screen[1].asString())
    }

    @Test
    fun `should resize width and height together`() {
        val screen = Screen(5, 3)
        screen[0][0] = Cell('A')
        screen[1][0] = Cell('B')
        screen[2][0] = Cell('C')

        val removedRows = screen.resize(2, 2)

        assertEquals(1, removedRows.size)
        assertEquals("A ", removedRows[0].asString())
        assertEquals("B ", screen[0].asString())
        assertEquals("C ", screen[1].asString())
    }
}