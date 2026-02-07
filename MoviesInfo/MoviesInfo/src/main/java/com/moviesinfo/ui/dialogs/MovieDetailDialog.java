package com.moviesinfo.ui.dialogs;

import javax.swing.*;
import java.awt.*;

public class MovieDetailDialog extends JDialog {
    public MovieDetailDialog(JFrame parent, Object[] movieData) {
        super(parent, "Movie Details", true);
        setSize(600, 520); // Increased size for more content
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Proper close behavior
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(46,204,113), 2, true));
        // ESC key closes dialog
        getRootPane().registerKeyboardAction(e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        setLayout(new BorderLayout());

        // Header Bar
        JPanel header = new JPanel();
        header.setBackground(new Color(46, 204, 113));
        header.setPreferredSize(new Dimension(600, 50));
        JLabel titleLabel = new JLabel(String.valueOf(movieData[1]));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        header.setLayout(new BorderLayout());
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220,220,220), 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18))); // Reduced padding
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Info Grid
        JPanel infoGrid = new JPanel(new GridLayout(0,2,10,10));
        infoGrid.setOpaque(false);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font valueFont = new Font("Segoe UI", Font.PLAIN, 13);
        infoGrid.add(createLabel("IMDB Score:", labelFont));
        infoGrid.add(createValue(String.valueOf(movieData[2]), valueFont));
        infoGrid.add(createLabel("Genre:", labelFont));
        infoGrid.add(createValue(String.valueOf(movieData[3]), valueFont));
        infoGrid.add(createLabel("Year:", labelFont));
        infoGrid.add(createValue(String.valueOf(movieData[4]), valueFont));
        infoGrid.add(createLabel("Director:", labelFont));
        infoGrid.add(createValue(String.valueOf(movieData[5]), valueFont));
        infoGrid.add(createLabel("Actors:", labelFont));
        infoGrid.add(createValue(String.valueOf(movieData[6]), valueFont));
        contentPanel.add(infoGrid);
        contentPanel.add(Box.createVerticalStrut(8)); // Reduced spacing

        // Review and Comments
        JTextArea reviewArea = createArea("Review:", String.valueOf(movieData[7]));
        JTextArea commentsArea = createArea("Comments:", String.valueOf(movieData[8]));
        JTextArea durationArea = createArea("Duration:", String.valueOf(movieData[9]));
        contentPanel.add(reviewArea);
        contentPanel.add(Box.createVerticalStrut(4)); // Reduced spacing
        contentPanel.add(commentsArea);
        contentPanel.add(Box.createVerticalStrut(4)); // Reduced spacing
        contentPanel.add(durationArea);
        contentPanel.add(Box.createVerticalStrut(20)); // Reduced spacing

        // Shadow effect (optional, for modern look)
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,3,0,new Color(46,204,113,70)),
            contentPanel.getBorder()));

        // Make content scrollable so all lines are visible
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Modern Close Button (bottom, prominent color)
        JButton closeButton = new JButton("Close");
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(true);
        closeButton.setBackground(Color.ORANGE);
        closeButton.setForeground(Color.BLACK); // Make text black for readability
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        closeButton.setPreferredSize(new Dimension(140, 44));
        closeButton.setBorder(BorderFactory.createLineBorder(new Color(200,120,0), 2, true));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setToolTipText("Close this window");
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(60, 60, 60));
        return label;
    }
    private JLabel createValue(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(new Color(40, 120, 80));
        return label;
    }
    private JTextArea createArea(String title, String value) {
        JTextArea area = new JTextArea(title + "\n" + value);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setEditable(false);
        area.setOpaque(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(6,0,6,0));
        return area;
    }
}

