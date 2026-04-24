package algorithm;

import model.Cell;
import model.CellType;
import util.Node;
import java.util.*;

/**
 * A* Search — uses Manhattan Distance heuristic to guide exploration
 * toward the goal. Guarantees the shortest path when the heuristic is admissible.
 */
public class AStar implements Solver {

    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    @Override
    public SolverResult solve(Cell[][] grid, Cell start, Cell end) {
        int rows = grid.length;
        int cols = grid[0].length;

        List<Cell> visitedOrder = new ArrayList<>();
        List<Cell> path = new ArrayList<>();

        // Reset all parent pointers before solving
        for (Cell[] row : grid) {
            for (Cell c : row) c.parent = null;
        }

        // g-cost table — tracks best known cost from start to each cell
        int[][] gCost = new int[rows][cols];
        for (int[] row : gCost) Arrays.fill(row, Integer.MAX_VALUE);
        gCost[start.row][start.col] = 0;

        boolean[][] closed = new boolean[rows][cols]; // settled cells
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        openSet.add(new Node(start, 0, heuristic(start, end)));

        boolean found = false;

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            Cell cell = current.cell;

            // Skip if already settled (a cheaper path was already processed)
            if (closed[cell.row][cell.col]) continue;
            closed[cell.row][cell.col] = true;

            if (cell.type != CellType.START && cell.type != CellType.END) {
                visitedOrder.add(cell);
            }

            if (cell == end) {
                found = true;
                break;
            }

            for (int[] dir : DIRS) {
                int nr = cell.row + dir[0];
                int nc = cell.col + dir[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !closed[nr][nc]
                        && grid[nr][nc].type != CellType.WALL) {

                    int newG = gCost[cell.row][cell.col] + 1;
                    if (newG < gCost[nr][nc]) {
                        gCost[nr][nc] = newG;
                        grid[nr][nc].parent = cell;
                        int h = heuristic(grid[nr][nc], end);
                        openSet.add(new Node(grid[nr][nc], newG, h));
                    }
                }
            }
        }

        if (found) {
            Cell cur = end;
            while (cur != null) {
                path.add(0, cur);
                cur = cur.parent;
            }
        }

        return new SolverResult(visitedOrder, path);
    }

    /** Manhattan Distance: |dr| + |dc| — admissible for 4-directional grids. */
    private int heuristic(Cell a, Cell b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }
}
