package ui;

import model.Grid;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 * Lays out the MazePanel (center) and ControlPanel (right side).
 */
public class MazeFrame extends JFrame {

    public MazeFrame() {
        super("Maze Solver Visualizer  —  BFS | DFS | A*");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Grid grid = new Grid(20, 20);
        MazePanel   mazePanel    = new MazePanel(grid);
        ControlPanel controlPanel = new ControlPanel(mazePanel);

        // Thin separator between grid and sidebar
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setForeground(new Color(40, 40, 60));
        sep.setPreferredSize(new Dimension(1, 0));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(13, 13, 22));
        root.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        root.add(mazePanel,    BorderLayout.CENTER);
        root.add(sep,          BorderLayout.EAST);
        root.add(controlPanel, BorderLayout.EAST);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null); // centre on screen
    }
}
