package com.moviesinfo.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.moviesinfo.database.DatabaseManager;
import java.sql.PreparedStatement;

public class AddMovieDialog extends JDialog {
    private JTextField nameField, directorField, actorsField;
    private JTextArea reviewArea, commentsArea;
    private JComboBox<String> yearBox;
    private JTextField runningTimeField;
    private JPanel genresPanel;
    private JSpinner ratingSpinner;
    private DatabaseManager dbManager;
    private Runnable onMovieAdded;

    public AddMovieDialog(Window owner, DatabaseManager dbManager, Runnable onMovieAdded) {
        super(owner, "Add movie", ModalityType.APPLICATION_MODAL);
        this.dbManager = dbManager;
        this.onMovieAdded = onMovieAdded;
        setLayout(new BorderLayout(10,10));
        JPanel shadowPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(0,0,0,32));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 32, 32);
                // Panel background
                g2.setColor(new Color(252,252,255,245));
                g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 22, 22);
                g2.dispose();
            }
        };
        shadowPanel.setOpaque(false);
        shadowPanel.setLayout(new BorderLayout());
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Name"), c);
        c.gridx = 1; nameField = new JTextField(14); nameField.setPreferredSize(new Dimension(140, 22)); form.add(nameField, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Genre(s)"), c);
        c.gridx = 1; c.gridwidth = 2;
        genresPanel = new JPanel(new GridLayout(8, 3));
        String[] genres = {"Action","Adventure","Anime","Biography","Comedy","Crime","Documentary","Drama","Family","Fantasy","Film-Noir","Game-Show","History","Horror","Music","Musical","Mystery","News","Reality-TV","Romance","Sci-Fi","Short","Talk-Show","Thriller","War","Western"};
        for (String g : genres) genresPanel.add(new JCheckBox(g));
        form.add(genresPanel, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Director"), c);
        c.gridx = 1; c.gridwidth = 2; directorField = new JTextField(14); directorField.setPreferredSize(new Dimension(140, 22)); form.add(directorField, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Actors"), c);
        c.gridx = 1; c.gridwidth = 2; actorsField = new JTextField(14); actorsField.setPreferredSize(new Dimension(140, 22)); form.add(actorsField, c); c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Review"), c);
        c.gridx = 1; c.gridwidth = 2; reviewArea = new JTextArea(2, 14); reviewArea.setLineWrap(true); reviewArea.setWrapStyleWord(true); form.add(new JScrollPane(reviewArea), c); c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("IMDB Score"), c);
        c.gridx = 1; ratingSpinner = new JSpinner(new SpinnerNumberModel(5.0, 0.0, 10.0, 0.1));
        ((JSpinner.DefaultEditor)ratingSpinner.getEditor()).getTextField().setColumns(3);
        form.add(ratingSpinner, c);
        c.gridx = 2; form.add(new JLabel("/ 10"), c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Your comments"), c);
        c.gridx = 1; c.gridwidth = 2; commentsArea = new JTextArea(2, 14); commentsArea.setLineWrap(true); commentsArea.setWrapStyleWord(true); form.add(new JScrollPane(commentsArea), c); c.gridwidth = 1;

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Year of release"), c);
        c.gridx = 1; yearBox = new JComboBox<>();
        yearBox.addItem("Unknown");
        for (int i = 2025; i >= 1900; i--) yearBox.addItem(String.valueOf(i));
        form.add(yearBox, c);

        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Running time"), c);
        c.gridx = 1;
        runningTimeField = new JTextField(6); runningTimeField.setPreferredSize(new Dimension(60, 22));
        JPanel runningTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        runningTimePanel.add(runningTimeField);
        runningTimePanel.add(new JLabel(" minutes"));
        form.add(runningTimePanel, c);

        add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("+ Add movie");
        JButton closeBtn = new JButton("✖ Close");
        btnPanel.add(addBtn); btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(Color.DARK_GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String genresStr = "";
                for (Component comp : genresPanel.getComponents())
                    if (comp instanceof JCheckBox && ((JCheckBox)comp).isSelected())
                        genresStr += ((JCheckBox)comp).getText() + ", ";
                if (genresStr.endsWith(", ")) genresStr = genresStr.substring(0, genresStr.length()-2);
                String director = directorField.getText().trim();
                String actors = actorsField.getText().trim();
                String review = reviewArea.getText().trim();
                double imdbScore = ((Number)ratingSpinner.getValue()).doubleValue();
                String comments = commentsArea.getText().trim();
                String year = yearBox.getSelectedItem().toString();
                String runningTime = runningTimeField.getText().trim();
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(
                    "INSERT INTO movies (title, imdb_score, genre, year, director, actors, review, comments, running_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                pstmt.setString(1, name);
                pstmt.setDouble(2, imdbScore);
                pstmt.setString(3, genresStr);
                pstmt.setString(4, year.equals("Unknown") ? null : year);
                pstmt.setString(5, director);
                pstmt.setString(6, actors);
                pstmt.setString(7, review);
                pstmt.setString(8, comments);
                pstmt.setString(9, runningTime.isEmpty() ? null : runningTime + " minutes");
                pstmt.executeUpdate();
                if (onMovieAdded != null) onMovieAdded.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding movie: " + ex.getMessage());
            }
        });
        closeBtn.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(owner);
    }
}
