package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.CursorPosition

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CursorPositionTest {

    @Test
    fun `should create cursor position`() {
        val cursor = CursorPosition(row = 2, column = 4)

        assertEquals(2, cursor.row)
        assertEquals(4, cursor.column)
    }
}