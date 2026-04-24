package model;

/**
 * A single cell in the maze grid.
 * Tracks its position, current state, and parent for path reconstruction.
 */
public class Cell {
    public final int row;
    public final int col;
    public CellType type;
    public Cell parent; // used by algorithms for path reconstruction

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.EMPTY;
        this.parent = null;
    }

    /**
     * Soft reset: clears VISITED/PATH state and parent pointer.
     * Preserves WALL, START, and END.
     */
    public void softReset() {
        if (type == CellType.VISITED || type == CellType.PATH) {
            type = CellType.EMPTY;
        }
        parent = null;
    }

    /**
     * Hard reset: clears everything back to EMPTY.
     */
    public void hardReset() {
        type = CellType.EMPTY;
        parent = null;
    }

    @Override
    public String toString() {
        return "Cell[" + row + "," + col + "](" + type + ")";
    }
}
