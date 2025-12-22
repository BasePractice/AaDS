package ru.mifi.practice.voln.heroes.ui;

import lombok.Getter;
import ru.mifi.practice.voln.heroes.BattleMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;

public final class BattleGui extends JFrame {
    @Getter
    private final BattleMap map;

    public BattleGui(BattleMap map) {
        this.map = map;
        setTitle("Герои");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        BattlePanel panel = new BattlePanel(this);
        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton waitBtn = new JButton("Ожидание");
        waitBtn.addActionListener(e -> {
            map.waitTurn();
            panel.repaint();
        });
        JButton skipBtn = new JButton("Пропуск");
        skipBtn.addActionListener(e -> {
            map.skipTurn();
            panel.repaint();
        });
        btnPanel.add(waitBtn);
        btnPanel.add(skipBtn);
        add(btnPanel, BorderLayout.SOUTH);

        map.addPropertyChangeListener(e -> {
            if ("log".equals(e.getPropertyName())) {
                panel.addLog((String) e.getNewValue());
            } else if ("move".equals(e.getPropertyName())) {
                //noinspection unchecked
                panel.startAnimation((List<int[]>) e.getNewValue());
            }
            panel.repaint();
        });
        pack();
        setLocationRelativeTo(null);
    }
}
