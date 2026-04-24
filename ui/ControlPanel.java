package ui;

import algorithm.*;
import animation.Animator;
import model.Cell;
import model.CellType;
import model.Grid;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Random;

/**
 * Redesigned sidebar with card-based sections, algorithm toggle buttons,
 * speed presets, stat tiles, and a 2-column legend.
 */
public class ControlPanel extends JPanel {

    // ── Palette ────────────────────────────────────────────────────────────
    private static final Color BG_SIDEBAR   = new Color(13, 13, 22);
    private static final Color BG_CARD      = new Color(24, 24, 38);
    private static final Color BG_TILE      = new Color(18, 18, 30);
    private static final Color BORDER_COLOR = new Color(45, 45, 65);
    private static final Color ACCENT       = new Color(99, 102, 241); // indigo

    // Algorithm colours (also used as visited-cell colours)
    static final Color COLOR_BFS   = new Color(56,  189, 248); // sky blue
    static final Color COLOR_DFS   = new Color(251, 146, 60);  // orange
    static final Color COLOR_ASTAR = new Color(167, 139, 250); // violet

    // Action button colours
    private static final Color C_START    = new Color(34,  197, 94);
    private static final Color C_END      = new Color(239, 68,  68);
    private static final Color C_GENERATE = new Color(139, 92,  246);
    private static final Color C_SOLVE    = new Color(99,  102, 241);
    private static final Color C_RESET    = new Color(234, 179, 8);
    private static final Color C_CLEAR    = new Color(75,  85,  99);

    // ── State ──────────────────────────────────────────────────────────────
    private final MazePanel mazePanel;
    private final Animator  animator;

    // Algorithm toggle
    private JButton bfsBtn, dfsBtn, astarBtn;
    private String  selectedAlgo = "BFS";

    // Speed presets
    private JButton slowPreset, medPreset, fastPreset;

    private JSlider speedSlider;
    private JButton setStartBtn, setEndBtn, generateBtn;
    private JButton solveBtn, resetBtn, clearBtn;

    // Live stat labels
    private JLabel exploredVal, pathVal, statusVal;

    // Legend visited swatch (updated when algo changes)
    private JPanel visitedLegendSwatch;

    // ── Construction ───────────────────────────────────────────────────────

    public ControlPanel(MazePanel mazePanel) {
        this.mazePanel = mazePanel;
        this.animator  = new Animator(mazePanel);

        setBackground(BG_SIDEBAR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(0, 0, 0, 0));
        setPreferredSize(new Dimension(248, mazePanel.getPreferredSize().height));

        buildUI();
        wireActions();

        // Initialise maze panel with BFS colour
        mazePanel.setVisitedColor(COLOR_BFS);
    }

    // ── Layout ─────────────────────────────────────────────────────────────

    private void buildUI() {
        add(buildHeader());
        gap(8);
        add(card("Algorithm", buildAlgoToggle()));
        gap(6);
        add(card("Speed", buildSpeedPanel()));
        gap(6);
        add(card("Grid Setup", buildGridSetupPanel()));
        gap(6);
        add(card("Actions", buildActionsPanel()));
        gap(6);
        add(card("Statistics", buildStatsPanel()));
        gap(6);
        add(card("Legend", buildLegendPanel()));
        gap(8);
    }

    // ── Header ─────────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BG_SIDEBAR);
        header.setBorder(new EmptyBorder(18, 16, 14, 16));
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(248, 80));

        JLabel title = new JLabel("MAZE SOLVER");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Pathfinding Visualizer");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sub.setForeground(new Color(100, 100, 140));
        sub.setAlignmentX(LEFT_ALIGNMENT);

        // Accent underline
        JSeparator line = new JSeparator();
        line.setForeground(ACCENT);
        line.setBackground(ACCENT);
        line.setMaximumSize(new Dimension(248, 2));

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(sub);
        header.add(Box.createVerticalStrut(12));
        header.add(line);

        return header;
    }

    // ── Algorithm toggle ───────────────────────────────────────────────────

    private JPanel buildAlgoToggle() {
        bfsBtn   = algoBtn("BFS",  COLOR_BFS);
        dfsBtn   = algoBtn("DFS",  COLOR_DFS);
        astarBtn = algoBtn("A★",  COLOR_ASTAR);

        markAlgoSelected(bfsBtn, COLOR_BFS);  // BFS selected by default

        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(216, 36));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(bfsBtn);
        row.add(dfsBtn);
        row.add(astarBtn);
        return row;
    }

    private JButton algoBtn(String label, Color color) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(BG_TILE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(color.darker().darker(), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void markAlgoSelected(JButton btn, Color color) {
        btn.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 60)
                // alpha blending manually since Swing ignores alpha on opaque
                // — use a computed solid approximation:
        );
        // Solid approximation of color at ~25% opacity over BG_TILE
        int r = blend(color.getRed(),   BG_TILE.getRed(),   0.30f);
        int g = blend(color.getGreen(), BG_TILE.getGreen(), 0.30f);
        int b = blend(color.getBlue(),  BG_TILE.getBlue(),  0.30f);
        btn.setBackground(new Color(r, g, b));
        btn.setForeground(color);
        btn.setBorder(BorderFactory.createLineBorder(color, 2));
    }

    private void markAlgoUnselected(JButton btn, Color color) {
        btn.setBackground(BG_TILE);
        btn.setForeground(color);
        btn.setBorder(BorderFactory.createLineBorder(color.darker().darker(), 1));
    }

    // ── Speed panel ────────────────────────────────────────────────────────

    private JPanel buildSpeedPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        // Presets row
        slowPreset = presetBtn("Slow");
        medPreset  = presetBtn("Med");
        fastPreset = presetBtn("Fast");

        JPanel presets = new JPanel(new GridLayout(1, 3, 6, 0));
        presets.setBackground(BG_CARD);
        presets.setMaximumSize(new Dimension(216, 28));
        presets.setAlignmentX(LEFT_ALIGNMENT);
        presets.add(slowPreset);
        presets.add(medPreset);
        presets.add(fastPreset);

        // Slider
        speedSlider = new JSlider(10, 300, 80);
        speedSlider.setInverted(true);
        speedSlider.setBackground(BG_CARD);
        speedSlider.setForeground(new Color(160, 160, 200));
        speedSlider.setMaximumSize(new Dimension(216, 28));
        speedSlider.setAlignmentX(LEFT_ALIGNMENT);

        // Labels row
        JPanel labels = new JPanel(new BorderLayout());
        labels.setBackground(BG_CARD);
        labels.setMaximumSize(new Dimension(216, 14));
        labels.add(microLabel("Fast"), BorderLayout.WEST);
        labels.add(microLabel("Slow"), BorderLayout.EAST);

        panel.add(presets);
        panel.add(Box.createVerticalStrut(6));
        panel.add(speedSlider);
        panel.add(labels);
        return panel;
    }

    private JButton presetBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setForeground(new Color(180, 180, 210));
        btn.setBackground(BG_TILE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Grid setup ─────────────────────────────────────────────────────────

    private JPanel buildGridSetupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        // Start + End side by side
        setStartBtn = iconBtn("● Set Start", C_START);
        setEndBtn   = iconBtn("● Set End",   C_END);

        JPanel startEndRow = new JPanel(new GridLayout(1, 2, 6, 0));
        startEndRow.setBackground(BG_CARD);
        startEndRow.setMaximumSize(new Dimension(216, 36));
        startEndRow.setAlignmentX(LEFT_ALIGNMENT);
        startEndRow.add(setStartBtn);
        startEndRow.add(setEndBtn);

        generateBtn = wideBtn("⚡  Generate Maze", C_GENERATE);

        panel.add(startEndRow);
        panel.add(Box.createVerticalStrut(6));
        panel.add(generateBtn);
        return panel;
    }

    // ── Actions ────────────────────────────────────────────────────────────

    private JPanel buildActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_CARD);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        solveBtn = wideBtn("▶   SOLVE", C_SOLVE);
        solveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        solveBtn.setMaximumSize(new Dimension(216, 42));
        solveBtn.setPreferredSize(new Dimension(216, 42));

        resetBtn = iconBtn("↺  Reset",    C_RESET);
        clearBtn = iconBtn("✕  Clear All", C_CLEAR);

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 6, 0));
        bottomRow.setBackground(BG_CARD);
        bottomRow.setMaximumSize(new Dimension(216, 34));
        bottomRow.setAlignmentX(LEFT_ALIGNMENT);
        bottomRow.add(resetBtn);
        bottomRow.add(clearBtn);

        panel.add(solveBtn);
        panel.add(Box.createVerticalStrut(6));
        panel.add(bottomRow);
        return panel;
    }

    // ── Stats ──────────────────────────────────────────────────────────────

    private JPanel buildStatsPanel() {
        exploredVal = bigStatLabel("0");
        pathVal     = bigStatLabel("0");
        statusVal   = bigStatLabel("—");
        statusVal.setForeground(new Color(74, 222, 128));

        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setBackground(BG_CARD);
        row.setMaximumSize(new Dimension(216, 58));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(statTile("Explored", exploredVal));
        row.add(statTile("Path",     pathVal));
        row.add(statTile("Status",   statusVal));
        return row;
    }

    private JPanel statTile(String label, JLabel value) {
        JPanel tile = new JPanel();
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBackground(BG_TILE);
        tile.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 4, 8, 4)
        ));

        value.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lbl.setForeground(new Color(90, 90, 120));
        lbl.setAlignmentX(CENTER_ALIGNMENT);

        tile.add(value);
        tile.add(Box.createVerticalStrut(2));
        tile.add(lbl);
        return tile;
    }

    private JLabel bigStatLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    // ── Legend ─────────────────────────────────────────────────────────────

    private JPanel buildLegendPanel() {
        visitedLegendSwatch = new JPanel();

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 5));
        grid.setBackground(BG_CARD);
        grid.setMaximumSize(new Dimension(216, 72));
        grid.setAlignmentX(LEFT_ALIGNMENT);

        grid.add(legendItem(new Color(245, 245, 250), "Empty",   null));
        grid.add(legendItem(new Color(15,  15,  20),  "Wall",    null));
        grid.add(legendItem(C_START,                  "Start",   null));
        grid.add(legendItem(C_END,                    "End",     null));
        grid.add(legendItem(COLOR_BFS,                "Visited", visitedLegendSwatch));
        grid.add(legendItem(new Color(250, 204, 21),  "Path",    null));

        return grid;
    }

    private JPanel legendItem(Color color, String label, JPanel swatchRef) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        row.setBackground(BG_CARD);

        JPanel swatch = (swatchRef != null) ? swatchRef : new JPanel();
        swatch.setPreferredSize(new Dimension(11, 11));
        swatch.setBackground(color);
        swatch.setBorder(BorderFactory.createLineBorder(color.brighter(), 1));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lbl.setForeground(new Color(180, 180, 200));

        row.add(swatch);
        row.add(lbl);
        return row;
    }

    // ── Event wiring ───────────────────────────────────────────────────────

    private void wireActions() {
        // Algorithm toggle buttons
        bfsBtn.addActionListener(e -> selectAlgo("BFS"));
        dfsBtn.addActionListener(e -> selectAlgo("DFS"));
        astarBtn.addActionListener(e -> selectAlgo("A*"));

        // Speed presets
        slowPreset.addActionListener(e -> { speedSlider.setValue(250); highlightPreset(slowPreset); });
        medPreset.addActionListener(e  -> { speedSlider.setValue(100); highlightPreset(medPreset);  });
        fastPreset.addActionListener(e -> { speedSlider.setValue(20);  highlightPreset(fastPreset); });

        // Grid setup
        setStartBtn.addActionListener(e -> {
            mazePanel.setSettingStart(true);
            activateBtn(setStartBtn, C_START);
            setStatus("Click a cell — Start", C_START);
        });
        setEndBtn.addActionListener(e -> {
            mazePanel.setSettingEnd(true);
            activateBtn(setEndBtn, C_END);
            setStatus("Click a cell — End", C_END);
        });

        mazePanel.setOnStartSet(() -> { deactivateBtn(setStartBtn, C_START); setStatus("Start placed", C_START); });
        mazePanel.setOnEndSet(()   -> { deactivateBtn(setEndBtn,   C_END);   setStatus("End placed",   C_END);   });

        generateBtn.addActionListener(e -> generateMaze());

        // Actions
        solveBtn.addActionListener(e -> runSolver());

        resetBtn.addActionListener(e -> {
            if (animator.isRunning()) { animator.stop(); setSolvingMode(false); }
            mazePanel.getGrid().softReset();
            restoreStartEnd();
            clearStats();
            setStatus("Ready", new Color(74, 222, 128));
            mazePanel.repaint();
        });

        clearBtn.addActionListener(e -> {
            if (animator.isRunning()) { animator.stop(); setSolvingMode(false); }
            mazePanel.getGrid().hardReset();
            mazePanel.setStartCell(null);
            mazePanel.setEndCell(null);
            clearStats();
            setStatus("Ready", new Color(74, 222, 128));
            mazePanel.repaint();
        });
    }

    // ── Solver ─────────────────────────────────────────────────────────────

    private void runSolver() {
        if (animator.isRunning()) return;

        Cell start = mazePanel.getStartCell();
        Cell end   = mazePanel.getEndCell();

        if (start == null || end == null) {
            setStatus("Set Start & End!", Color.ORANGE);
            return;
        }

        Color visitedColor = algoColor();
        mazePanel.setVisitedColor(visitedColor);
        if (visitedLegendSwatch != null) visitedLegendSwatch.setBackground(visitedColor);

        mazePanel.getGrid().softReset();
        restoreStartEnd();
        mazePanel.repaint();

        Solver solver;
        switch (selectedAlgo) {
            case "DFS": solver = new DFS();   break;
            case "A*":  solver = new AStar(); break;
            default:    solver = new BFS();
        }

        SolverResult result = solver.solve(mazePanel.getGrid().getCells(), start, end);

        setStatus("Solving…", Color.CYAN);
        clearStats();
        setSolvingMode(true);

        final int[] n = {0};
        animator.animate(
            result,
            speedSlider.getValue(),
            () -> exploredVal.setText(String.valueOf(++n[0])),
            () -> {
                setSolvingMode(false);
                if (result.pathFound()) {
                    int len = Math.max(0, result.path.size() - 2);
                    pathVal.setText(String.valueOf(len));
                    setStatus("Found!", new Color(74, 222, 128));
                } else {
                    setStatus("None", new Color(239, 68, 68));
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
        for (int r = 0; r < grid.getRows(); r++)
            for (int c = 0; c < grid.getCols(); c++)
                if (rand.nextFloat() < 0.30f) cells[r][c].type = CellType.WALL;

        Cell s = cells[0][0];
        Cell e = cells[grid.getRows() - 1][grid.getCols() - 1];
        s.type = CellType.START;
        e.type = CellType.END;
        mazePanel.setStartCell(s);
        mazePanel.setEndCell(e);

        clearStats();
        setStatus("Generated!", C_GENERATE);
        mazePanel.repaint();
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void selectAlgo(String algo) {
        selectedAlgo = algo;
        markAlgoUnselected(bfsBtn,   COLOR_BFS);
        markAlgoUnselected(dfsBtn,   COLOR_DFS);
        markAlgoUnselected(astarBtn, COLOR_ASTAR);

        switch (algo) {
            case "BFS": markAlgoSelected(bfsBtn,   COLOR_BFS);   break;
            case "DFS": markAlgoSelected(dfsBtn,   COLOR_DFS);   break;
            case "A*":  markAlgoSelected(astarBtn, COLOR_ASTAR); break;
        }

        Color c = algoColor();
        mazePanel.setVisitedColor(c);
        if (visitedLegendSwatch != null) visitedLegendSwatch.setBackground(c);
    }

    private Color algoColor() {
        switch (selectedAlgo) {
            case "DFS": return COLOR_DFS;
            case "A*":  return COLOR_ASTAR;
            default:    return COLOR_BFS;
        }
    }

    private void highlightPreset(JButton active) {
        for (JButton b : new JButton[]{slowPreset, medPreset, fastPreset}) {
            b.setBackground(b == active ? ACCENT : BG_TILE);
            b.setForeground(b == active ? Color.WHITE : new Color(180, 180, 210));
        }
    }

    private void activateBtn(JButton btn, Color color) {
        int r = blend(color.getRed(),   BG_TILE.getRed(),   0.4f);
        int g = blend(color.getGreen(), BG_TILE.getGreen(), 0.4f);
        int b = blend(color.getBlue(),  BG_TILE.getBlue(),  0.4f);
        btn.setBackground(new Color(r, g, b));
        // Change border colour to white — thickness stays 2px so size never changes
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    }

    private void deactivateBtn(JButton btn, Color color) {
        btn.setBackground(color);
        // Border stays 2px thick but matches the button bg — visually invisible
        btn.setBorder(BorderFactory.createLineBorder(color, 2));
    }

    private void restoreStartEnd() {
        Cell s = mazePanel.getStartCell();
        Cell e = mazePanel.getEndCell();
        if (s != null) s.type = CellType.START;
        if (e != null) e.type = CellType.END;
    }

    private void clearStats() {
        exploredVal.setText("0");
        pathVal.setText("0");
    }

    /**
     * Locks or unlocks all controls that must not change during an animation.
     * Reset and Clear All remain enabled so the user can always stop a run.
     */
    private void setSolvingMode(boolean solving) {
        // Algorithm toggle
        bfsBtn.setEnabled(!solving);
        dfsBtn.setEnabled(!solving);
        astarBtn.setEnabled(!solving);

        // Speed
        slowPreset.setEnabled(!solving);
        medPreset.setEnabled(!solving);
        fastPreset.setEnabled(!solving);
        speedSlider.setEnabled(!solving);

        // Grid setup
        setStartBtn.setEnabled(!solving);
        setEndBtn.setEnabled(!solving);
        generateBtn.setEnabled(!solving);

        // Solve button shows different label while busy
        solveBtn.setEnabled(!solving);
        solveBtn.setText(solving ? "⏳  Solving…" : "▶   SOLVE");

        // Dim the algo buttons visually when locked
        float alpha = solving ? 0.4f : 1.0f;
        dimBtn(bfsBtn,   COLOR_BFS,   solving);
        dimBtn(dfsBtn,   COLOR_DFS,   solving);
        dimBtn(astarBtn, COLOR_ASTAR, solving);
    }

    /** Reduces a button's visual prominence when disabled during solving. */
    private void dimBtn(JButton btn, Color baseColor, boolean dim) {
        if (dim) {
            btn.setForeground(new Color(
                blend(baseColor.getRed(),   BG_TILE.getRed(),   0.3f),
                blend(baseColor.getGreen(), BG_TILE.getGreen(), 0.3f),
                blend(baseColor.getBlue(),  BG_TILE.getBlue(),  0.3f)
            ));
        } else {
            // restore: re-apply selected/unselected appearance
            selectAlgo(selectedAlgo);
        }
    }

    private void setStatus(String text, Color color) {
        statusVal.setText(text);
        statusVal.setForeground(color);
    }

    /** Linear blend: amount=1.0 → full fg, amount=0.0 → full bg */
    private int blend(int fg, int bg, float amount) {
        return Math.round(fg * amount + bg * (1f - amount));
    }

    // ── Widget factories ───────────────────────────────────────────────────

    /**
     * Wraps content in a dark card with a section title label above.
     */
    private JPanel card(String title, JPanel content) {
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(BG_SIDEBAR);
        wrapper.setBorder(new EmptyBorder(0, 12, 0, 12));
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(248, Integer.MAX_VALUE));

        // Section title
        JLabel title_lbl = new JLabel(title.toUpperCase());
        title_lbl.setFont(new Font("SansSerif", Font.BOLD, 9));
        title_lbl.setForeground(new Color(90, 90, 120));
        title_lbl.setBorder(new EmptyBorder(0, 2, 5, 0));
        title_lbl.setAlignmentX(LEFT_ALIGNMENT);

        // Card body
        content.setBackground(BG_CARD);
        content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        content.setAlignmentX(LEFT_ALIGNMENT);
        content.setMaximumSize(new Dimension(224, Integer.MAX_VALUE));

        wrapper.add(title_lbl);
        wrapper.add(content);
        return wrapper;
    }

    private JButton wideBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        // Always paint a 2px border matching the bg so the size is fixed.
        // activateBtn/deactivateBtn change only the colour, never the thickness.
        btn.setBorder(BorderFactory.createLineBorder(bg, 2));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(216, 36));
        btn.setPreferredSize(new Dimension(216, 36));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton iconBtn(String text, Color bg) {
        JButton btn = wideBtn(text, bg);
        btn.setMaximumSize(new Dimension(105, 34));
        btn.setPreferredSize(new Dimension(105, 34));
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        return btn;
    }

    private JLabel microLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lbl.setForeground(new Color(90, 90, 120));
        return lbl;
    }

    private void gap(int px) {
        add(Box.createVerticalStrut(px));
    }
}
