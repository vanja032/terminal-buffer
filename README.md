# Terminal Text Buffer

This project implements a simplified terminal text buffer similar to the internal data structures used by terminal emulators. The goal was to design a clean, testable model that stores and manipulates terminal output while keeping the responsibilities of each component clearly separated.

The implementation focuses on correctness, predictable behavior, and maintainable design rather than full terminal emulation.

---

# Overview

A terminal buffer stores the text that appears in a terminal window. Internally it is represented as a **grid of character cells**.

The buffer is divided into two logical parts:

- **Screen** – the visible part of the terminal
- **Scrollback** – history containing lines that have scrolled off the screen

Each position in the grid contains a **cell** which stores a character and formatting attributes.

---

# Architecture

The implementation is organized into several core components:

```
TerminalBuffer      (width x height)
│
├── Screen          (width x height)
│   └── Row         (width)
│       └── Cell
│
└── Scrollback      (width x maxHistorySize)
    └── Row         (width)
        └── Cell
```

## Cell

Represents a single terminal cell.

A cell stores:

- character
- foreground color
- background color
- text style

Cells are the smallest unit of rendering in the buffer.

---

## Row

Represents a horizontal line of cells.

Responsibilities:

- storing cells
- providing indexed access to cells
- resizing row width when the terminal width changes

Each row maintains the invariant:

cells.size == width

---

## Screen

Represents the visible terminal grid.

Responsibilities:

- storing rows
- scrolling rows when new lines are appended
- resizing width and height
- returning rows that leave the screen during resize

---

## Scrollback (History)

Stores rows that scrolled off the screen.

Responsibilities:

- maintaining terminal history
- enforcing maximum history size
- resizing row width when the terminal width changes

---

## TerminalBuffer

`TerminalBuffer` is the main orchestrator that coordinates all operations.

Responsibilities:

- cursor movement
- text editing operations
- screen manipulation
- scrollback management
- content access
- resize orchestration

---

# Cursor

The buffer maintains a cursor position which determines where the next character will be written.

Supported cursor operations:

- set cursor position
- move up
- move down
- move left
- move right

Cursor movement is clamped to valid screen bounds.

---

# Text Editing Operations

The following editing operations are implemented.

### Write text

Writes characters starting from the current cursor position and overwrites existing content.

When the cursor reaches the end of the row, writing continues on the next line.

If the bottom of the screen is reached, scrolling occurs.

---

### Insert text

Inserts text at the current cursor position.

Existing characters are shifted to the right. Overflow cascades across rows and may trigger scrolling if the bottom of the screen is reached.

The cursor position after insertion remains at the end of the inserted text.

---

### Fill line

Replaces all cells in a specified row with a given character or empty cells.

This is useful for terminal operations that clear or reset lines.

---

# Screen Operations

### Append empty line

Adds an empty line to the bottom of the screen.

The top row of the screen is moved to scrollback.

---

### Clear screen

Resets the visible screen by filling all rows with empty cells.

Scrollback remains unchanged.

---

### Clear screen and scrollback

Clears both the screen and the scrollback history.

Cursor is reset to the top-left position.

---

# Content Access

The buffer exposes several read-only methods for retrieving terminal content.

Supported operations:

- get character at position
- get cell attributes at position
- get line as string
- get entire screen content
- get full content (scrollback + screen)

These operations do not modify the internal buffer state.

---

# Resize Support (Bonus)

Terminal resizing is implemented as a structural transformation of the buffer.

The resize operation affects:

- screen width
- screen height
- scrollback row width

### Width Increase

Rows are extended with empty cells.

### Width Decrease

Rows are truncated on the right side.

### Height Increase

New empty rows are appended at the bottom of the screen.

### Height Decrease

Rows removed from the top of the screen are moved to scrollback.

### Cursor Behavior

After resizing, the cursor is clamped to the new screen bounds.

---

# Testing

The project includes unit tests covering:

- row resizing
- screen resizing
- scrollback resizing
- terminal buffer resize behavior
- cursor movement
- text editing operations
- content retrieval

Tests cover boundary conditions and edge cases to ensure buffer consistency.

---

# Design Decisions

### Grid-based model

The terminal is implemented as a grid of cells rather than a continuous text stream. This reflects how most terminal emulators internally represent the screen state.

---

### No text reflow on resize

When the width changes, rows are resized independently. Text is not reflowed across lines. This simplifies the implementation and preserves buffer structure.

---

# Possible Improvements

Future improvements could include:

- support for wide characters (CJK / emoji occupying two cells)
- ANSI escape sequence handling
- more advanced cursor and editing operations

---

# Running Tests

```bash
./gradlew test
```

---

# Author

Vanja Sretenović