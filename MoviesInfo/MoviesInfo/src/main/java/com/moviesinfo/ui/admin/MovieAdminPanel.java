package com.moviesinfo.ui.admin;

import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableModel; // ADDED

import java.awt.*;
import com.moviesinfo.database.DatabaseManager;
import java.sql.*;

public class MovieAdminPanel extends JPanel {
    private DatabaseManager dbManager;
    private javax.swing.table.DefaultTableModel tableModel;
    private JLabel countLabel;
    private JPanel moviesTab;
    private JPanel topPanel;
    private JScrollPane tableScroll;
    private JTabbedPane tabbedPane;
    private JTable movieTable;

    // User Table and Model as fields for access in constructor
    private JTable userTable;
    private DefaultTableModel userTableModel;

    public MovieAdminPanel(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();

        moviesTab = new JPanel(new BorderLayout(5, 5));
        moviesTab.setBackground(new Color(245, 247, 250));

        // User Table and Model initialization
        userTableModel = new DefaultTableModel(new String[]{"Username", "Password", "Role", "First Name", "Last Name"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(userTableModel);
        // No User Favorites tab or logic in the original version.

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        JButton addButton = new JButton("+ Add movie");
        // Add Movie Dialog integration
        addButton.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(MovieAdminPanel.this);
            new com.moviesinfo.ui.dialogs.AddMovieDialog(win, dbManager, this::reloadMovieTable).setVisible(true);
        });

        JButton editButton = new JButton("# Edit movie");
        editButton.addActionListener(e -> {
            int row = movieTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie to edit.");
                return;
            }
            Object[] movieRow = new Object[9];
            for (int i = 0; i < 9; i++) movieRow[i] = tableModel.getValueAt(row, i);
            // Get the movie id from DB (title + year + director should be unique enough for demo)
            try {
                String sql = "SELECT id FROM movies WHERE title=? AND year=? AND director=?";
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
                pstmt.setString(1, movieRow[0].toString());
                pstmt.setInt(2, Integer.parseInt(movieRow[3].toString()));
                pstmt.setString(3, movieRow[4].toString());
                java.sql.ResultSet rs = pstmt.executeQuery();
                int movieId = rs.next() ? rs.getInt("id") : -1;
                if (movieId == -1) {
                    JOptionPane.showMessageDialog(this, "Could not find movie in database.");
                    return;
                }
                Window win = SwingUtilities.getWindowAncestor(MovieAdminPanel.this);
                new com.moviesinfo.ui.dialogs.EditMovieDialog(win, dbManager, movieRow, this::reloadMovieTable, movieId).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        JButton removeButton = new JButton("- Remove movie");
        removeButton.addActionListener(e -> {
            int row = movieTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a movie to remove.");
                return;
            }
            Object[] movieRow = new Object[9];
            for (int i = 0; i < 9; i++) movieRow[i] = tableModel.getValueAt(row, i);
            // Get the movie id from DB (title + year + director should be unique enough for demo)
            try {
                String sql = "SELECT id FROM movies WHERE title=? AND year=? AND director=?";
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
                pstmt.setString(1, movieRow[0].toString());
                pstmt.setInt(2, Integer.parseInt(movieRow[3].toString()));
                pstmt.setString(3, movieRow[4].toString());
                java.sql.ResultSet rs = pstmt.executeQuery();
                int movieId = rs.next() ? rs.getInt("id") : -1;
                if (movieId == -1) {
                    JOptionPane.showMessageDialog(this, "Could not find movie in database.");
                    return;
                }
                // Custom confirmation dialog with movie info
                JPanel panel = new JPanel(new BorderLayout(10,10));

                // Header with icon and warning
                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                headerPanel.setBackground(new Color(255, 230, 230));
                JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
                JLabel warnLabel = new JLabel("Are you sure you want to delete this movie?");
                JLabel imdbScoreLabel = new JLabel("IMDB Score");
                warnLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                warnLabel.setForeground(new Color(183, 28, 28));
                headerPanel.add(iconLabel);
                headerPanel.add(warnLabel);
                panel.add(headerPanel, BorderLayout.NORTH);

                // Movie info panel
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200,200,200), 1),
                    BorderFactory.createEmptyBorder(14, 18, 14, 18)));
                JLabel titleLabel = new JLabel(movieRow[0].toString());
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
                titleLabel.setForeground(new Color(33, 33, 33));
                infoPanel.add(titleLabel);
                infoPanel.add(Box.createVerticalStrut(10));
                infoPanel.add(new JLabel("IMDB Score: " + movieRow[1]));
                infoPanel.add(new JLabel("Genre: " + movieRow[2]));
                infoPanel.add(new JLabel("Year: " + movieRow[3]));
                infoPanel.add(new JLabel("Director: " + movieRow[4]));
                infoPanel.add(new JLabel("Actors: " + movieRow[5]));
                infoPanel.add(new JLabel("Review: " + movieRow[6]));
                infoPanel.add(new JLabel("Comments: " + movieRow[7]));
                infoPanel.add(new JLabel("Running time: " + movieRow[8]));
                infoPanel.setBackground(Color.WHITE);
                panel.add(infoPanel, BorderLayout.CENTER);
                int result = JOptionPane.showConfirmDialog(this, panel, "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    String delSql = "DELETE FROM movies WHERE id=?";
                    PreparedStatement delPstmt = dbManager.getConnection().prepareStatement(delSql);
                    delPstmt.setInt(1, movieId);
                    delPstmt.executeUpdate();
                    reloadMovieTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        JButton exportButton = new JButton("⇩ Export");
        exportButton.setBackground(new Color(76, 175, 80));
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportButton.addActionListener(ev -> {
            String[] options = {"CSV", "TXT", "DOC", "XLS"};
            String format = (String) JOptionPane.showInputDialog(this, "Choose export format:", "Export Table", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (format == null) return;
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save as " + format);
            chooser.setSelectedFile(new java.io.File("movies_list." + format.toLowerCase()));
            int userSelection = chooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();
                try (java.io.PrintWriter out = new java.io.PrintWriter(file, "UTF-8")) {
                    // Write header
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        out.print(tableModel.getColumnName(col));
                        if (col < tableModel.getColumnCount()-1) out.print(format.equals("CSV") ? "," : "\t");
                    }
                    out.println();
                    // Write rows
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        for (int col = 0; col < tableModel.getColumnCount(); col++) {
                            Object val = tableModel.getValueAt(row, col);
                            String s = val == null ? "" : val.toString();
                            if (format.equals("CSV")) s = s.replace("\"", "\"\"");
                            out.print(format.equals("CSV") ? '"'+s+'"' : s);
                            if (col < tableModel.getColumnCount()-1) out.print(format.equals("CSV") ? "," : "\t");
                        }
                        out.println();
                    }
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
                    return;
                }
                JOptionPane.showMessageDialog(this, "Exported successfully as " + file.getName());
            }
        });

        JButton searchButton = new JButton("» Search");
        JButton refreshButton = new JButton("* Refresh list");
        refreshButton.setVisible(false);
        refreshButton.addActionListener(ev -> {
            reloadMovieTable();
            refreshButton.setVisible(false);
        });
        refreshButton.addActionListener(ev -> {
            reloadMovieTable();
            refreshButton.setVisible(false);
        });
        searchButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Search", true);
            dialog.setLayout(new BorderLayout());
            JPanel shadowPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0,0,0,32));
                    g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 32, 32);
                    g2.setColor(new Color(252,252,255,245));
                    g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 22, 22);
                    g2.dispose();
                }
            };
            shadowPanel.setOpaque(false);
            shadowPanel.setLayout(new BorderLayout());
            JPanel form = new JPanel(new BorderLayout(0, 10));
            form.setOpaque(false);
            // Main section with titled border
            JPanel mainPanel = new JPanel();
            mainPanel.setOpaque(false);
            mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Must contain the following data in..", 0, 0, new Font("Segoe UI", Font.BOLD, 13)));
            mainPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 8, 6, 8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Title"), gbc); gbc.gridx = 1;
            JTextField titleField = new JTextField(18); mainPanel.add(titleField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            mainPanel.add(new JLabel("Director"), gbc); gbc.gridx = 1;
            JTextField directorField = new JTextField(18); mainPanel.add(directorField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            mainPanel.add(new JLabel("Actor"), gbc); gbc.gridx = 1;
            JTextField actorField = new JTextField(18); mainPanel.add(actorField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            mainPanel.add(new JLabel("Review"), gbc); gbc.gridx = 1;
            JTextField reviewField = new JTextField(18); mainPanel.add(reviewField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            mainPanel.add(new JLabel("Comments"), gbc); gbc.gridx = 1;
            JTextField commentsField = new JTextField(18); mainPanel.add(commentsField, gbc);
            form.add(mainPanel, BorderLayout.NORTH);
            // Options section
            JPanel optionsPanel = new JPanel();
            optionsPanel.setLayout(new GridBagLayout());
            GridBagConstraints ogbc = new GridBagConstraints();
            ogbc.insets = new Insets(4, 4, 4, 4);
            ogbc.anchor = GridBagConstraints.WEST;
            ogbc.gridx = 0; ogbc.gridy = 0;
            JCheckBox imdbCheck = new JCheckBox("IMDB Score");
            JComboBox<String> imdbBox = new JComboBox<>(new String[]{"1","2","3","4","5","6","7","8","9","10"});
            optionsPanel.add(imdbCheck, ogbc);
            ogbc.gridx = 1;
            optionsPanel.add(imdbBox, ogbc);
            ogbc.gridx = 0; ogbc.gridy++;
            JCheckBox yearCheck = new JCheckBox("Year of release");
            JComboBox<String> yearBox = new JComboBox<>(new String[]{"Unknown","2025","2024","2023","2022","2021","2020","2010","2000","1990","1980","1970","1960","1950","1940","1930","1920","1910","1900"});
            optionsPanel.add(yearCheck, ogbc);
            ogbc.gridx = 1;
            optionsPanel.add(yearBox, ogbc);
            ogbc.gridx = 0; ogbc.gridy++;
            JCheckBox runningTimeCheck = new JCheckBox("Running time of at least");
            JComboBox<String> runningTimeBox = new JComboBox<>(new String[]{"Unknown","30","60","90","120","150","180"});
            optionsPanel.add(runningTimeCheck, ogbc);
            ogbc.gridx = 1;
            optionsPanel.add(runningTimeBox, ogbc);
            ogbc.gridx = 0; ogbc.gridy++;
            JCheckBox genreCheck = new JCheckBox("In this/these genre(s)");
            optionsPanel.add(genreCheck, ogbc);
            ogbc.gridx = 1;
            JPanel genrePanel = new JPanel(new GridLayout(8,3,2,2));
            JCheckBox[] genreBoxes = new JCheckBox[]{
                new JCheckBox("Action"), new JCheckBox("Adventure"), new JCheckBox("Animation"), new JCheckBox("Biography"),
                new JCheckBox("Comedy"), new JCheckBox("Crime"), new JCheckBox("Documentary"), new JCheckBox("Drama"),
                new JCheckBox("Family"), new JCheckBox("Fantasy"), new JCheckBox("Film-Noir"), new JCheckBox("Game-Show"),
                new JCheckBox("History"), new JCheckBox("Horror"), new JCheckBox("Music"), new JCheckBox("Musical"),
                new JCheckBox("Mystery"), new JCheckBox("News"), new JCheckBox("Reality-TV"), new JCheckBox("Romance"),
                new JCheckBox("Sci-Fi"), new JCheckBox("Short"), new JCheckBox("Talk-Show"), new JCheckBox("Thriller"),
                new JCheckBox("War"), new JCheckBox("Western")};
            for (JCheckBox cb : genreBoxes) genrePanel.add(cb);
            optionsPanel.add(genrePanel, ogbc);
            form.add(optionsPanel, BorderLayout.CENTER);
            // Button panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
            JButton searchBtn = new JButton("Search");
            JButton closeBtn = new JButton("Close");
            searchBtn.setBackground(new Color(255,193,7));
            searchBtn.setForeground(Color.BLACK);
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            closeBtn.setBackground(new Color(80,80,80));
            closeBtn.setForeground(Color.WHITE);
            btnPanel.add(searchBtn); btnPanel.add(closeBtn);
            form.add(btnPanel, BorderLayout.SOUTH);
            shadowPanel.add(form, BorderLayout.CENTER);
            dialog.setContentPane(shadowPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            // Search logic
            searchBtn.addActionListener(ev -> {
                StringBuilder sql = new StringBuilder("SELECT title, imdb_score, genre, year, director, actors, review, comments, running_time FROM movies WHERE 1=1");
                java.util.List<Object> params = new java.util.ArrayList<>();
                if (!titleField.getText().trim().isEmpty()) {
                    sql.append(" AND title LIKE ?");
                    params.add("%"+titleField.getText().trim()+"%");
                }
                if (!directorField.getText().trim().isEmpty()) {
                    sql.append(" AND director LIKE ?");
                    params.add("%"+directorField.getText().trim()+"%");
                }
                if (!actorField.getText().trim().isEmpty()) {
                    sql.append(" AND actors LIKE ?");
                    params.add("%"+actorField.getText().trim()+"%");
                }
                if (!reviewField.getText().trim().isEmpty()) {
                    sql.append(" AND review LIKE ?");
                    params.add("%"+reviewField.getText().trim()+"%");
                }
                if (!commentsField.getText().trim().isEmpty()) {
                    sql.append(" AND comments LIKE ?");
                    params.add("%"+commentsField.getText().trim()+"%");
                }
                if (imdbCheck.isSelected()) {
                    sql.append(" AND CAST(imdb_score AS INTEGER) = ?");
                    params.add(Integer.parseInt(imdbBox.getSelectedItem().toString()));
                }
                if (yearCheck.isSelected() && !yearBox.getSelectedItem().toString().equals("Unknown")) {
                    sql.append(" AND year = ?");
                    params.add(Integer.parseInt(yearBox.getSelectedItem().toString()));
                }
                if (runningTimeCheck.isSelected() && !runningTimeBox.getSelectedItem().toString().equals("Unknown")) {
                    sql.append(" AND CAST(SUBSTR(running_time, 1, INSTR(running_time, ' ')-1) AS INTEGER) >= ?");
                    params.add(Integer.parseInt(runningTimeBox.getSelectedItem().toString()));
                }
                if (genreCheck.isSelected()) {
                    java.util.List<String> selectedGenres = new java.util.ArrayList<>();
                    for (JCheckBox cb : genreBoxes) if (cb.isSelected()) selectedGenres.add(cb.getText());
                    if (!selectedGenres.isEmpty()) {
                        for (String genre : selectedGenres) {
                            sql.append(" AND genre LIKE ?");
                            params.add("%"+genre+"%");
                        }
                    }
                }
                // Fill table
                tableModel.setRowCount(0);
                int count = 0;
                try {
                    PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql.toString());
                    for (int i = 0; i < params.size(); i++) pstmt.setObject(i+1, params.get(i));
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        tableModel.addRow(new Object[]{
                            rs.getString("title"),
                            rs.getDouble("imdb_score"),
                            rs.getString("genre"),
                            rs.getInt("year"),
                            rs.getString("director"),
                            rs.getString("actors"),
                            rs.getString("review"),
                            rs.getString("comments"),
                            rs.getString("running_time")
                        });
                        count++;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                }
                countLabel.setText(count + (count == 1 ? " movie" : " movies"));
                refreshButton.setVisible(true);
                dialog.dispose();
            });
            closeBtn.addActionListener(ev -> {
                dialog.dispose();
                reloadMovieTable();
                refreshButton.setVisible(false);
            });
            dialog.setVisible(true);
        });

        // Button styling helper
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);
        // More vibrant, modern colors
        Color addColor = new Color(76, 175, 80);      // #4CAF50
        Color editColor = new Color(33, 150, 243);    // #2196F3
        Color removeColor = new Color(244, 67, 54);   // #F44336
        Color searchColor = new Color(255, 193, 7);   // #FFC107
        Color hoverAdd = new Color(56, 142, 60);      // #388E3C
        Color hoverEdit = new Color(25, 118, 210);    // #1976D2
        Color hoverRemove = new Color(211, 47, 47);   // #D32F2F
        Color hoverSearch = new Color(255, 160, 0);   // #FFA000

        Dimension buttonSize = new Dimension(140, 32);
        JButton[] buttons = {addButton, editButton, removeButton, searchButton, refreshButton};
        Color[] baseColors = {addColor, editColor, removeColor, searchColor, new Color(41, 182, 246)};
        Color[] hoverColors = {hoverAdd, hoverEdit, hoverRemove, hoverSearch, new Color(3, 169, 244)};
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setPreferredSize(buttonSize);
            btn.setMinimumSize(buttonSize);
            btn.setMaximumSize(buttonSize);
            btn.setBackground(baseColors[i]);
            btn.setForeground(Color.WHITE);
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(true);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColors[i].darker(), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
            ));
            int idx = i;
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(hoverColors[idx]);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(baseColors[idx]);
                }
            });
        }
        countLabel = new JLabel("2 movies");
        JCheckBox imdbScoreCheck = new JCheckBox("IMDB Score");
        topPanel.add(addButton);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(editButton);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(removeButton);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(searchButton);
        topPanel.add(Box.createHorizontalStrut(5));
        topPanel.add(refreshButton);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(exportButton);
        topPanel.add(Box.createHorizontalStrut(12));
        topPanel.add(countLabel);
        topPanel.add(Box.createHorizontalStrut(20));
        JCheckBox showInfo = new JCheckBox("Show movie information", true);
        topPanel.add(showInfo);

        // Table for movies
        String[] columns = {"Movie", "IMDB Score", "Genre(s)", "Year of release", "Director", "Actor(s)", "Review", "Your comments", "Running time"};
        tableModel = new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        movieTable = new JTable(tableModel);
        movieTable.setRowHeight(28);
        // Movie Options tablosu için sıralama desteği ekle
        TableRowSorter<javax.swing.table.DefaultTableModel> movieSorter = new TableRowSorter<>(tableModel);
        movieTable.setRowSorter(movieSorter);
        // Zebra (alternatif satır) efekti + seçili satır (MOVIE TABLE)
        javax.swing.table.DefaultTableCellRenderer movieZebraRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(209, 234, 254)); // Açık mavi (seçili)
                } else if (row % 2 == 0) {
                    c.setBackground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(new java.awt.Color(245, 245, 245)); // Açık gri zebra
                }
                return c;
            }
        };
        for (int i = 0; i < movieTable.getColumnCount(); i++) {
            movieTable.getColumnModel().getColumn(i).setCellRenderer(movieZebraRenderer);
        }
        // Movie Options tablosu başlığını belirginleştir
        JTableHeader movieHeader = movieTable.getTableHeader();
        movieHeader.setBackground(new Color(56, 142, 60)); // #388E3C koyu yeşil
        movieHeader.setForeground(Color.WHITE);
        movieHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        // Make header bold
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableScroll = new JScrollPane(movieTable);

        // Movie info panel
        JPanel movieInfoPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Drop shadow
                g2.setColor(new Color(0,0,0,32));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 32, 32);
                // Panel background
                g2.setColor(new Color(255,255,255,245));
                g2.fillRoundRect(0, 0, getWidth()-8, getHeight()-8, 24, 24);
                g2.dispose();
            }
        };
        movieInfoPanel.setOpaque(false);
        movieInfoPanel.setBorder(BorderFactory.createEmptyBorder(18, 32, 18, 32));
        movieInfoPanel.setVisible(showInfo.isSelected());
        moviesTab.add(movieInfoPanel, BorderLayout.SOUTH);

        // Listener: Show/hide info panel
        showInfo.addItemListener(e -> {
            movieInfoPanel.setVisible(showInfo.isSelected());
        });

        // Listener: Update info panel on row selection
        movieTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && movieTable.getSelectedRow() != -1) {
                int row = movieTable.getSelectedRow();
                StringBuilder html = new StringBuilder();
                html.append("<html><div style='font-size:18pt;font-weight:bold;color:#222;margin-bottom:12px;text-shadow:1px 1px 0 #eee;'>🎬 ")
                    .append(tableModel.getValueAt(row, 0)).append("</div>");
                html.append("<div style='font-size:11.5pt;margin-bottom:8px;'><b>Director:</b> <span style='color:#1565c0;'>").append(tableModel.getValueAt(row, 4)).append("</span> &nbsp; ");
                html.append("<b>Actor(s):</b> <span style='color:#2e7d32;'>").append(tableModel.getValueAt(row, 5)).append("</span> &nbsp; ");
                html.append("<b>Genre(s):</b> <span style='color:#ad1457;'>").append(tableModel.getValueAt(row, 2)).append("</span> &nbsp; ");
                html.append("<b>Year:</b> <span style='color:#6d4c41;'>").append(tableModel.getValueAt(row, 3)).append("</span> &nbsp; ");
                html.append("<b>Length:</b> <span style='color:#283593;'>").append(tableModel.getValueAt(row, 8)).append("</span></div>");
                html.append("<div style='font-size:12.5pt;margin-bottom:8px;'><b>⭐ IMDB:</b> <span style='color:#fbc02d;font-size:14pt;'>").append(tableModel.getValueAt(row, 1)).append("</span> <span style='color:#888;'>/ 10</span></div>");
                html.append("<hr style='border:0;border-top:1px solid #eee;margin:8px 0;'>");
                html.append("<table width='100%' style='font-size:11.5pt;'><tr><th align='left' style='padding-bottom:4px;'>Review</th><th align='left' style='padding-bottom:4px;'>Your comments</th></tr><tr><td valign='top' style='background:#f7f7f7;padding:6px 8px;border-radius:6px;'>");
                html.append(tableModel.getValueAt(row, 6)).append("</td><td valign='top' style='background:#f7f7f7;padding:6px 8px;border-radius:6px;'>");
                html.append(tableModel.getValueAt(row, 7)).append("</td></tr></table></html>");
                movieInfoPanel.removeAll();
                movieInfoPanel.add(new JLabel(html.toString()), BorderLayout.CENTER);
                movieInfoPanel.revalidate();
                movieInfoPanel.repaint();
            }
        });

        // Load data from DB
        // dbManager = new DatabaseManager(); // Artık parametre ile geliyor, tekrar oluşturulmaz.
        // Add panels and tabs only once (constructor)
        // Yeni ana panel: buton paneli + boşluk + tablo paneli
        JPanel moviesMainPanel = new JPanel();
        moviesMainPanel.setLayout(new BoxLayout(moviesMainPanel, BoxLayout.Y_AXIS));
        moviesMainPanel.setBackground(new Color(245, 247, 250));
        moviesMainPanel.add(topPanel);
        moviesMainPanel.add(Box.createVerticalStrut(18));
        moviesMainPanel.add(tableScroll);
        moviesTab.add(moviesMainPanel, BorderLayout.CENTER);
        // --- USER OPTIONS TAB ---
        JPanel usersTab = new JPanel(new BorderLayout(5, 5));
        usersTab.setBackground(new Color(245, 247, 250));
        // Modern kutulu ana panel
        JPanel userMainPanel = new JPanel(new BorderLayout(10, 10));
        userMainPanel.setBackground(new Color(245, 247, 250));
        userMainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(16, 16, 16, 16),
            BorderFactory.createLineBorder(new Color(210, 210, 210), 1, true)
        ));

        // Top panel for user actions
        JPanel userTopPanel = new JPanel();
        userTopPanel.setLayout(new BoxLayout(userTopPanel, BoxLayout.X_AXIS));
        userTopPanel.setBackground(new Color(245, 247, 250));
        userTopPanel.setBorder(BorderFactory.createEmptyBorder(18, 12, 18, 12));
        JButton addUserBtn = new JButton("+ Add User");
        addUserBtn.setBackground(new Color(76, 175, 80));
        addUserBtn.setForeground(Color.WHITE);
        addUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addUserBtn.setFocusPainted(false);
        addUserBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(56, 142, 60)),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        addUserBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        JButton editUserBtn = new JButton("# Edit User");
        editUserBtn.setBackground(new Color(33, 150, 243));
        editUserBtn.setForeground(Color.WHITE);
        editUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        editUserBtn.setFocusPainted(false);
        editUserBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(25, 118, 210)),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        editUserBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        JButton removeUserBtn = new JButton("- Remove User");
        removeUserBtn.setBackground(new Color(244, 67, 54));
        removeUserBtn.setForeground(Color.WHITE);
        removeUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        removeUserBtn.setFocusPainted(false);
        removeUserBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(211, 47, 47)),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        removeUserBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        userTopPanel.add(addUserBtn);
        userTopPanel.add(Box.createHorizontalStrut(12));
        userTopPanel.add(editUserBtn);
        userTopPanel.add(Box.createHorizontalStrut(12));
        userTopPanel.add(removeUserBtn);
        userTopPanel.add(Box.createHorizontalGlue());
        // User Table
        String[] userColumns = {"Username", "Password", "Role", "First Name", "Last Name"};
        javax.swing.table.DefaultTableModel userTableModel = new javax.swing.table.DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable userTable = new JTable(userTableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(28);
        userTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane userTableScroll = new JScrollPane(userTable);
        userTableScroll.setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
        userTable.setFillsViewportHeight(true);
        // Tablo başlığını (header) belirginleştir
        JTableHeader userHeader = userTable.getTableHeader();
        userHeader.setBackground(new Color(25, 118, 210)); // #1976D2 koyu mavi
        userHeader.setForeground(Color.WHITE);
        userHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JPanel userTablePanel = new JPanel(new BorderLayout());
        userTablePanel.setBackground(new Color(245, 247, 250));
        // Zebra (alternatif satır) efekti + seçili satır (USER TABLE)
        javax.swing.table.DefaultTableCellRenderer userZebraRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new java.awt.Color(209, 234, 254)); // Açık mavi (seçili)
                } else if (row % 2 == 0) {
                    c.setBackground(java.awt.Color.WHITE);
                } else {
                    c.setBackground(new java.awt.Color(245, 245, 245)); // Açık gri zebra
                }
                return c;
            }
        };
        for (int i = 0; i < userTable.getColumnCount(); i++) {
            userTable.getColumnModel().getColumn(i).setCellRenderer(userZebraRenderer);
        }
        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        searchPanel.add(searchLabel, BorderLayout.WEST);
        JTextField userSearchField = new JTextField();
        userSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userSearchField.setToolTipText("Search users...");
        userSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchPanel.add(userSearchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        userTablePanel.add(searchPanel, BorderLayout.NORTH);
        userTablePanel.add(userTableScroll, BorderLayout.CENTER);
        // Tablo filtreleme
        TableRowSorter<javax.swing.table.DefaultTableModel> userSorter = new TableRowSorter<>(userTableModel);
        userTable.setRowSorter(userSorter);
        userSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = userSearchField.getText().trim();
                if (text.length() == 0) {
                    userSorter.setRowFilter(null);
                } else {
                    userSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 2, 3, 4)); // Username, Role, First Name, Last Name
                }
            }
        });
        userMainPanel.add(userTablePanel, BorderLayout.CENTER);
        userMainPanel.add(userTopPanel, BorderLayout.SOUTH);
        // Load users from DB
        Runnable reloadUserTable = () -> {
            userTableModel.setRowCount(0);
            try {
                String sql = "SELECT username, password, role, first_name, last_name FROM users ORDER BY username";
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    userTableModel.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("first_name"),
                        rs.getString("last_name")
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(usersTab, "Error loading users: " + e.getMessage());
            }
        };
        reloadUserTable.run();
        // Add User
        addUserBtn.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(MovieAdminPanel.this);
            new com.moviesinfo.ui.dialogs.AddUserDialog(win, dbManager, reloadUserTable).setVisible(true);
        });
        // Edit User
        editUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(usersTab, "Please select a user to edit.");
                return;
            }
            String username = userTableModel.getValueAt(row, 0).toString();
            Window win = SwingUtilities.getWindowAncestor(MovieAdminPanel.this);
            new com.moviesinfo.ui.dialogs.EditUserDialog(win, dbManager, username, reloadUserTable).setVisible(true);
        });
        // Remove User
        removeUserBtn.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(usersTab, "Please select a user to remove.");
                return;
            }
            String username = userTableModel.getValueAt(row, 0).toString();
            if (username.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(usersTab, "Admin kullanıcısı silinemez!");
                return;
            }
            int result = JOptionPane.showConfirmDialog(usersTab, "Are you sure you want to delete user '" + username + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    String sql = "DELETE FROM users WHERE username=?";
                    PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.executeUpdate();
                    reloadUserTable.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(usersTab, "Error deleting user: " + ex.getMessage());
                }
            }
        });
        usersTab.add(userMainPanel, BorderLayout.CENTER);
        tabbedPane.addTab("Movie Options", moviesTab);
        tabbedPane.addTab("User Options", usersTab);
        add(tabbedPane, BorderLayout.CENTER);
        // Sekme başlıklarını renkli ve ikonlu yap
        // Movie sekmesi için ikonlu label
        ImageIcon movieIcon = null;
        try {
            java.net.URL movieIconUrl = getClass().getResource("/movie.png");
            if (movieIconUrl != null) movieIcon = new ImageIcon(movieIconUrl);
        } catch (Exception e) {}
        JLabel movieTabLabel = new JLabel("Movie Options");
        if (movieIcon != null) movieTabLabel.setIcon(movieIcon);
        movieTabLabel.setIconTextGap(8);
        movieTabLabel.setOpaque(true);
        movieTabLabel.setBackground(new Color(103, 58, 183)); // Mor
        movieTabLabel.setForeground(Color.WHITE);
        movieTabLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        // User sekmesi için ikonlu label
        ImageIcon userIcon = null;
        try {
            java.net.URL userIconUrl = getClass().getResource("/user.png");
            if (userIconUrl != null) userIcon = new ImageIcon(userIconUrl);
        } catch (Exception e) {}
        JLabel userTabLabel = new JLabel("User Options");
        if (userIcon != null) userTabLabel.setIcon(userIcon);
        userTabLabel.setIconTextGap(8);
        userTabLabel.setOpaque(true);
        userTabLabel.setBackground(new Color(0, 151, 167)); // Koyu turkuaz
        userTabLabel.setForeground(Color.WHITE);
        userTabLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setTabComponentAt(0, movieTabLabel);
        tabbedPane.setTabComponentAt(1, userTabLabel);

        // Tab seçimine göre başlık renklerini güncelle
        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 0) {
                movieTabLabel.setBackground(new Color(103, 58, 183)); // Mor
                movieTabLabel.setForeground(Color.WHITE);
                userTabLabel.setBackground(new Color(230, 230, 230)); // Açık gri
                userTabLabel.setForeground(new Color(80, 80, 80));
            } else {
                movieTabLabel.setBackground(new Color(230, 230, 230)); // Açık gri
                movieTabLabel.setForeground(new Color(80, 80, 80));
                userTabLabel.setBackground(new Color(0, 151, 167)); // Koyu turkuaz
                userTabLabel.setForeground(Color.WHITE);
            }
        });
        // İlk açılışta doğru renkleri ayarla
        tabbedPane.setSelectedIndex(0);
        movieTabLabel.setBackground(new Color(103, 58, 183));
        movieTabLabel.setForeground(Color.WHITE);
        userTabLabel.setBackground(new Color(230, 230, 230));
        userTabLabel.setForeground(new Color(80, 80, 80));

        reloadMovieTable();
        // On startup: if there are movies, select the first row and update info panel
        if (tableModel.getRowCount() > 0) {
            movieTable.setRowSelectionInterval(0, 0);
            if (showInfo.isSelected()) {
                // Force panel update
                int row = 0;
                StringBuilder html = new StringBuilder();
                html.append("<html><div style='font-size:18pt;font-weight:bold;color:#222;margin-bottom:12px;text-shadow:1px 1px 0 #eee;'>🎬 ")
                    .append(tableModel.getValueAt(row, 0)).append("</div>");
                html.append("<div style='font-size:11.5pt;margin-bottom:8px;'><b>Director:</b> <span style='color:#1565c0;'>").append(tableModel.getValueAt(row, 4)).append("</span> &nbsp; ");
                html.append("<b>Actor(s):</b> <span style='color:#2e7d32;'>").append(tableModel.getValueAt(row, 5)).append("</span> &nbsp; ");
                html.append("<b>Genre(s):</b> <span style='color:#ad1457;'>").append(tableModel.getValueAt(row, 2)).append("</span> &nbsp; ");
                html.append("<b>Year:</b> <span style='color:#6d4c41;'>").append(tableModel.getValueAt(row, 3)).append("</span> &nbsp; ");
                html.append("<b>Length:</b> <span style='color:#283593;'>").append(tableModel.getValueAt(row, 8)).append("</span></div>");
                html.append("<div style='font-size:12.5pt;margin-bottom:8px;'><b>⭐ IMDB:</b> <span style='color:#fbc02d;font-size:14pt;'>").append(tableModel.getValueAt(row, 1)).append("</span> <span style='color:#888;'>/ 10</span></div>");
                html.append("<hr style='border:0;border-top:1px solid #eee;margin:8px 0;'>");
                html.append("<table width='100%' style='font-size:11.5pt;'><tr><th align='left' style='padding-bottom:4px;'>Review</th><th align='left' style='padding-bottom:4px;'>Your comments</th></tr><tr><td valign='top' style='background:#f7f7f7;padding:6px 8px;border-radius:6px;'>");
                html.append(tableModel.getValueAt(row, 6)).append("</td><td valign='top' style='background:#f7f7f7;padding:6px 8px;border-radius:6px;'>");
                html.append(tableModel.getValueAt(row, 7)).append("</td></tr></table></html>");
                movieInfoPanel.removeAll();
                movieInfoPanel.add(new JLabel(html.toString()), BorderLayout.CENTER);
                movieInfoPanel.revalidate();
                movieInfoPanel.repaint();
            }
        }
    }

    // Belirli bir kullanıcıya ait favori filmleri tabloya yükler
    public void loadUserFavorites(int userId, javax.swing.table.DefaultTableModel favTableModel) {
        favTableModel.setRowCount(0);
        try {
            String sql = "SELECT f.id, f.movie_id, m.title FROM favorites f JOIN movies m ON f.movie_id = m.id WHERE f.user_id = ?";
            java.sql.PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
            pstmt.setInt(1, userId);
            java.sql.ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                favTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getInt("movie_id"),
                    rs.getString("title")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Favori filmler yüklenirken hata: " + ex.getMessage());
        }
    }

    // Helper to reload table data
    private void reloadMovieTable() {
        // Clear table
        tableModel.setRowCount(0);
        int count = 0;
        try {
            String sql = "SELECT title, imdb_score, genre, year, director, actors, review, comments, running_time FROM movies ORDER BY title";
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("title"),
                    rs.getDouble("imdb_score"),
                    rs.getString("genre"),
                    rs.getInt("year"),
                    rs.getString("director"),
                    rs.getString("actors"),
                    rs.getString("review"),
                    rs.getString("comments"),
                    rs.getString("running_time")
                });
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        countLabel.setText(count + (count == 1 ? " movie" : " movies"));
    }
}
