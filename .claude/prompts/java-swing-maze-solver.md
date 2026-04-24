# MASTER PROMPT — Java Swing Maze Solver (BFS / DFS / A*)

You are an expert Java software engineer and GUI developer. Build a complete, runnable Java Swing desktop application: a Maze Solver Visualizer with animation and multiple pathfinding algorithms.

## PROJECT GOAL

Create a Maze Solver GUI application using Java Swing that visually demonstrates maze solving using:
- Breadth-First Search (BFS)
- Depth-First Search (DFS)
- A* Search Algorithm

The application must allow interactive grid editing, start/end selection, and animated step-by-step visualization of the algorithm.

## CORE FEATURES (MANDATORY)

### 1. Java Swing GUI
- Window-based application (JFrame)
- Grid-based maze editor (2D cells)
- Mouse interaction:
  - Left click: place wall
  - Right click: remove wall
- UI controls panel:
  - Start button
  - Reset button
  - Algorithm selector (Dropdown: BFS, DFS, A*)
  - Speed slider (animation speed)

### 2. Maze Grid System
- 2D grid (recommended 20x20 or scalable)
- Each cell has states:
  - EMPTY
  - WALL
  - START
  - END
  - VISITED
  - PATH

### 3. Pathfinding Algorithms

Implement clean, separate classes for:

**BFS (Shortest Path guaranteed)**
- Use Queue
- Track parents for path reconstruction

**DFS (Not optimal but fast exploration)**
- Use Stack or recursion
- Track visited nodes

**A* Algorithm**
- Use PriorityQueue
- Heuristic: Manhattan Distance

### 4. Visualization / Animation (VERY IMPORTANT)
- Must show step-by-step traversal
- Use Swing Timer (javax.swing.Timer)
- Animate:
  - visited nodes (blue/gray)
  - final path (yellow/green)
- No freezing UI (must be non-blocking)

### 5. Start & End Selection
- User can click grid to set:
  - Start node (green)
  - End node (red)
- Only one start and one end allowed

### 6. Reset Functionality
- Clears: visited nodes, path
- Keeps or resets walls (must be consistent)
- Allows re-run without restarting app

## PROJECT STRUCTURE (MANDATORY CLEAN DESIGN)

```
maze-solver/
│
├── Main.java
├── ui/
│   ├── MazeFrame.java
│   ├── MazePanel.java
│   ├── ControlPanel.java
│
├── model/
│   ├── Cell.java
│   ├── Grid.java
│   ├── CellType.java
│
├── algorithm/
│   ├── Solver.java (interface)
│   ├── BFS.java
│   ├── DFS.java
│   ├── AStar.java
│
├── util/
│   ├── Node.java
│   ├── Pair.java
│
└── animation/
    └── Animator.java
```

## DESIGN REQUIREMENTS

### SOLVER INTERFACE

```java
public interface Solver {
    void solve(Cell[][] grid, Cell start, Cell end, MazePanel panel);
}
```

Each algorithm must:
- Update visited nodes in real time
- Call `repaint()` on panel
- Store parent pointers for path reconstruction

### THREADING RULE (IMPORTANT)
- Do NOT freeze UI
- Use: Swing Timer OR Background Thread (SwingWorker optional)
- Animation must be smooth

## UI REQUIREMENTS
- Clean modern Swing layout
- Left: grid panel
- Right: control panel
- Buttons: "Solve", "Reset"
- Dropdown: BFS / DFS / A*
- Speed slider: Slow → Fast animation

## RUN REQUIREMENT
- Fully runnable from `Main.java`
- No external libraries
- Pure Java (JDK 8+ compatible)
- Compiles without errors

## VISUALIZATION RULES

| Cell State | Color      |
|------------|------------|
| Wall       | BLACK      |
| Empty      | WHITE      |
| Start      | GREEN      |
| End        | RED        |
| Visited    | LIGHT BLUE |
| Path       | YELLOW     |

## EXTRA CREDIT FEATURES (IF POSSIBLE)
- Diagonal movement toggle
- Maze generator (random walls)
- Step counter (nodes explored)
- Path length display
- Speed presets (Slow / Medium / Fast)

## OUTPUT EXPECTATION

Generate:
- Full Java source code
- Proper folder structure
- No pseudo-code
- No missing classes
- Ready-to-run project

## IMPORTANT
- Keep code clean, modular, and well-commented
- Avoid putting everything in one file
- Ensure algorithms are correct and independent
- Ensure UI responsiveness
