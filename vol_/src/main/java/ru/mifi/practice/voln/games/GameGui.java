package ru.mifi.practice.voln.games;

import ru.mifi.practice.voln.games.logic.GameAuto;
import ru.mifi.practice.voln.games.logic.Item;
import ru.mifi.practice.voln.games.logic.Person;
import ru.mifi.practice.voln.games.logic.Updatable;
import ru.mifi.practice.voln.games.transmit.Output;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class GameGui extends JFrame {
    private static final int REFRESH_DELAY = 100;
    private static final int MAX_MESSAGES = 5;

    private final AdventureGame game;
    private final Person.Player player;
    private final List<String> messages = new ArrayList<>();
    private final GamePanel panel;
    private final Timer timer;
    private final GameAuto autoPilot;
    private final Timer autoTimer;

    public GameGui() {
        this.player = new Person.Player("Hero");
        Output output = (format, args) -> {
            String msg = String.format(format, args).trim();
            if (!msg.isEmpty()) {
                messages.add(msg);
                if (messages.size() > MAX_MESSAGES) {
                    messages.remove(0);
                }
            }
        };
        this.game = new AdventureGame(output, player);
        this.autoPilot = new GameAuto(game, player);
        this.panel = new GamePanel();

        this.timer = new Timer(REFRESH_DELAY, e -> {
            game.idleTick();
            panel.repaint();
            if (!game.isRunning()) {
                ((Timer) e.getSource()).stop();
            }
        });

        this.autoTimer = new Timer(500, e -> {
            autoPilot.tick();
            panel.repaint();
            if (!game.isRunning()) {
                ((Timer) e.getSource()).stop();
            }
        });

        setTitle("Приключения");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        final JPanel controlPanel = new JPanel();
        JButton restartBtn = new JButton("Сброс");
        restartBtn.setFocusable(false);

        JToggleButton autoBtn = new JToggleButton("Авто");
        autoBtn.setFocusable(false);
        autoBtn.addActionListener(e -> {
            if (autoBtn.isSelected()) {
                autoTimer.start();
            } else {
                autoTimer.stop();
            }
        });

        restartBtn.addActionListener(e -> {
            game.restart();
            messages.clear();
            messages.add("Старт");
            if (!timer.isRunning()) {
                timer.start();
            }
            if (autoBtn.isSelected() && !autoTimer.isRunning()) {
                autoTimer.start();
            }
            panel.repaint();
        });
        controlPanel.add(restartBtn);
        controlPanel.add(autoBtn);

        JSlider speedSlider = new JSlider(100, 2000, 500);
        speedSlider.setFocusable(false);
        speedSlider.addChangeListener(e -> autoTimer.setDelay(speedSlider.getValue()));
        controlPanel.add(new JLabel("Скорость (ms):"));
        controlPanel.add(speedSlider);

        add(controlPanel, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKey(e.getKeyCode());
                panel.repaint();
            }
        });

        pack();
        setLocationRelativeTo(null);
        this.timer.start();
    }

    private void handleKey(int keyCode) {
        if (!game.isRunning()) {
            return;
        }
        boolean action = true;
        if (keyCode == KeyEvent.VK_RIGHT) {
            game.forward();
        } else if (keyCode == KeyEvent.VK_LEFT) {
            game.backward();
        } else if (keyCode == KeyEvent.VK_A) {
            game.attack();
        } else if (keyCode == KeyEvent.VK_C) {
            game.catchItem();
        } else if (keyCode == KeyEvent.VK_S) {
            selectNextItem();
            action = false;
        } else if (keyCode == KeyEvent.VK_U) {
            useSelectedItem();
        } else if (keyCode == KeyEvent.VK_D) {
            deleteSelectedItem();
        } else if (keyCode != KeyEvent.VK_W && keyCode != KeyEvent.VK_SPACE) {
            action = false;
        }
        if (action) {
            game.resetIdle();
            game.update();
        }
    }

    private void selectNextItem() {
        List<Item> items = game.listItems();
        if (!items.isEmpty()) {
            Item current = player.getSelectedItem();
            int currentIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).equals(current)) {
                    currentIndex = i;
                    break;
                }
            }
            game.selectItem((currentIndex + 1) % items.size());
        }
    }

    private void useSelectedItem() {
        List<Item> items = game.listItems();
        Item current = player.getSelectedItem();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(current)) {
                game.useItem(i);
                break;
            }
        }
    }

    private void deleteSelectedItem() {
        List<Item> items = game.listItems();
        Item current = player.getSelectedItem();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(current)) {
                game.removeItem(i);
                break;
            }
        }
    }

    private class GamePanel extends JPanel {
        private static final int WIDTH = 800;
        private static final int HEIGHT = 400;
        private static final int CELL_SIZE = 30;
        private transient BufferedImage buffer;

        GamePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setBackground(new Color(245, 245, 245));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight()) {
                buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D bg = (Graphics2D) buffer.getGraphics();
            bg.setColor(getBackground());
            bg.fillRect(0, 0, getWidth(), getHeight());

            double scale = Math.min((double) getWidth() / WIDTH, (double) getHeight() / HEIGHT);
            bg.translate((getWidth() - WIDTH * scale) / 2, (getHeight() - HEIGHT * scale) / 2);
            bg.scale(scale, scale);

            drawGame(bg);
            bg.dispose();
            g.drawImage(buffer, 0, 0, null);
        }

        private void drawGame(Graphics g) {
            int cells = game.getLineLength();
            int cellWidth = WIDTH / cells;
            int cellHeight = cellWidth;
            int yPos = (HEIGHT - cellHeight) / 2;

            g.setFont(new Font("Fira Code", Font.BOLD, 12));
            for (int i = 0; i < cells; i++) {
                Updatable.View view = game.viewAt(i);
                int xPos = i * cellWidth;

                g.setColor(new Color(200, 200, 200));
                g.drawRect(xPos, yPos, cellWidth - 2, cellHeight);

                int size = Math.min(cellWidth - 6, cellHeight - 4);
                int xOffset = (cellWidth - size) / 2;
                int yOffset = (cellHeight - size) / 2;

                if (view.type() == Updatable.Type.PLAYER) {
                    g.setColor(new Color(100, 200, 100));
                    g.fillRect(xPos + xOffset, yPos + yOffset, size, size);
                    g.setColor(Color.BLACK);
                    drawCenteredString(g, "H:" + player.health(), xPos + xOffset, yPos + yOffset, size, size);
                } else if (view.type() == Updatable.Type.ENEMY) {
                    g.setColor(new Color(230, 100, 100));
                    g.fillRect(xPos + xOffset, yPos + yOffset, size, size);
                    Person mob = (Person) view.element();
                    g.setColor(Color.BLACK);
                    drawCenteredString(g, "H:" + mob.health(), xPos + xOffset, yPos + yOffset, size, size);
                } else if (view.type() == Updatable.Type.ITEM) {
                    g.setColor(new Color(230, 230, 100));
                    g.fillRect(xPos + xOffset, yPos + yOffset, size, size);
                    g.setColor(Color.BLACK);
                    drawCenteredString(g, view.element().toString(), xPos + xOffset, yPos + yOffset, size, size);
                }
            }

            g.setColor(new Color(50, 50, 50));
            g.drawString("A: атака, C: захват, S: выбор, U: использовать, D: удалить, Space: пропустить", 10, 20);
            g.drawString("Уровень: " + game.getLevel() + " Жизнь: " + player.health()
                + " Атака: +" + player.getBaseAttack()
                + " Шаги: " + game.getSteps() + " Тики: " + game.getIdleTicks(), 10, 40);
            g.drawString("Инвентарь: " + player.items(), 10, HEIGHT - 20);

            g.setColor(new Color(180, 180, 180));
            g.drawRect(WIDTH - 160, HEIGHT - 100, 150, 60);
            g.setColor(new Color(50, 50, 50));
            g.drawString("Текущий:", WIDTH - 150, HEIGHT - 80);
            g.drawString(player.getSelectedItem().toString(), WIDTH - 150, HEIGHT - 60);

            int msgY = 60;
            for (String msg : messages) {
                g.drawString(msg, WIDTH - 300, msgY);
                msgY += 20;
            }

            if (!game.isRunning()) {
                g.setColor(new Color(150, 0, 0));
                g.drawString("Игра завершена", WIDTH / 2 - 30, HEIGHT / 2 + CELL_SIZE * 3);
            }
        }

        private void drawCenteredString(Graphics g, String text, int x, int y, int width, int height) {
            Font oldFont = g.getFont();
            FontMetrics metrics = g.getFontMetrics(oldFont);
            int textWidth = metrics.stringWidth(text);
            if (textWidth > width - 2 && width > 2) {
                float newSize = oldFont.getSize2D() * (width - 2) / textWidth;
                g.setFont(oldFont.deriveFont(Math.max(1.0f, newSize)));
                metrics = g.getFontMetrics();
            }
            int tx = x + (width - metrics.stringWidth(text)) / 2;
            int ty = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
            g.drawString(text, tx, ty);
            g.setFont(oldFont);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameGui gui = new GameGui();
            gui.setVisible(true);
        });
    }
}
