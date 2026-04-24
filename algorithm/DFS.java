package algorithm;

import model.Cell;
import model.CellType;
import java.util.*;

/**
 * Depth-First Search — explores as deep as possible before backtracking.
 * Does NOT guarantee the shortest path, but can find a path quickly.
 * Uses an explicit Stack to avoid recursive stack-overflow on large grids.
 */
public class DFS implements Solver {

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
        Deque<Cell> stack = new ArrayDeque<>();

        stack.push(start);
        visited[start.row][start.col] = true;

        boolean found = false;

        while (!stack.isEmpty()) {
            Cell current = stack.pop();

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
                    stack.push(grid[nr][nc]);
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
}
