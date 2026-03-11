package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.Cell
import com.vanjasretenovic.terminalbuffer.model.Row
import com.vanjasretenovic.terminalbuffer.model.Scrollback

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ScrollbackTest {

    @Test
    fun `should initialize empty scrollback`() {
        val scrollback = Scrollback(3)

        assertEquals(0, scrollback.size())
    }

    @Test
    fun `should throw when max size is invalid`() {
        assertThrows(IllegalArgumentException::class.java) {
            Scrollback(0)
        }
    }

    @Test
    fun `should add rows to scrollback`() {
        val scrollback = Scrollback(3)

        val row = Row(5)

        scrollback.add(row)

        assertEquals(1, scrollback.size())
        assertEquals(row, scrollback[0])
    }

    @Test
    fun `should discard oldest row when capacity exceeded`() {
        val scrollback = Scrollback(2)

        val row1 = Row(5)
        val row2 = Row(5)
        val row3 = Row(5)

        scrollback.add(row1)
        scrollback.add(row2)
        scrollback.add(row3)

        assertEquals(2, scrollback.size())
        assertEquals(row2, scrollback[0])
        assertEquals(row3, scrollback[1])
    }

    @Test
    fun `should throw when accessing invalid index`() {
        val scrollback = Scrollback(3)

        assertThrows(IllegalArgumentException::class.java) {
            scrollback[0]
        }
    }

    @Test
    fun `should clear scrollback`() {
        val scrollback = Scrollback(3)

        scrollback.add(Row(5))
        scrollback.add(Row(5))

        scrollback.clear()

        assertEquals(0, scrollback.size())
    }

    @Test
    fun `should increase scrollback row widths`() {
        val scrollback = Scrollback(10)

        val row1 = Row(3)
        row1[0] = Cell('A')
        row1[1] = Cell('B')
        row1[2] = Cell('C')

        scrollback.add(row1)
        scrollback.resize(5)

        assertEquals("ABC  ", scrollback[0].asString())
    }

    @Test
    fun `should decrease scrollback row widths`() {
        val scrollback = Scrollback(10)

        val row1 = Row(5)
        row1[0] = Cell('A')
        row1[1] = Cell('B')
        row1[2] = Cell('C')
        row1[3] = Cell('D')
        row1[4] = Cell('E')

        scrollback.add(row1)
        scrollback.resize(3)

        assertEquals("ABC", scrollback[0].asString())
    }

    @Test
    fun `should resize all scrollback rows`() {
        val scrollback = Scrollback(10)

        val row1 = Row(4)
        row1[0] = Cell('A')

        val row2 = Row(4)
        row2[0] = Cell('B')

        scrollback.add(row1)
        scrollback.add(row2)

        scrollback.resize(2)

        assertEquals("A ", scrollback[0].asString())
        assertEquals("B ", scrollback[1].asString())
    }

    @Test
    fun `should keep scrollback size unchanged when resizing width`() {
        val scrollback = Scrollback(10)

        scrollback.add(Row(4))
        scrollback.add(Row(4))

        scrollback.resize(6)

        assertEquals(2, scrollback.size())
    }
}