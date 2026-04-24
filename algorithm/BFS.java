package algorithm;

import model.Cell;
import model.CellType;
import java.util.*;

/**
 * Breadth-First Search — guarantees the shortest path on an unweighted grid.
 * Explores cells level by level using a FIFO queue.
 */
public class BFS implements Solver {

    // 4-directional movement: up, down, left, right
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

        boolean[][] visited = new boolean[rows][cols];
        Queue<Cell> queue = new LinkedList<>();

        queue.add(start);
        visited[start.row][start.col] = true;

        boolean found = false;

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            // Record this cell as visited (skip start/end so colors stay intact)
            if (current.type != CellType.START && current.type != CellType.END) {
                visitedOrder.add(current);
            }

            if (current == end) {
                found = true;
                break;
            }

            for (int[] dir : DIRS) {
                int nr = current.row + dir[0];
                int nc = current.col + dir[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc]
                        && grid[nr][nc].type != CellType.WALL) {
                    visited[nr][nc] = true;
                    grid[nr][nc].parent = current;
                    queue.add(grid[nr][nc]);
                }
            }
        }

        if (found) {
            reconstructPath(end, path);
        }

        return new SolverResult(visitedOrder, path);
    }

    /** Walks parent pointers from end back to start, then reverses. */
    private void reconstructPath(Cell end, List<Cell> path) {
        Cell cur = end;
        while (cur != null) {
            path.add(0, cur);
            cur = cur.parent;
        }
    }
}
