package util;

import model.Cell;

/**
 * A priority-queue node used by the A* algorithm.
 * Stores g (cost from start), h (heuristic to end), and f = g + h.
 */
public class Node implements Comparable<Node> {
    public final Cell cell;
    public final int g; // cost from start
    public final int h; // heuristic estimate to end
    public final int f; // total estimated cost

    public Node(Cell cell, int g, int h) {
        this.cell = cell;
        this.g = g;
        this.h = h;
        this.f = g + h;
    }

    @Override
    public int compareTo(Node other) {
        // Lower f-cost has higher priority
        int cmp = Integer.compare(this.f, other.f);
        if (cmp != 0) return cmp;
        return Integer.compare(this.h, other.h); // tie-break by h
    }
}
