import ui.MazeFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point.
 * Launches the Maze Solver Visualizer on the Swing Event Dispatch Thread.
 *
 * Compile & run:
 *   javac -d out $(find . -name "*.java")
 *   java  -cp out Main
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MazeFrame frame = new MazeFrame();
            frame.setVisible(true);
        });
    }
}
