package ru.mifi.practice.voln.heroes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

public final class BattleGui extends JFrame {
    private static final int ROWS = 11;
    private static final int COLS = 15;
    private static final int CELL_SIZE = 50;

    private final BattleMap map;

    public BattleGui(BattleMap map) {
        this.map = map;
        setTitle("Герои");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        BattlePanel panel = new BattlePanel();
        add(panel, BorderLayout.CENTER);
        map.addPropertyChangeListener(e -> {
            if ("log".equals(e.getPropertyName())) {
                panel.addLog((String) e.getNewValue());
            } else if ("move".equals(e.getPropertyName())) {
                panel.startAnimation((List<int[]>) e.getNewValue());
            }
            panel.repaint();
        });
        pack();
        setLocationRelativeTo(null);
    }

    private final class BattlePanel extends JPanel {
        private static final int GRID_WIDTH = COLS * CELL_SIZE;
        private static final int LOG_WIDTH = 250;
        private static final int WIDTH = GRID_WIDTH + LOG_WIDTH;
        private static final int HEIGHT = ROWS * CELL_SIZE + 30;
        private static final int FONT_SIZE = 10;
        private static final int OFFSET = 5;
        private static final int DOUBLE_OFFSET = 10;
        private static final int MAX_LOGS = 12;
        private final List<String> logs = new ArrayList<>();
        private transient BufferedImage buffer;
        private final Timer animTimer;
        private List<int[]> animPath;
        private int animStep;
        private double animProgress;
        private int selR = -1;
        private int selC = -1;
        private int previewR = -1;
        private int previewC = -1;
        private List<int[]> previewPath;

        private BattlePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(245, 245, 245));
            ToolTipManager.sharedInstance().registerComponent(this);
            animTimer = new Timer(20, e -> {
                animProgress += 0.2;
                if (animProgress >= 1.0) {
                    animProgress = 0;
                    animStep++;
                    if (animStep >= animPath.size() - 1) {
                        ((Timer) e.getSource()).stop();
                        animPath = null;
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

        private void addLog(String msg) {
            logs.add(msg);
            if (logs.size() > MAX_LOGS) {
                logs.remove(0);
            }
        }

        private void startAnimation(List<int[]> path) {
            this.animPath = path;
            this.animStep = 0;
            this.animProgress = 0;
            animTimer.start();
        }

        private void handleCellClick(int r, int c) {
            if (selR == -1) {
                Unit.Stack stack = map.getStack(r, c);
                if (stack != null && map.isLeft(r, c) == map.isLeftTurn() && !stack.hasActed()) {
                    selR = r;
                    selC = c;
                    clearPreview();
                }
            } else if (selR == r && selC == c) {
                map.skip(r, c);
                selR = -1;
                selC = -1;
                clearPreview();
            } else if (previewR == r && previewC == c) {
                Unit.Stack target = map.getStack(r, c);
                if (target != null && map.isLeft(r, c) != map.isLeftTurn()) {
                    map.attack(selR, selC, r, c);
                } else if (target == null && !map.isObstacle(r, c)) {
                    map.move(selR, selC, r, c);
                }
                selR = -1;
                selC = -1;
                clearPreview();
            } else {
                Unit.Stack target = map.getStack(r, c);
                if (target != null && map.isLeft(r, c) == map.isLeftTurn()) {
                    if (!target.hasActed()) {
                        selR = r;
                        selC = c;
                    } else {
                        selR = -1;
                        selC = -1;
                    }
                    clearPreview();
                } else {
                    Unit.Stack selStack = map.getStack(selR, selC);
                    previewPath = map.getPath(selR, selC, r, c, selStack.getType() == Unit.Type.FLYER);
                    if (!previewPath.isEmpty() && (target != null || previewPath.size() - 1 <= selStack.speed())) {
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
            if (selR != -1) {
                Unit.Stack stack = map.getStack(selR, selC);
                if (stack != null) {
                    g2.setColor(new Color(255, 255, 0, 100));
                    g2.fillRect(selC * CELL_SIZE, selR * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                    int[][] dists = map.getDistances(selR, selC, stack.getType() == Unit.Type.FLYER);
                    g2.setColor(new Color(0, 255, 0, 50));
                    for (int r = 0; r < ROWS; r++) {
                        for (int c = 0; c < COLS; c++) {
                            if (dists[r][c] <= stack.speed() && (r != selR || c != selC)) {
                                g2.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                            }
                        }
                    }
                }
            }
            if (previewPath != null) {
                g2.setColor(new Color(150, 150, 150, 150));
                for (int[] p : previewPath) {
                    g2.fillRect(p[1] * CELL_SIZE, p[0] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        private void drawTurnInfo(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Fira Code", Font.BOLD, 14));
            String text = "Ход: " + (map.isLeftTurn() ? "ЛЕВЫЕ (Зеленые)" : "ПРАВЫЕ (Красные)");
            g2.drawString(text, 10, HEIGHT - 10);
        }

        private void drawLogs(Graphics2D g2) {
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Fira Code", Font.PLAIN, 12));
            int x = GRID_WIDTH + 10;
            int y = 20;
            g2.drawString("ЛОГ БОЯ:", x, y);
            y += 20;
            for (String log : logs) {
                g2.drawString(log, x, y);
                y += 20;
            }
        }

        private void drawGrid(Graphics2D g2) {
            g2.setColor(new Color(200, 200, 200));
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
                        g2.setColor(new Color(139, 69, 19));
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
                g2.setColor(new Color(100, 200, 100));
            } else {
                g2.setColor(new Color(230, 100, 100));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BattleMap map = new BattleMap();
            map.fillRandomly();
            BattleGui gui = new BattleGui(map);
            gui.setVisible(true);
        });
    }
}
