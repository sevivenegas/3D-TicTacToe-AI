# 3D Tic-Tac-Toe AI

> A command-line 3D (4×4×4) Tic-Tac-Toe game with an AI opponent powered by **Minimax search with alpha-beta pruning**.

---

## Overview

This is not your standard Tic-Tac-Toe. The game is played on a **4×4×4 three-dimensional board** — four stacked 4×4 layers. To win, a player must place **4 in a row** across any of the **76 possible winning lines**, which span rows, columns, pillars, face diagonals, and 3D space diagonals through the cube.

The AI uses a **Minimax algorithm with alpha-beta pruning** and a **heuristic evaluation function** that scores board positions based on unblocked threat counts across all 76 lines. Search depth is automatically increased as the board fills up.

---

## How to Run

### 1. Compile

```bash
javac *.java
```

### 2. Play

**AI goes first (default):**
```bash
java Temp
```

**Human goes first:**
```bash
java Temp -second
```

**Custom search depth:**
```bash
java Temp -plies 5
```

**Load a preset board state:**
```bash
java Temp -start "X...O............................................................."
```
Use `X`, `O`, and `.` to represent the 64 cells in order.

---

## Gameplay

The board is printed as **four 4×4 layers** side by side after each move. Each turn you enter three coordinates (all 1-indexed):

```
Layer  (1–4) → which of the four stacked planes
Column (1–4) → left to right within the layer
Row    (1–4) → bottom to top within the layer
```

You play as **O**. The AI plays as **X**. First to align 4 in a row wins.

**Example board display** (four layers printed side by side):
```
....    ....    ....    ....
....    ....    ....    ....
....    ..X.    ....    ....
....    ....    ....    O...
```

---

## Architecture

| File | Responsibility |
|---|---|
| `Temp.java` | Main game loop and CLI entry point |
| `Board.java` | 4×4×4 board state using a bitboard representation |
| `Minimax.java` | Minimax with alpha-beta pruning |
| `Coordinate.java` | 3D (x, y, z) ↔ linear position encoding |
| `Line.java` | All 76 winning lines precomputed as bitmasks |
| `Plane.java` | The 18 cross-sectional planes of the 3D board |
| `Bit.java` | Low-level bitwise utility functions |
| `Player.java` | Player enum — X (AI), O (human), EMPTY |
| `Game.java` | Generic two-player game state interface |

---

## How the AI Works

### Minimax with Alpha-Beta Pruning

The AI performs a depth-limited minimax search, treating itself as the **maximizing player** (X) and the human as the **minimizing player** (O). Alpha-beta pruning eliminates branches that cannot influence the final decision, drastically reducing the search space.

Search depth automatically increases at turns 32 and 48 — when fewer empty squares remain, deeper searches are computationally affordable. When multiple moves tie for the best score, one is chosen randomly to avoid repetitive play.

### Heuristic Evaluation

Board positions are scored by scanning all **76 winning lines**:

| Condition | Score |
|---|---|
| X has 4-in-a-row | `+∞` (immediate win) |
| O has 4-in-a-row | `-∞` (immediate loss) |
| X has unblocked 3-in-a-row | +100 per line |
| O has unblocked 3-in-a-row | -100 per line |
| X has unblocked 2-in-a-row | +10 per line |
| O has unblocked 2-in-a-row | -10 per line |

A line is **unblocked** if only one player has pieces on it. The active player's threat counts are weighted 2× to bias the AI toward offensive play.

---

## The 76 Winning Lines

| Type | Count | Description |
|---|---|---|
| Straight lines | 48 | Rows, columns, and pillars (16 per axis) |
| Face diagonals | 24 | Forward + reverse diagonals in each layer (8 per axis) |
| Space diagonals | 4 | Main diagonals passing through the center of the cube |
| **Total** | **76** | |

---

## Bitboard Representation

The board stores two `long` values — one for X's pieces, one for O's — where **bit `i` is set if that player occupies position `i`**. Positions are encoded as:

```
position = z × 16 + y × 4 + x
```

This allows win detection and move generation to use fast bitwise AND against precomputed line masks instead of looping over cells.
