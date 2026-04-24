package model;

/**
 * Represents the visual/logical state of a cell in the maze grid.
 */
public enum CellType {
    EMPTY,   // walkable, unvisited
    WALL,    // blocked
    START,   // starting cell (green)
    END,     // target cell (red)
    VISITED, // explored by algorithm (light blue)
    PATH     // final shortest/found path (yellow)
}
