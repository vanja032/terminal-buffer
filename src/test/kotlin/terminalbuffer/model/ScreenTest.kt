package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.Cell
import com.vanjasretenovic.terminalbuffer.model.Screen

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScreenTest {

    @Test
    fun `should initialize screen with correct dimensions`() {
        val screen = Screen(width = 5, height = 3)

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
}