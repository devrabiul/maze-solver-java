package model;

/**
 * Holds the 2D array of cells that forms the maze.
 */
public class Grid {
    private final int rows;
    private final int cols;
    private final Cell[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
        init();
    }

    private void init() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public Cell[][] getCells() {
        return cells;
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /** Clears visited/path state; keeps walls, start, and end. */
    public void softReset() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.softReset();
            }
        }
    }

    /** Resets every cell to EMPTY. */
    public void hardReset() {
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                cell.hardReset();
            }
        }
    }
}
