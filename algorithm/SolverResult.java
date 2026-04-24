package algorithm;

import model.Cell;
import java.util.List;

/**
 * Holds the output of a pathfinding algorithm:
 *   - visitedOrder: cells explored in traversal order (for animation)
 *   - path: the reconstructed route from start to end (empty if none found)
 */
public class SolverResult {
    public final List<Cell> visitedOrder;
    public final List<Cell> path;

    public SolverResult(List<Cell> visitedOrder, List<Cell> path) {
        this.visitedOrder = visitedOrder;
        this.path = path;
    }

    /** Returns true if a path was found. */
    public boolean pathFound() {
        return path != null && !path.isEmpty();
    }
}
