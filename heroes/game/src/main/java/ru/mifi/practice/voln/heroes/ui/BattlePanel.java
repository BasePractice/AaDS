package ru.mifi.practice.voln.heroes.ui;

import ru.mifi.practice.voln.heroes.Battle;
import ru.mifi.practice.voln.heroes.BattleMap;
import ru.mifi.practice.voln.heroes.Tactics;
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
    private static final int BOLT_SIZE = 12;
    private static final int FLASH_DELAY = 70;
    private static final int FLASH_TIMES = 6;
    private static final int TURN_INFO_Y_OFFSET = 10;
    private static final int TURN_INFO_X_OFFSET = 10;
    private static final int TURN_INFO_FONT_SIZE = 14;
    private static final int OUTCOME_FONT_SIZE = 30;
    private static final int LOG_X_OFFSET = 10;
    private static final int LOG_Y_START = 20;
    private static final int LOG_LINE_HEIGHT = 20;
    private static final int LOG_FONT_SIZE = 10;

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color GRID_COLOR = new Color(107, 166, 89);
    private static final Color ACTIVE_HIGHLIGHT_COLOR = new Color(255, 255, 0, 100);
    private static final Color MOVE_HIGHLIGHT_COLOR = new Color(0, 255, 0, 50);
    private static final Color SHOT_HIGHLIGHT_COLOR = new Color(60, 130, 255, 70);
    private static final Color PREVIEW_HIGHLIGHT_COLOR = new Color(150, 150, 150, 150);
    private static final Color OBSTACLE_COLOR = new Color(211, 124, 56, 197);
    private static final Color LEFT_UNIT_COLOR = new Color(100, 200, 100);
    private static final Color RIGHT_UNIT_COLOR = new Color(230, 100, 100);
    private static final Color BOLT_COLOR = new Color(40, 40, 40);
    private static final Color FLASH_COLOR = new Color(255, 255, 255);
    private static final Color SHADE_COLOR = new Color(0, 0, 0, 130);

    private final transient Battle battle;
    private final BattleMap map;
    private final List<String> logs = new ArrayList<>();
    private final Timer animTimer;
    private final Timer boltTimer;
    private final Timer flashTimer;
    private transient BufferedImage buffer;
    private List<int[]> animPath;
    private int animStep;
    private double animProgress;
    private List<int[]> boltLine;
    private double boltProgress;
    private int[] flashCell;
    private int flashLeft;
    private int previewR = -1;
    private int previewC = -1;
    private List<int[]> previewPath;

    BattlePanel(BattleGui battleGui) {
        this.battle = battleGui.getBattle();
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
        boltTimer = new Timer(ANIM_DELAY, e -> {
            boltProgress += ANIM_SPEED / span();
            if (boltProgress >= 1.0) {
                ((Timer) e.getSource()).stop();
                int[] hit = boltLine.get(1);
                boltLine = null;
                map.endAction();
                flash(hit);
            }
            repaint();
        });
        flashTimer = new Timer(FLASH_DELAY, e -> {
            flashLeft--;
            if (flashLeft <= 0) {
                ((Timer) e.getSource()).stop();
                flashCell = null;
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

    /** Полёт снаряда: тем же шагом, что и пеший ход, чтобы выстрел читался глазом. */
    void startShot(List<int[]> line) {
        this.boltLine = line;
        this.boltProgress = 0;
        boltTimer.start();
    }

    /** Сколько клеток летит снаряд: на этом расстоянии растягивается его полёт. */
    private double span() {
        int[] from = boltLine.get(0);
        int[] to = boltLine.get(1);
        return Math.max(1, Math.max(Math.abs(to[0] - from[0]), Math.abs(to[1] - from[1])));
    }

    /** Мигание подстреленной цели: здоровье на клетке уже другое, и смена видна на глаз. */
    private void flash(int[] cell) {
        if (map.getStack(cell[0], cell[1]) == null) {
            return;
        }
        flashCell = cell;
        flashLeft = FLASH_TIMES;
        flashTimer.restart();
    }

    private void handleCellClick(int r, int c) {
        if (map.isAnimating() || !battle.ours()) {
            return;
        }
        Long activeId = map.getTurnQueue().peekFirst();
        int[] coord = map.getStackCoord(activeId);
        if (coord.length != 2) {
            return;
        }
        if (previewR == r && previewC == c) {
            act(coord[0], coord[1], r, c);
            clearPreview();
        } else {
            preview(map.getStackById(activeId), coord, r, c);
        }
        repaint();
    }

    /** Второй клик по клетке: выстрел, если стрела долетает, иначе подход с ударом или ход. */
    private void act(int activeRow, int activeColumn, int r, int c) {
        Unit.Stack target = map.getStack(r, c);
        if (target != null && map.isLeft(r, c) != map.isLeftTurn()) {
            battle.apply(new Tactics.Decision(kind(activeRow, activeColumn, r, c), r, c));
        } else if (target == null && !map.isObstacle(r, c)) {
            battle.apply(new Tactics.Decision(Tactics.Kind.MOVE, r, c));
        }
    }

    private Tactics.Kind kind(int activeRow, int activeColumn, int r, int c) {
        if (map.shot(activeRow, activeColumn, r, c) > 0) {
            return Tactics.Kind.SHOOT;
        }
        return Tactics.Kind.ATTACK;
    }

    /** Первый клик по клетке: показать, чем он обернётся — путём подхода или линией выстрела. */
    private void preview(Unit.Stack active, int[] coord, int r, int c) {
        Unit.Stack target = map.getStack(r, c);
        if (active == null || target != null && map.isLeft(r, c) == map.isLeftTurn()) {
            clearPreview();
            return;
        }
        if (map.shot(coord[0], coord[1], r, c) > 0) {
            previewPath = List.of(new int[]{r, c});
            previewR = r;
            previewC = c;
            return;
        }
        previewPath = map.getPath(coord[0], coord[1], r, c, active.getType() == Unit.Type.FLYER);
        if (!previewPath.isEmpty() && (target != null || previewPath.size() - 1 <= active.speed())) {
            previewR = r;
            previewC = c;
        } else {
            clearPreview();
        }
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
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS || map.getStack(r, c) == null) {
            return null;
        }
        Unit.Stack stack = map.getStack(r, c);
        return String.format(
            "<html><b>%s</b><br>Бойцов: %d<br>Удар: %d<br>Здоровье: %d<br>Скорость: %d%s%s%s</html>",
            stack.getType().getName(), stack.size(), stack.maximumMelee(), stack.totalHealth(), stack.speed(),
            range(stack), aimed(r, c), stack.hasActed() ? "<br><i>(Уже ходил)</i>" : "");
    }

    private String range(Unit.Stack stack) {
        if (stack.getType().getRange() <= 0) {
            return "";
        }
        return "<br>Дальность: " + stack.getType().getRange();
    }

    /** Во что обойдётся дальность: сила выстрела по этой клетке видна до самого выстрела. */
    private String aimed(int r, int c) {
        int[] coord = map.getStackCoord(map.getTurnQueue().peekFirst());
        if (coord.length != 2 || map.shot(coord[0], coord[1], r, c) <= 0) {
            return "";
        }
        return "<br><b>Выстрел: " + map.shot(coord[0], coord[1], r, c) + "</b>";
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
        drawBolt(g2);
        drawOutcome(g2);
        drawTurnInfo(g2);
        drawLogs(g2);
        g2.dispose();
        g.drawImage(buffer, 0, 0, null);
    }

    private void drawHighlight(Graphics2D g2) {
        int[] coord = map.getStackCoord(map.getTurnQueue().peekFirst());
        if (coord.length == 2) {
            drawReach(g2, coord[0], coord[1]);
        }
        if (previewPath != null) {
            g2.setColor(PREVIEW_HIGHLIGHT_COLOR);
            for (int[] p : previewPath) {
                g2.fillRect(p[1] * CELL_SIZE, p[0] * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    /** Куда активный отряд дотягивается: шагом — зелёным, выстрелом — синим. */
    private void drawReach(Graphics2D g2, int ar, int ac) {
        Unit.Stack stack = map.getStack(ar, ac);
        if (stack == null) {
            return;
        }
        g2.setColor(ACTIVE_HIGHLIGHT_COLOR);
        g2.fillRect(ac * CELL_SIZE, ar * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        int[][] dists = map.getDistances(ar, ac, stack.getType() == Unit.Type.FLYER);
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (dists[r][c] <= stack.speed() && (r != ar || c != ac)) {
                    g2.setColor(MOVE_HIGHLIGHT_COLOR);
                    g2.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else if (map.shot(ar, ac, r, c) > 0) {
                    g2.setColor(SHOT_HIGHLIGHT_COLOR);
                    g2.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
            }
        }
    }

    private void drawTurnInfo(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Fira Code", Font.BOLD, TURN_INFO_FONT_SIZE));
        g2.drawString(state(), TURN_INFO_X_OFFSET, HEIGHT - TURN_INFO_Y_OFFSET);
    }

    /** Что стоит под полем: чей ход, а после последнего отряда — чья победа. */
    private String state() {
        if (map.outcome() == BattleMap.Outcome.NONE) {
            return "Ход: " + (map.isLeftTurn() ? "ЛЕВЫЕ (Зеленые)" : "ПРАВЫЕ (Красные)");
        }
        return "Бой окончен, победа: " + map.outcome().getName();
    }

    /** Победа объявляется прямо на поле: войска остались только у одной стороны. */
    private void drawOutcome(Graphics2D g2) {
        if (map.outcome() == BattleMap.Outcome.NONE) {
            return;
        }
        g2.setColor(SHADE_COLOR);
        g2.fillRect(0, 0, GRID_WIDTH, ROWS * CELL_SIZE);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Fira Code", Font.BOLD, OUTCOME_FONT_SIZE));
        String text = "ПОБЕДА: " + map.outcome().getName();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (GRID_WIDTH - fm.stringWidth(text)) / 2, ROWS * CELL_SIZE / 2);
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
            drawStackAt(g2, stack, (int) x, (int) y, color(target[0], target[1]));
        }
    }

    /** Снаряд в полёте: от стрелка к цели по прямой, поверх поля. */
    private void drawBolt(Graphics2D g2) {
        if (boltLine == null) {
            return;
        }
        int[] from = boltLine.get(0);
        int[] to = boltLine.get(1);
        double x = (from[1] + (to[1] - from[1]) * boltProgress) * CELL_SIZE + CELL_SIZE / 2.0;
        double y = (from[0] + (to[0] - from[0]) * boltProgress) * CELL_SIZE + CELL_SIZE / 2.0;
        g2.setColor(BOLT_COLOR);
        g2.fillOval((int) x - BOLT_SIZE / 2, (int) y - BOLT_SIZE / 2, BOLT_SIZE, BOLT_SIZE);
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
                if (stack != null && !animated(r, c)) {
                    drawStackAt(g2, stack, c * CELL_SIZE, r * CELL_SIZE, color(r, c));
                }
            }
        }
    }

    /** Отряд в движении рисуется отдельно, между клетками, а не на своей клетке. */
    private boolean animated(int r, int c) {
        if (animPath == null) {
            return false;
        }
        int[] target = animPath.get(animPath.size() - 1);
        return r == target[0] && c == target[1];
    }

    /** Цвет отряда: свой у каждой стороны, а у только что подстреленного — мигающий белый. */
    private Color color(int row, int column) {
        if (flashCell != null && flashCell[0] == row && flashCell[1] == column && flashLeft % 2 == 1) {
            return FLASH_COLOR;
        }
        return map.isLeft(row, column) ? LEFT_UNIT_COLOR : RIGHT_UNIT_COLOR;
    }

    private void drawStackAt(Graphics2D g2, Unit.Stack stack, int x, int y, Color color) {
        g2.setFont(new Font("Fira Code", Font.BOLD, FONT_SIZE));
        g2.setColor(color);
        int sx = x + OFFSET;
        int sy = y + OFFSET;
        int size = CELL_SIZE - DOUBLE_OFFSET;
        g2.fillRect(sx, sy, size, size);
        g2.setColor(Color.BLACK);
        String nStr = String.format("N: %3d", stack.size());
        String aStr = String.format("A: %3d", stack.maximumAttack());
        String hStr = String.format("H: %3d", stack.totalHealth());
        FontMetrics fm = g2.getFontMetrics();
        int lineHeight = fm.getHeight();
        int textY = sy + (size - 3 * lineHeight) / 2 + fm.getAscent();
        g2.drawString(nStr, sx + (size - fm.stringWidth(nStr)) / 2, textY);
        g2.drawString(aStr, sx + (size - fm.stringWidth(aStr)) / 2, textY + lineHeight);
        g2.drawString(hStr, sx + (size - fm.stringWidth(hStr)) / 2, textY + 2 * lineHeight);
    }

}
