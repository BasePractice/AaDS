package ru.mifi.practice.voln.heroes.ui;

import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.Unit;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static ru.mifi.practice.voln.heroes.Constants.CELL_SIZE;
import static ru.mifi.practice.voln.heroes.Constants.COLS;
import static ru.mifi.practice.voln.heroes.Constants.ROWS;

final class BattlePanel extends JPanel {
    private static final int GRID_WIDTH = COLS * CELL_SIZE;
    private static final int LOG_WIDTH = 250;
    private static final int WIDTH = GRID_WIDTH + LOG_WIDTH;
    private static final int HEIGHT = ROWS * CELL_SIZE + 50;
    private static final int FONT_SIZE = 10;
    private static final int OFFSET = 5;
    private static final int DOUBLE_OFFSET = 10;
    private static final int MAX_LOGS = 25;
    private static final int ANIM_DELAY = 20;
    private static final double ANIM_SPEED = 0.2;
    private static final int TURN_INFO_Y_OFFSET = 10;
    private static final int TURN_INFO_X_OFFSET = 10;
    private static final int TURN_INFO_FONT_SIZE = 14;
    private static final int LOG_X_OFFSET = 10;
    private static final int LOG_Y_START = 20;
    private static final int LOG_LINE_HEIGHT = 20;
    private static final int LOG_FONT_SIZE = 12;

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color GRID_COLOR = new Color(107, 166, 89);
    private static final Color ACTIVE_HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);
    private static final Color MOVE_HIGHLIGHT_COLOR = new Color(0, 255, 0, 50);
    private static final Color PREVIEW_HIGHLIGHT_COLOR = new Color(150, 150, 150, 150);
    private static final Color OBSTACLE_COLOR = new Color(211, 124, 56, 197);
    private static final Color LEFT_UNIT_COLOR = new Color(100, 200, 100);
    private static final Color RIGHT_UNIT_COLOR = new Color(230, 100, 100);

    private final BattleMap map;
    private final List<String> logs = new ArrayList<>();
    private final Timer animTimer;
    private transient BufferedImage buffer;
    private List<int[]> animPath;
    private int animStep;
    private double animProgress;
    private int previewR = -1;
    private int previewC = -1;
    private List<int[]> previewPath;

    BattlePanel(BattleGui battleGui) {
        this.map = battleGui.getMap();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BG_COLOR);
        ToolTipManager.sharedInstance().registerComponent(this);
        animTimer = new Timer(ANIM_DELAY, e -> {
            animProgress += ANIM_SPEED;
            if (animProgress >= 1.0) {
                animProgress = 0;
                animStep++;
                if (animStep >= animPath.size() - 1) {
                    ((Timer) e.getSource()).stop();
                    animPath = null;
                    map.endAction();
                }
            }
            repaint();
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int c = e.getX() / CELL_SIZE;
                int r = e.getY() / CELL_SIZE;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    handleCellClick(r, c);
                }
            }
        });
    }

    void addLog(String msg) {
        logs.add(msg);
        if (logs.size() > MAX_LOGS) {
            logs.remove(0);
        }
    }

    void startAnimation(List<int[]> path) {
        this.animPath = path;
        this.animStep = 0;
        this.animProgress = 0;
        animTimer.start();
    }

    private void handleCellClick(int r, int c) {
        if (map.isAnimating()) {
            return;
        }
        Long activeId = map.getTurnQueue().peekFirst();
        if (activeId == null) {
            return;
        }
        int[] activeCoord = map.getStackCoord(activeId);
        if (activeCoord == null) {
            return;
        }
        int ar = activeCoord[0];
        int ac = activeCoord[1];

        if (previewR == r && previewC == c) {
            Unit.Stack target = map.getStack(r, c);
            if (target != null && map.isLeft(r, c) != map.isLeftTurn()) {
                map.attack(ar, ac, r, c);
            } else if (target == null && !map.isObstacle(r, c)) {
                map.move(ar, ac, r, c);
            }
            clearPreview();
        } else {
            Unit.Stack target = map.getStack(r, c);
            if (target != null && map.isLeft(r, c) == map.isLeftTurn()) {
                clearPreview();
            } else {
                Unit.Stack activeStack = map.getStackById(activeId);
                if (activeStack == null) {
                    return;
                }
                previewPath = map.getPath(ar, ac, r, c, activeStack.getType() == Unit.Type.FLYER);
                if (!previewPath.isEmpty() && (target != null || previewPath.size() - 1 <= activeStack.speed())) {
                    previewR = r;
                    previewC = c;
                } else {
                    clearPreview();
                }
            }
        }
        repaint();
    }

    private void clearPreview() {
        previewR = -1;
        previewC = -1;
        previewPath = null;
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int c = event.getX() / CELL_SIZE;
        int r = event.getY() / CELL_SIZE;
        if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            Unit.Stack stack = map.getStack(r, c);
            if (stack != null) {
                return String.format("<html><b>%s</b><br>Бойцов: %d<br>Атака: %d<br>Здоровье: %d<br>Скорость: %d%s</html>",
                    stack.getType().getName(), stack.size(), stack.attack(), stack.totalHealth(), stack.speed(),
                    stack.hasActed() ? "<br><i>(Уже ходил)</i>" : "");
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2 = (Graphics2D) buffer.getGraphics();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawGrid(g2);
        drawHighlight(g2);
        drawUnits(g2);
        drawAnimatedUnit(g2);
        drawTurnInfo(g2);
        drawLogs(g2);
        g2.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void drawHighlight(Graphics2D g2) {
        Long activeId = map.getTurnQueue().peekFirst();
        if (activeId != null) {
            int[] coord = map.getStackCoord(activeId);
            if (coord != null) {
                int ar = coord[0];
                int ac = coord[1];
                Unit.Stack stack = map.getStack(ar, ac);
                if (stack != null) {
                    g2.setColor(ACTIVE_HIGHLIGHT_COLOR);
                    g2.fillRect(ac * CELL_SIZE, ar * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    int[][] dists = map.getDistances(ar, ac, stack.getType() == Unit.Type.FLYER);
                    g2.setColor(MOVE_HIGHLIGHT_COLOR);
                    for (int r = 0; r < ROWS; r++) {
                        for (int c = 0; c < COLS; c++) {
                            if (dists[r][c] <= stack.speed() && (r != ar || c != ac)) {
                                g2.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            }
                        }
                    }
                }
            }
        }
        if (previewPath != null) {
            g2.setColor(PREVIEW_HIGHLIGHT_COLOR);
            for (int[] p : previewPath) {
                g2.fillRect(p[1] * CELL_SIZE, p[0] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawTurnInfo(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Fira Code", Font.BOLD, TURN_INFO_FONT_SIZE));
        String text = "Ход: " + (map.isLeftTurn() ? "ЛЕВЫЕ (Зеленые)" : "ПРАВЫЕ (Красные)");
        g2.drawString(text, TURN_INFO_X_OFFSET, HEIGHT - TURN_INFO_Y_OFFSET);
    }

    private void drawLogs(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Fira Code", Font.PLAIN, LOG_FONT_SIZE));
        int x = GRID_WIDTH + LOG_X_OFFSET;
        int y = LOG_Y_START;
        g2.drawString("ЛОГ БОЯ:", x, y);
        y += LOG_LINE_HEIGHT;
        for (String log : logs) {
            g2.drawString(log, x, y);
            y += LOG_LINE_HEIGHT;
        }
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(GRID_COLOR);
        for (int i = 0; i <= ROWS; i++) {
            g2.drawLine(0, i * CELL_SIZE, GRID_WIDTH, i * CELL_SIZE);
        }
        for (int i = 0; i <= COLS; i++) {
            g2.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, ROWS * CELL_SIZE);
        }
    }

    private void drawAnimatedUnit(Graphics2D g2) {
        if (animPath == null || animStep >= animPath.size() - 1) {
            return;
        }
        int[] start = animPath.get(animStep);
        int[] end = animPath.get(animStep + 1);
        double x = (start[1] + (end[1] - start[1]) * animProgress) * CELL_SIZE;
        double y = (start[0] + (end[0] - start[0]) * animProgress) * CELL_SIZE;
        int[] target = animPath.get(animPath.size() - 1);
        Unit.Stack stack = map.getStack(target[0], target[1]);
        if (stack != null) {
            drawStackAt(g2, stack, (int) x, (int) y, map.isLeft(target[0], target[1]));
        }
    }

    private void drawUnits(Graphics2D g2) {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (map.isObstacle(r, c)) {
                    g2.setColor(OBSTACLE_COLOR);
                    int x = c * CELL_SIZE + OFFSET;
                    int y = r * CELL_SIZE + OFFSET;
                    int size = CELL_SIZE - DOUBLE_OFFSET;
                    g2.fillRect(x, y, size, size);
                }
                Unit.Stack stack = map.getStack(r, c);
                if (stack != null) {
                    if (animPath != null) {
                        int[] target = animPath.get(animPath.size() - 1);
                        if (r == target[0] && c == target[1]) {
                            continue;
                        }
                    }
                    drawStackAt(g2, stack, c * CELL_SIZE, r * CELL_SIZE, map.isLeft(r, c));
                }
            }
        }
    }

    private void drawStackAt(Graphics2D g2, Unit.Stack stack, int x, int y, boolean left) {
        g2.setFont(new Font("Fira Code", Font.BOLD, FONT_SIZE));
        if (left) {
            g2.setColor(LEFT_UNIT_COLOR);
        } else {
            g2.setColor(RIGHT_UNIT_COLOR);
        }
        int sx = x + OFFSET;
        int sy = y + OFFSET;
        int size = CELL_SIZE - DOUBLE_OFFSET;
        g2.fillRect(sx, sy, size, size);
        g2.setColor(Color.BLACK);
        String nStr = String.format("N: %3d", stack.size());
        String aStr = String.format("A: %3d", stack.attack());
        String hStr = String.format("H: %3d", stack.totalHealth());
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int textY = sy + (size - 3 * lineHeight) / 2 + fm.getAscent();
        g2.drawString(nStr, sx + (size - fm.stringWidth(nStr)) / 2, textY);
        g2.drawString(aStr, sx + (size - fm.stringWidth(aStr)) / 2, textY + lineHeight);
        g2.drawString(hStr, sx + (size - fm.stringWidth(hStr)) / 2, textY + 2 * lineHeight);
    }

}
