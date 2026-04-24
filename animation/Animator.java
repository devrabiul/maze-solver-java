package animation;

import algorithm.SolverResult;
import model.Cell;
import model.CellType;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.List;

/**
 * Drives the step-by-step visual animation using a Swing Timer.
 *
 * Phase 1 — replays the visitedOrder list: colors cells VISITED one tick at a time.
 * Phase 2 — replays the path list: colors cells PATH one tick at a time.
 *
 * All UI updates happen on the Event Dispatch Thread (Timer fires on EDT).
 */
public class Animator {

    private Timer timer;
    private final JPanel panel;

    private List<Cell> visitedOrder;
    private List<Cell> path;

    private int visitedIndex;
    private int pathIndex;
    private boolean visitingDone;

    private Runnable onVisitedStep; // called each time a visited cell is painted
    private Runnable onComplete;    // called when animation finishes

    public Animator(JPanel panel) {
        this.panel = panel;
    }

    /**
     * Start animating the given SolverResult at the specified delay (ms per step).
     *
     * @param result        the solver output to animate
     * @param delay         milliseconds between each animation tick
     * @param onVisitedStep callback fired on each visited-cell step (for counters)
     * @param onComplete    callback fired when the entire animation ends
     */
    public void animate(SolverResult result, int delay,
                        Runnable onVisitedStep, Runnable onComplete) {
        this.visitedOrder = result.visitedOrder;
        this.path = result.path;
        this.visitedIndex = 0;
        this.pathIndex = 0;
        this.visitingDone = false;
        this.onVisitedStep = onVisitedStep;
        this.onComplete = onComplete;

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(delay, e -> tick());
        timer.start();
    }

    /** Stop any running animation immediately. */
    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    public boolean isRunning() {
        return timer != null && timer.isRunning();
    }

    // -----------------------------------------------------------------------

    private void tick() {
        if (!visitingDone) {
            // Phase 1: animate explored cells
            if (visitedIndex < visitedOrder.size()) {
                Cell cell = visitedOrder.get(visitedIndex++);
                if (cell.type == CellType.EMPTY) {
                    cell.type = CellType.VISITED;
                }
                if (onVisitedStep != null) onVisitedStep.run();
                panel.repaint();
            } else {
                visitingDone = true;
                // If no path was found, stop immediately
                if (path == null || path.isEmpty()) {
                    timer.stop();
                    if (onComplete != null) onComplete.run();
                }
            }
        } else {
            // Phase 2: animate the final path
            if (pathIndex < path.size()) {
                Cell cell = path.get(pathIndex++);
                if (cell.type != CellType.START && cell.type != CellType.END) {
                    cell.type = CellType.PATH;
                }
                panel.repaint();
            } else {
                timer.stop();
                if (onComplete != null) onComplete.run();
            }
        }
    }
}
