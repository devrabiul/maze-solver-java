package algorithm;

import model.Cell;

/**
 * Common interface for all pathfinding algorithms.
 * Each implementation returns a SolverResult containing the visited order
 * and the reconstructed path — enabling the Animator to replay them step by step.
 */
public interface Solver {
    /**
     * Runs the pathfinding algorithm on the given grid.
     *
     * @param grid  2D array of cells (walls already set)
     * @param start the starting cell
     * @param end   the target cell
     * @return a SolverResult with visitedOrder and path lists
     */
    SolverResult solve(Cell[][] grid, Cell start, Cell end);
}
