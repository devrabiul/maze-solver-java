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

        JPanel root = new JPanel(new BorderLayout(10, 0));
        root.setBackground(new Color(18, 18, 28));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(mazePanel,    BorderLayout.CENTER);
        root.add(controlPanel, BorderLayout.EAST);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null); // centre on screen
    }
}
