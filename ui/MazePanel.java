package ui;

import model.Cell;
import model.CellType;
import model.Grid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The interactive grid component.
 *
 * Mouse behaviour:
 *   - Default mode: left-drag places walls, right-drag removes walls.
 *   - Start/End mode: single click places the start (green) or end (red) cell.
 */
public class MazePanel extends JPanel {

    private static final int CELL_SIZE = 30;

    private final Grid grid;
    private Cell startCell = null;
    private Cell endCell   = null;

    // When true, the next click sets start/end instead of toggling walls
    private boolean settingStart = false;
    private boolean settingEnd   = false;

    public MazePanel(Grid grid) {
        this.grid = grid;
        int w = grid.getCols() * CELL_SIZE;
        int h = grid.getRows() * CELL_SIZE;
        setPreferredSize(new Dimension(w, h));
        setBackground(new Color(20, 20, 30));

        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Dragging only paints/erases walls — not start/end
                if (!settingStart && !settingEnd) {
                    handleWallDrag(e);
                }
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    // -----------------------------------------------------------------------
    // Mouse handling
    // -----------------------------------------------------------------------

    private void handleClick(MouseEvent e) {
        Cell cell = cellAt(e.getX(), e.getY());
        if (cell == null) return;

        if (settingStart) {
            if (cell == endCell) return; // refuse to overlap
            if (startCell != null) startCell.type = CellType.EMPTY;
            startCell = cell;
            cell.type = CellType.START;
            settingStart = false;
        } else if (settingEnd) {
            if (cell == startCell) return;
            if (endCell != null) endCell.type = CellType.EMPTY;
            endCell = cell;
            cell.type = CellType.END;
            settingEnd = false;
        } else {
            toggleWall(e, cell);
        }
        repaint();
    }

    private void handleWallDrag(MouseEvent e) {
        Cell cell = cellAt(e.getX(), e.getY());
        if (cell == null) return;
        toggleWall(e, cell);
        repaint();
    }

    private void toggleWall(MouseEvent e, Cell cell) {
        if (cell.type == CellType.START || cell.type == CellType.END) return;
        if (SwingUtilities.isLeftMouseButton(e)) {
            cell.type = CellType.WALL;
        } else if (SwingUtilities.isRightMouseButton(e)) {
            cell.type = CellType.EMPTY;
        }
    }

    /** Returns the Cell under pixel coordinate (px, py), or null if out of bounds. */
    private Cell cellAt(int px, int py) {
        int col = px / CELL_SIZE;
        int row = py / CELL_SIZE;
        if (row < 0 || row >= grid.getRows() || col < 0 || col >= grid.getCols()) return null;
        return grid.getCell(row, col);
    }

    // -----------------------------------------------------------------------
    // Painting
    // -----------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Cell[][] cells = grid.getCells();
        int rows = grid.getRows();
        int cols = grid.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                // Fill cell
                g2.setColor(colorFor(cells[r][c].type));
                g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);

                // Grid line
                g2.setColor(new Color(60, 60, 75));
                g2.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private Color colorFor(CellType type) {
        switch (type) {
            case WALL:    return new Color(15, 15, 20);
            case START:   return new Color(34, 197, 94);   // green
            case END:     return new Color(239, 68, 68);   // red
            case VISITED: return new Color(125, 211, 252); // light blue
            case PATH:    return new Color(250, 204, 21);  // yellow
            default:      return new Color(245, 245, 250); // near-white
        }
    }

    // -----------------------------------------------------------------------
    // Accessors used by ControlPanel
    // -----------------------------------------------------------------------

    public void setSettingStart(boolean v) { settingStart = v; settingEnd = false; }
    public void setSettingEnd(boolean v)   { settingEnd = v;   settingStart = false; }

    public Cell getStartCell() { return startCell; }
    public Cell getEndCell()   { return endCell; }
    public void setStartCell(Cell c) { startCell = c; }
    public void setEndCell(Cell c)   { endCell   = c; }

    public Grid getGrid() { return grid; }
    public int getCellSize() { return CELL_SIZE; }
}
