package ru.mifi.practice.voln.heroes.ui;

import ru.mifi.practice.voln.heroes.Talk;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;

/** Панель разговора с соперником: лента реплик и строка ввода. */
final class ChatPanel extends JPanel {
    private static final int WIDTH = 220;
    private static final int ROWS = 12;
    private static final int COLUMNS = 16;

    private final transient Talk talk;
    private final JTextArea heard = new JTextArea(ROWS, COLUMNS);
    private final JTextField said = new JTextField();

    ChatPanel(Talk talk) {
        this.talk = talk;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Соперник"));
        setPreferredSize(new Dimension(WIDTH, 0));
        heard.setEditable(false);
        heard.setLineWrap(true);
        heard.setWrapStyleWord(true);
        add(new JScrollPane(heard), BorderLayout.CENTER);
        JButton send = new JButton("Сказать");
        send.addActionListener(e -> say());
        said.addActionListener(e -> say());
        JPanel input = new JPanel(new BorderLayout());
        input.add(said, BorderLayout.CENTER);
        input.add(send, BorderLayout.EAST);
        add(input, BorderLayout.SOUTH);
    }

    /** Показать реплику в ленте. */
    void append(String line) {
        heard.append(line + System.lineSeparator());
        heard.setCaretPosition(heard.getDocument().getLength());
    }

    private void say() {
        String text = said.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        talk.say(text);
        append("я: " + text);
        said.setText("");
    }
}
