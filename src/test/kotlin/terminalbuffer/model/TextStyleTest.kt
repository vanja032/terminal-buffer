package terminalbuffer.model

import com.vanjasretenovic.terminalbuffer.model.TextStyle

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TextStyleTest {

    @Test
    fun `should create default text style with all flags disabled`() {
        val style = TextStyle()

        assertFalse(style.bold)
        assertFalse(style.italic)
        assertFalse(style.underline)
    }

    @Test
    fun `should create text style with selected flags enabled`() {
        val style = TextStyle(
            bold = true,
            italic = false,
            underline = true
        )

        assertTrue(style.bold)
        assertFalse(style.italic)
        assertTrue(style.underline)
    }
}