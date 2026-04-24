package ui;

import algorithm.*;
import animation.Animator;
import model.Cell;
import model.CellType;
import model.Grid;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

/**
 * Right-side control panel: algorithm selector, speed slider,
 * action buttons, live statistics, and legend.
 */
public class ControlPanel extends JPanel {

    private final MazePanel mazePanel;
    private final Animator  animator;

    // Controls
    private JComboBox<String> algoSelector;
    private JSlider           speedSlider;
    private JButton           solveBtn;
    private JButton           resetBtn;
    private JButton           clearBtn;
    private JButton           setStartBtn;
    private JButton           setEndBtn;
    private JButton           generateBtn;

    // Stats
    private JLabel statusLabel;
    private JLabel stepCountLabel;
    private JLabel pathLengthLabel;

    public ControlPanel(MazePanel mazePanel) {
        this.mazePanel = mazePanel;
        this.animator  = new Animator(mazePanel);

        setPreferredSize(new Dimension(220, mazePanel.getPreferredSize().height));
        setBackground(new Color(28, 28, 38));
        setBorder(new EmptyBorder(15, 12, 15, 12));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        buildUI();
        wireActions();
    }

    // -----------------------------------------------------------------------
    // UI construction
    // -----------------------------------------------------------------------

    private void buildUI() {
        addTitle("Maze Solver");
        addGap(18);

        // Algorithm
        sectionLabel("Algorithm");
        algoSelector = new JComboBox<>(new String[]{"BFS", "DFS", "A*"});
        styleCombo(algoSelector);
        add(algoSelector);
        addGap(16);

        // Speed
        sectionLabel("Speed");
        add(buildSpeedRow());
        addGap(18);

        // Grid setup
        sectionLabel("Grid Setup");
        setStartBtn = makeBtn("Set Start", new Color(34, 197, 94));
        setEndBtn   = makeBtn("Set End",   new Color(239, 68, 68));
        generateBtn = makeBtn("Generate Maze", new Color(139, 92, 246));
        add(setStartBtn); addGap(5);
        add(setEndBtn);   addGap(5);
        add(generateBtn); addGap(18);

        // Actions
        sectionLabel("Actions");
        solveBtn = makeBtn("Solve",     new Color(59, 130, 246));
        resetBtn = makeBtn("Reset",     new Color(245, 158, 11));
        clearBtn = makeBtn("Clear All", new Color(107, 114, 128));
        add(solveBtn); addGap(5);
        add(resetBtn); addGap(5);
        add(clearBtn); addGap(20);

        // Statistics
        sectionLabel("Statistics");
        stepCountLabel  = statLabel("Explored: 0");
        pathLengthLabel = statLabel("Path length: 0");
        statusLabel     = statLabel("Ready");
        statusLabel.setForeground(new Color(34, 197, 94));
        add(stepCountLabel);  addGap(3);
        add(pathLengthLabel); addGap(3);
        add(statusLabel);     addGap(20);

        // Legend
        buildLegend();
    }

    private JPanel buildSpeedRow() {
        speedSlider = new JSlider(10, 300, 80);
        speedSlider.setInverted(true); // right = slow (high delay), left = fast (low delay)
        speedSlider.setBackground(new Color(28, 28, 38));
        speedSlider.setForeground(Color.WHITE);
        speedSlider.setMaximumSize(new Dimension(196, 30));

        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setBackground(new Color(28, 28, 38));
        row.setMaximumSize(new Dimension(200, 30));
        row.add(tinyLabel("Fast"), BorderLayout.WEST);
        row.add(speedSlider,       BorderLayout.CENTER);
        row.add(tinyLabel("Slow"), BorderLayout.EAST);
        return row;
    }

    private void buildLegend() {
        sectionLabel("Legend");
        Object[][] entries = {
            {new Color(245, 245, 250), "Empty"},
            {new Color(15, 15, 20),    "Wall"},
            {new Color(34, 197, 94),   "Start"},
            {new Color(239, 68, 68),   "End"},
            {new Color(125, 211, 252), "Visited"},
            {new Color(250, 204, 21),  "Path"},
        };
        for (Object[] e : entries) {
            add(legendRow((Color) e[0], (String) e[1]));
            addGap(2);
        }
    }

    // -----------------------------------------------------------------------
    // Button / event wiring
    // -----------------------------------------------------------------------

    private void wireActions() {
        setStartBtn.addActionListener(e -> {
            mazePanel.setSettingStart(true);
            setStatus("Click a cell to set Start", new Color(34, 197, 94));
        });

        setEndBtn.addActionListener(e -> {
            mazePanel.setSettingEnd(true);
            setStatus("Click a cell to set End", new Color(239, 68, 68));
        });

        solveBtn.addActionListener(e -> runSolver());

        resetBtn.addActionListener(e -> {
            if (animator.isRunning()) animator.stop();
            Grid grid = mazePanel.getGrid();
            grid.softReset();
            // softReset preserves START/END types, but restore references just in case
            restoreStartEnd();
            resetStats();
            setStatus("Ready", new Color(34, 197, 94));
            mazePanel.repaint();
        });

        clearBtn.addActionListener(e -> {
            if (animator.isRunning()) animator.stop();
            mazePanel.getGrid().hardReset();
            mazePanel.setStartCell(null);
            mazePanel.setEndCell(null);
            resetStats();
            setStatus("Ready", new Color(34, 197, 94));
            mazePanel.repaint();
        });

        generateBtn.addActionListener(e -> generateMaze());
    }

    private void runSolver() {
        if (animator.isRunning()) return;

        Cell start = mazePanel.getStartCell();
        Cell end   = mazePanel.getEndCell();

        if (start == null || end == null) {
            setStatus("Set Start & End first!", Color.ORANGE);
            return;
        }

        // Soft-reset so we can re-run without restarting
        mazePanel.getGrid().softReset();
        restoreStartEnd();
        mazePanel.repaint();

        // Select algorithm
        Solver solver;
        switch ((String) algoSelector.getSelectedItem()) {
            case "DFS": solver = new DFS();   break;
            case "A*":  solver = new AStar(); break;
            default:    solver = new BFS();
        }

        SolverResult result = solver.solve(mazePanel.getGrid().getCells(), start, end);

        setStatus("Solving...", Color.CYAN);
        resetStats();

        final int[] explored = {0};

        animator.animate(
            result,
            speedSlider.getValue(),
            () -> stepCountLabel.setText("Explored: " + (++explored[0])),
            () -> {
                if (result.pathFound()) {
                    // Subtract start and end from display count
                    int pathLen = Math.max(0, result.path.size() - 2);
                    pathLengthLabel.setText("Path length: " + pathLen);
                    setStatus("Path found!", new Color(34, 197, 94));
                } else {
                    setStatus("No path found", new Color(239, 68, 68));
                }
            }
        );
    }

    private void generateMaze() {
        if (animator.isRunning()) animator.stop();
        Grid grid = mazePanel.getGrid();
        grid.hardReset();
        mazePanel.setStartCell(null);
        mazePanel.setEndCell(null);

        Random rand = new Random();
        Cell[][] cells = grid.getCells();

        // ~30% random walls
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                if (rand.nextFloat() < 0.30f) {
                    cells[r][c].type = CellType.WALL;
                }
            }
        }

        // Default start = top-left, end = bottom-right
        Cell s = cells[0][0];
        Cell e = cells[grid.getRows() - 1][grid.getCols() - 1];
        s.type = CellType.START;
        e.type = CellType.END;
        mazePanel.setStartCell(s);
        mazePanel.setEndCell(e);

        resetStats();
        setStatus("Maze generated!", new Color(139, 92, 246));
        mazePanel.repaint();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Ensure start/end cells keep their type after a soft reset. */
    private void restoreStartEnd() {
        Cell s = mazePanel.getStartCell();
        Cell e = mazePanel.getEndCell();
        if (s != null) s.type = CellType.START;
        if (e != null) e.type = CellType.END;
    }

    private void resetStats() {
        stepCountLabel.setText("Explored: 0");
        pathLengthLabel.setText("Path length: 0");
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    // -----------------------------------------------------------------------
    // Widget factory methods
    // -----------------------------------------------------------------------

    private void addTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        add(lbl);
    }

    private void sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(140, 140, 165));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        add(lbl);
        addGap(5);
    }

    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(196, 34));
        btn.setPreferredSize(new Dimension(196, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel tinyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lbl.setForeground(new Color(160, 160, 180));
        return lbl;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(new Color(48, 48, 62));
        box.setForeground(Color.WHITE);
        box.setFont(new Font("SansSerif", Font.PLAIN, 13));
        box.setMaximumSize(new Dimension(196, 34));
        box.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JPanel legendRow(Color color, String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 1));
        row.setBackground(new Color(28, 28, 38));
        row.setMaximumSize(new Dimension(196, 20));
        row.setAlignmentX(LEFT_ALIGNMENT);

        JPanel swatch = new JPanel();
        swatch.setPreferredSize(new Dimension(13, 13));
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 120), 1));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(new Color(200, 200, 210));

        row.add(swatch);
        row.add(lbl);
        return row;
    }

    private void addGap(int h) {
        add(Box.createVerticalStrut(h));
    }
}
