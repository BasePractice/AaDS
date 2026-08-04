package ru.mifi.practice.voln.heroes.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;

import static ru.mifi.practice.voln.heroes.Constants.CELL_SIZE;
import static ru.mifi.practice.voln.heroes.Constants.COLS;
import static ru.mifi.practice.voln.heroes.Constants.ROWS;

/**
 * Рисованный выбор: пункты лежат на том же холсте и в той же палитре, что и поле боя, поэтому
 * игра начинается там же, где идёт, а не в диалоге поверх окна.
 *
 * <p>Пункт не знает, что он запускает: у него есть имя и действие, — и меню одинаково годится и
 * для выбора режима, и для чего угодно ещё.
 */
final class Menu extends JPanel {
    private static final int WIDTH = COLS * CELL_SIZE + 250;
    private static final int HEIGHT = ROWS * CELL_SIZE + 50;
    private static final int ITEM_WIDTH = 360;
    private static final int ITEM_HEIGHT = 54;
    private static final int ITEM_GAP = 18;
    private static final int ITEMS_TOP = 260;
    private static final int TITLE_Y = 150;
    private static final int SUBTITLE_Y = 200;
    private static final int TITLE_FONT_SIZE = 48;
    private static final int SUBTITLE_FONT_SIZE = 16;
    private static final int ITEM_FONT_SIZE = 20;

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color TITLE_COLOR = new Color(107, 166, 89);
    private static final Color ITEM_COLOR = new Color(100, 200, 100);
    private static final Color HOVER_COLOR = new Color(230, 100, 100);

    private final transient List<Screen.Choice> choices;
    private int hovered = -1;

    Menu(List<Screen.Choice> choices) {
        this.choices = List.copyOf(choices);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(BG_COLOR);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                choose(at(e.getX(), e.getY()));
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hover(at(e.getX(), e.getY()));
            }
        });
    }

    private void choose(int item) {
        if (item >= 0) {
            choices.get(item).action().run();
        }
    }

    private void hover(int item) {
        if (item != hovered) {
            hovered = item;
            repaint();
        }
    }

    /** Пункт под курсором или -1, если курсор мимо. */
    private int at(int x, int y) {
        if (x < left() || x > left() + ITEM_WIDTH) {
            return -1;
        }
        for (int i = 0; i < choices.size(); i++) {
            int top = top(i);
            if (y >= top && y <= top + ITEM_HEIGHT) {
                return i;
            }
        }
        return -1;
    }

    private int left() {
        return (WIDTH - ITEM_WIDTH) / 2;
    }

    private int top(int item) {
        return ITEMS_TOP + item * (ITEM_HEIGHT + ITEM_GAP);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawTitle(g2);
        for (int i = 0; i < choices.size(); i++) {
            drawChoice(g2, i);
        }
        g2.dispose();
    }

    private void drawTitle(Graphics2D g2) {
        g2.setColor(TITLE_COLOR);
        g2.setFont(new Font("Fira Code", Font.BOLD, TITLE_FONT_SIZE));
        centered(g2, "ГЕРОИ", TITLE_Y);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Fira Code", Font.PLAIN, SUBTITLE_FONT_SIZE));
        centered(g2, "Выберите режим боя", SUBTITLE_Y);
    }

    private void drawChoice(Graphics2D g2, int item) {
        g2.setColor(item == hovered ? HOVER_COLOR : ITEM_COLOR);
        g2.fillRect(left(), top(item), ITEM_WIDTH, ITEM_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Fira Code", Font.BOLD, ITEM_FONT_SIZE));
        FontMetrics fm = g2.getFontMetrics();
        centered(g2, choices.get(item).name(), top(item) + (ITEM_HEIGHT + fm.getAscent()) / 2);
    }

    private void centered(Graphics2D g2, String text, int y) {
        g2.drawString(text, (WIDTH - g2.getFontMetrics().stringWidth(text)) / 2, y);
    }
}
