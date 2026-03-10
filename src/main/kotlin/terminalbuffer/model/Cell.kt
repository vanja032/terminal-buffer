package com.vanjasretenovic.terminalbuffer.model

data class Cell(
    val character: Char? = null,
    val foreground: TerminalColor = TerminalColor.DEFAULT,
    val background: TerminalColor = TerminalColor.DEFAULT,
    val style: TextStyle = TextStyle(),
) {
    val isEmpty get() = character == null
}
