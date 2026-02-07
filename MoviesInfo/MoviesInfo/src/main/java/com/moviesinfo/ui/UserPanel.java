package com.moviesinfo.ui;

import com.moviesinfo.database.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import com.moviesinfo.ui.dialogs.MovieDetailDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserPanel extends JFrame {
    private JTable movieTable;
    private JTable favoriteTable;
    private int currentUserId; // Giriş yapan kullanıcı ID'si

    private DefaultTableModel tableModel;
    private DefaultTableModel favoriteTableModel;
    private DatabaseManager dbManager;
    // Search fields
    private JTextField titleField, directorField, actorField, reviewField, commentsField;
    private JCheckBox imdbScoreCheck, yearCheck, durationCheck, genreCheck;
    private JComboBox<String> imdbScoreCombo, yearCombo, durationCombo;
    private JCheckBox[] genreBoxes;
    private JButton searchButton;
    private JButton refreshButton;
    private JPanel buttonPanel;
    private boolean isFiltered = false;
    private final String[] genres = {"Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Documentary", "Drama", "Family", "Fantasy", "Film-Noir", "Game-Show", "History", "Horror", "Music", "Musical", "Mystery", "News", "Reality-TV", "Romance", "Sci-Fi", "Short", "Talk-Show", "Thriller", "War", "Western"};
    private JTabbedPane tabbedPane;

    public UserPanel(DatabaseManager dbManager, int userId) {
        this.dbManager = dbManager;
        this.currentUserId = userId;
        // Pencere ikonu artık resource olarak yükleniyor
        ImageIcon icon = new ImageIcon(getClass().getResource("/pngegg.png"));
        setIconImage(icon.getImage());
        setTitle("MOVIES USER PANEL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 900);
        setMinimumSize(new Dimension(1280, 850));
        setLocationRelativeTo(null);
        setModernLookAndFeel();
        initUI(); // Önce UI ve tablo/model oluşturulur
        loadMovies(); // Sonra veri yüklenir
        // Tanı için: Veritabanında film var mı, konsola yazdır
        try {
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies");
            while (rs.next()) {
                System.out.println("Film: " + rs.getString("title"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Seçili filmi favorilere ekle
    private void addSelectedMovieToFavorites() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a movie to add to favorites.");
            return;
        }
        int movieId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            java.util.List<Integer> favoriteMovieIds = dbManager.getFavoriteMovieIds(currentUserId);
            if (favoriteMovieIds.contains(movieId)) {
                JOptionPane.showMessageDialog(this, "This movie is already in your favorites.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dbManager.addFavorite(currentUserId, movieId);
            JOptionPane.showMessageDialog(this, "Movie added to favorites.");
            movieTable.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error while adding to favorites: " + ex.getMessage());
        }
    }
    // Seçili filmi favorilerden kaldır
    private void removeSelectedMovieFromFavorites() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a movie to remove from favorites.");
            return;
        }
        int movieId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            java.util.List<Integer> favoriteMovieIds = dbManager.getFavoriteMovieIds(currentUserId);
            if (!favoriteMovieIds.contains(movieId)) {
                JOptionPane.showMessageDialog(this, "This movie is not in your favorites.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dbManager.removeFavorite(currentUserId, movieId);
            JOptionPane.showMessageDialog(this, "Movie removed from favorites.");
            movieTable.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error while removing from favorites: " + ex.getMessage());
        }
    }
    private void setModernLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Panel.background", new Color(245, 249, 255));
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 15));
            UIManager.put("TableHeader.background", new Color(220, 230, 245));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 16));
            UIManager.put("TableHeader.foreground", new Color(40, 60, 100));
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 15));
            UIManager.put("Button.background", new Color(33, 150, 243));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 15));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 15));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 15));
            UIManager.put("CheckBox.font", new Font("Segoe UI", Font.PLAIN, 15));
        } catch (Exception ignored) {}
    }
    private void initUI() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(18,18,10,18),
                BorderFactory.createLineBorder(new Color(210,220,235), 2, true)));
        searchPanel.setBackground(new Color(245,249,255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 0: Labels
        gbc.gridx = 0; gbc.gridy = 0; searchPanel.add(new JLabel("Title"), gbc);
        gbc.gridx = 1; searchPanel.add(new JLabel("Director"), gbc);
        gbc.gridx = 2; searchPanel.add(new JLabel("Actor"), gbc);
        gbc.gridx = 3; searchPanel.add(new JLabel("Review"), gbc);
        gbc.gridx = 4; searchPanel.add(new JLabel("Comments"), gbc);

        // Row 1: TextFields
        titleField = new JTextField(10); gbc.gridx = 0; gbc.gridy = 1; searchPanel.add(titleField, gbc);
        directorField = new JTextField(10); gbc.gridx = 1; searchPanel.add(directorField, gbc);
        actorField = new JTextField(10); gbc.gridx = 2; searchPanel.add(actorField, gbc);
        reviewField = new JTextField(10); gbc.gridx = 3; searchPanel.add(reviewField, gbc);
        commentsField = new JTextField(10); gbc.gridx = 4; searchPanel.add(commentsField, gbc);

        // Row 2: Checkboxes and Comboboxes
        imdbScoreCheck = new JCheckBox("IMDB Score"); gbc.gridx = 0; gbc.gridy = 2; searchPanel.add(imdbScoreCheck, gbc);
        imdbScoreCombo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) imdbScoreCombo.addItem(String.valueOf(i));
        gbc.gridx = 1; searchPanel.add(imdbScoreCombo, gbc);
        imdbScoreCombo.addActionListener(e -> {
            if (imdbScoreCombo.getSelectedIndex() > -1) imdbScoreCheck.setSelected(true);
            if (imdbScoreCombo.getSelectedIndex() == 0) imdbScoreCheck.setSelected(false);
        });

        yearCheck = new JCheckBox("Year of release"); gbc.gridx = 2; searchPanel.add(yearCheck, gbc);
        yearCombo = new JComboBox<>();
        yearCombo.addItem("Unknown");
        for (int y = 1950; y <= 2030; y++) yearCombo.addItem(String.valueOf(y));
        gbc.gridx = 3; searchPanel.add(yearCombo, gbc);
        yearCombo.addActionListener(e -> {
            if (yearCombo.getSelectedIndex() > 0) yearCheck.setSelected(true);
            if (yearCombo.getSelectedIndex() == 0) yearCheck.setSelected(false);
        });

        durationCheck = new JCheckBox("Running time at least"); gbc.gridx = 4; searchPanel.add(durationCheck, gbc);
        durationCombo = new JComboBox<>(new String[]{"Unknown", "60", "90", "120", "150", "180"});
        gbc.gridx = 5; searchPanel.add(durationCombo, gbc);
        durationCombo.addActionListener(e -> {
            if (durationCombo.getSelectedIndex() > 0) durationCheck.setSelected(true);
            if (durationCombo.getSelectedIndex() == 0) durationCheck.setSelected(false);
        });

        // Row 3: Genres
        genreCheck = new JCheckBox("In this/these genre(s)"); gbc.gridx = 0; gbc.gridy = 3; searchPanel.add(genreCheck, gbc);
        JPanel genrePanel = new JPanel(new GridLayout(4, 7, 6, 6));
        genrePanel.setOpaque(false);
        genreBoxes = new JCheckBox[genres.length];
        for (int i = 0; i < genres.length; i++) {
            genreBoxes[i] = new JCheckBox(genres[i]);
            genrePanel.add(genreBoxes[i]);
            genreBoxes[i].addActionListener(e -> {
                boolean anySelected = false;
                for (JCheckBox box : genreBoxes) if (box.isSelected()) anySelected = true;
                genreCheck.setSelected(anySelected);
            });
        }
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 5; searchPanel.add(genrePanel, gbc);
        gbc.gridwidth = 1;

        // Row 4: Search button
        searchButton = new JButton("Search") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(230, 120, 0));
                } else if (!isEnabled()) {
                    g.setColor(new Color(255, 200, 120));
                } else {
                    g.setColor(new Color(255, 140, 0));
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Shadow
                g.setColor(new Color(255, 140, 0, 60));
                g.fillRoundRect(2, getHeight()-6, getWidth()-4, 6, 12, 12);
                super.paintComponent(g);
            }
        };
        searchButton.setFocusPainted(false);
        searchButton.setOpaque(false);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        searchButton.setPreferredSize(new Dimension(120, 36));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setToolTipText("Search movies with selected filters");

        refreshButton = new JButton("Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(30, 100, 200));
                } else if (!isEnabled()) {
                    g.setColor(new Color(180, 210, 255));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(70, 140, 240));
                } else {
                    g.setColor(new Color(40, 120, 230));
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g.setColor(new Color(40, 120, 230, 60));
                g.fillRoundRect(2, getHeight()-6, getWidth()-4, 6, 12, 12);
                super.paintComponent(g);
            }
        };
        refreshButton.setFocusPainted(false);
        refreshButton.setOpaque(false);
        refreshButton.setContentAreaFilled(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        refreshButton.setPreferredSize(new Dimension(120, 36));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setToolTipText("Show all movies and clear filters");
        refreshButton.setVisible(false);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        // Favoriye Ekle butonu
        JButton favoriteButton = new JButton("Add to Favorites") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(0, 160, 90));
                } else if (!isEnabled()) {
                    g.setColor(new Color(180, 230, 200));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(0, 220, 140));
                } else {
                    g.setColor(new Color(0, 200, 120));
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g.setColor(new Color(0, 200, 120, 60));
                g.fillRoundRect(2, getHeight()-6, getWidth()-4, 6, 12, 12);
                super.paintComponent(g);
            }
        };
        favoriteButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        favoriteButton.setForeground(Color.WHITE);
        favoriteButton.setFocusPainted(false);
        favoriteButton.setOpaque(false);
        favoriteButton.setContentAreaFilled(false);
        favoriteButton.setBorderPainted(false);
        favoriteButton.setPreferredSize(new Dimension(160, 36));
        favoriteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favoriteButton.setToolTipText("Add selected movie to favorites");
        favoriteButton.addActionListener(e -> addSelectedMovieToFavorites());
        buttonPanel.add(favoriteButton);
        // Favoriden Kaldır butonu
        JButton removeFavoriteButton = new JButton("Remove from Favorites") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(180, 40, 40));
                } else if (!isEnabled()) {
                    g.setColor(new Color(255, 180, 180));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(240, 80, 80));
                } else {
                    g.setColor(new Color(220, 60, 60));
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g.setColor(new Color(220, 60, 60, 60));
                g.fillRoundRect(2, getHeight()-6, getWidth()-4, 6, 12, 12);
                super.paintComponent(g);
            }
        };
        removeFavoriteButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        removeFavoriteButton.setForeground(Color.WHITE);
        removeFavoriteButton.setFocusPainted(false);
        removeFavoriteButton.setOpaque(false);
        removeFavoriteButton.setContentAreaFilled(false);
        removeFavoriteButton.setBorderPainted(false);
        removeFavoriteButton.setPreferredSize(new Dimension(200, 36));
        removeFavoriteButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeFavoriteButton.setToolTipText("Remove selected movie from favorites");
        removeFavoriteButton.addActionListener(e -> {
    int row = favoriteTable.getSelectedRow();
    if (row != -1) {
        try {
            Object value = favoriteTableModel.getValueAt(row, 0);
            int movieId;
            if (value instanceof Integer) {
                movieId = (Integer) value;
            } else if (value instanceof Number) {
                movieId = ((Number) value).intValue();
            } else if (value instanceof String) {
                movieId = Integer.parseInt((String) value);
            } else {
                throw new IllegalArgumentException("Movie ID is not a valid number! Value: " + value);
            }
            dbManager.removeFavorite(currentUserId, movieId);
            loadFavoriteMovies();
            JOptionPane.showMessageDialog(UserPanel.this, "Movie removed from favorites.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(UserPanel.this, "Error while removing from favorites: " + ex.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(UserPanel.this, "Select a movie to remove from favorites.");
    }
});
buttonPanel.add(removeFavoriteButton);
gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; searchPanel.add(buttonPanel, gbc);
gbc.gridwidth = 1;

// Refresh logic
refreshButton.addActionListener(e -> resetFiltersAndReload());

searchButton.addActionListener(e -> {
    // Sadece 'All Movies' tabı aktifse search çalışsın
    if (tabbedPane.getSelectedIndex() == 0) {
        searchMovies();
    } else {
        // İstenirse kullanıcıya uyarı gösterilebilir
        JOptionPane.showMessageDialog(UserPanel.this, "Search only works in the 'All Movies' tab.");
    }
});

add(searchPanel, BorderLayout.NORTH);
        JLabel infoLabel = new JLabel("Double-click a movie row to see details. - Your favorite movies are highlighted in pink in the table.");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        infoLabel.setForeground(new Color(120, 130, 150));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(6, 22, 2, 2));

        

        // All Movies table setup
        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{
                "ID", "Title", "IMDB Score", "Genre", "Year", "Director", "Actors", "Review", "Comments", "Running Time"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        movieTable = new JTable(tableModel);
        // Sıralama desteği ekle
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        // ID sütunu (0. index) için integer sıralama kullan
        sorter.setComparator(0, (o1, o2) -> {
            Integer i1 = (o1 instanceof Integer) ? (Integer)o1 : Integer.parseInt(o1.toString());
            Integer i2 = (o2 instanceof Integer) ? (Integer)o2 : Integer.parseInt(o2.toString());
            return i1.compareTo(i2);
        });
        movieTable.setRowSorter(sorter);
        movieTable.setRowHeight(28);
        movieTable.setShowGrid(false);
        movieTable.setIntercellSpacing(new Dimension(0,0));
        movieTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        movieTable.getTableHeader().setReorderingAllowed(false);
        movieTable.getTableHeader().setPreferredSize(new Dimension(100, 38));
        // Sağ tık menüsü
        setupMovieTableContextMenu();
        // Çift tıklama ile ayrıntı göster
        movieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && movieTable.getSelectedRow() != -1) {
                    int viewRow = movieTable.getSelectedRow();
                    int modelRow = movieTable.convertRowIndexToModel(viewRow);
                    Object[] movieData = new Object[tableModel.getColumnCount()];
                    for (int i = 0; i < tableModel.getColumnCount(); i++) {
                        movieData[i] = tableModel.getValueAt(modelRow, i);
                    }
                    movieTable.setEnabled(false);
                    try {
                        new MovieDetailDialog(UserPanel.this, movieData).setVisible(true);
                    } finally {
                        movieTable.setEnabled(true);
                    }
                }
            }
        });
        // Favorite Movies tablosu için model oluştur
        favoriteTableModel = new DefaultTableModel(new Object[][]{}, new String[]{
                "ID", "Title", "IMDB Score", "Genre", "Year", "Director", "Actors", "Review", "Comments", "Running Time"
        }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        favoriteTable = new JTable(favoriteTableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                // Zebra çizgiler
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(250, 253, 255) : new Color(235, 242, 250));
                } else {
                    c.setBackground(new Color(180, 215, 255));
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
                }
                return c;
            }
        };
        favoriteTable.setRowHeight(28);
        favoriteTable.setShowGrid(false);
        favoriteTable.setIntercellSpacing(new Dimension(0,0));
        favoriteTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        favoriteTable.getTableHeader().setReorderingAllowed(false);
        favoriteTable.getTableHeader().setPreferredSize(new Dimension(100, 38));
        favoriteTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 16));
        favoriteTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        favoriteTable.setSelectionBackground(new Color(180, 215, 255));
        favoriteTable.setSelectionForeground(Color.BLACK);

        // Favori tabloya çift tıklama ile detay göster
        favoriteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && favoriteTable.getSelectedRow() != -1) {
                    int row = favoriteTable.getSelectedRow();
                    Object[] movieData = new Object[favoriteTableModel.getColumnCount()];
                    for (int i = 0; i < favoriteTableModel.getColumnCount(); i++) {
                        movieData[i] = favoriteTableModel.getValueAt(row, i);
                    }
                    favoriteTable.setEnabled(false);
                    try {
                        new MovieDetailDialog(UserPanel.this, movieData).setVisible(true);
                    } finally {
                        favoriteTable.setEnabled(true);
                    }
                    e.consume();
                }
            }
        });

        // Favori tablosunda seçili satırı Remove from Favorites ile sil (Delete tuşu)
        favoriteTable.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removeFavoriteRow");
        AbstractAction removeFavoriteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = favoriteTable.getSelectedRow();
                if (row != -1) {
                    try {
                        Object value = favoriteTableModel.getValueAt(row, 0);
                        int movieId;
                        if (value instanceof Integer) {
                            movieId = (Integer) value;
                        } else if (value instanceof Number) {
                            movieId = ((Number) value).intValue();
                        } else if (value instanceof String) {
                            movieId = Integer.parseInt((String) value);
                        } else {
                            throw new IllegalArgumentException("Movie ID is not a valid number! Value: " + value);
                        }
                        dbManager.removeFavorite(currentUserId, movieId);
                        loadFavoriteMovies();
                        JOptionPane.showMessageDialog(UserPanel.this, "Movie removed from favorites.");
                    } catch (Exception ex) {
                        ex.printStackTrace(); // Konsolda detaylı hata görmek için
                        JOptionPane.showMessageDialog(UserPanel.this, "Error while removing from favorites: " + ex.getMessage());
                    }
                }
            }
        };
        favoriteTable.getActionMap().put("removeFavoriteRow", removeFavoriteAction);

        // Sağ tık menüsü ile favorilerden kaldır
        JPopupMenu favoriteTablePopup = new JPopupMenu();
        JMenuItem removeFromFavoritesMenu = new JMenuItem("Favorilerden Kaldır");
        removeFromFavoritesMenu.addActionListener(removeFavoriteAction);
        favoriteTablePopup.add(removeFromFavoritesMenu);
        favoriteTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showPopup(e);
            }
            private void showPopup(MouseEvent e) {
                int row = favoriteTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    favoriteTable.setRowSelectionInterval(row, row);
                    favoriteTablePopup.show(favoriteTable, e.getX(), e.getY());
                }
            }
        });

        // --- ScrollPane ile tabloları sarmala ---
        JScrollPane allMoviesScroll = new JScrollPane(movieTable);
        JScrollPane favoriteMoviesScroll = new JScrollPane(favoriteTable);
        // Make scroll panes as wide as the search panel
        int panelWidth = 1240;
        int tableRowHeight = 28;
        int visibleRows = 10;
        int scrollPaneHeight = visibleRows * tableRowHeight + 40; // 10 satır + biraz boşluk
        allMoviesScroll.setPreferredSize(new Dimension(panelWidth, scrollPaneHeight));
        favoriteMoviesScroll.setPreferredSize(new Dimension(panelWidth, scrollPaneHeight));

        // --- TabbedPane oluştur ve sekmeleri ekle ---
        tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // nothing here, custom painting is in UI below
            }
        };
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tabbedPane.setBackground(new Color(245, 249, 255));
        tabbedPane.setForeground(new Color(60, 60, 60));
        tabbedPane.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 210, 230)));
        tabbedPane.addTab("All Movies", allMoviesScroll);
        tabbedPane.addTab("Favorite Movies", favoriteMoviesScroll);
        // Custom renkli sekme UI
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            private final Color blue = new Color(40, 120, 230);
            private final Color pink = new Color(255, 170, 200);
            private final Color selectedText = Color.WHITE;
            private final Color unselectedText = new Color(60, 60, 60);
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    if (tabIndex == 0) g.setColor(blue);
                    else if (tabIndex == 1) g.setColor(pink);
                    g.fillRoundRect(x, y+2, w, h-4, 14, 14);
                } else {
                    g.setColor(new Color(245, 249, 255));
                    g.fillRect(x, y, w, h);
                }
            }
            @Override
            protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
                g.setFont(font);
                if (isSelected) {
                    g.setColor(selectedText);
                } else {
                    g.setColor(unselectedText);
                }
                int y = textRect.y + metrics.getAscent();
                g.drawString(title, textRect.x, y);
            }
        });
        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            if (idx == 0) loadMovies();
            else if (idx == 1) loadFavoriteMovies();
        });

        // --- Info panel üstte ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(infoLabel);

        // --- Ana panel düzeni ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // --- Ana pencereye ekle ---
        add(mainPanel, BorderLayout.CENTER);
    }
    private void resetFiltersAndReload() {
        titleField.setText("");
        directorField.setText("");
        actorField.setText("");
        reviewField.setText("");
        commentsField.setText("");
        imdbScoreCheck.setSelected(false);
        imdbScoreCombo.setSelectedIndex(0);
        yearCheck.setSelected(false);
        yearCombo.setSelectedIndex(0);
        durationCheck.setSelected(false);
        durationCombo.setSelectedIndex(0);
        genreCheck.setSelected(false);
        for (JCheckBox box : genreBoxes) box.setSelected(false);
        loadMovies();
        // Tanı için: Veritabanında film var mı, konsola yazdır
        try {
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM movies");
            while (rs.next()) {
                System.out.println("Film: " + rs.getString("title"));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshButton.setVisible(false);
        isFiltered = false;
    }
    private void loadMovies() {
        loadMoviesByQuery("");
    }
    // Favori filmleri yükleyen fonksiyon
    private void loadFavoriteMovies() {
        favoriteTableModel.setRowCount(0); // Önce tabloyu temizle
        try {
            java.util.List<Integer> favoriteIds = dbManager.getFavoriteMovieIds(currentUserId);
            if (favoriteIds.isEmpty()) return;
            StringBuilder sb = new StringBuilder();
            for (int id : favoriteIds) {
                if (sb.length() > 0) sb.append(",");
                sb.append(id);
            }
            String query = "SELECT * FROM movies WHERE id IN (" + sb + ")";
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                favoriteTableModel.addRow(new Object[]{
                    rs.getInt("id"),
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
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void searchMovies() {
        StringBuilder where = new StringBuilder();
        // Text fields
        if (!titleField.getText().trim().isEmpty()) {
            appendWhere(where, "title LIKE '%" + escape(titleField.getText()) + "%'");
        }
        if (!directorField.getText().trim().isEmpty()) {
            appendWhere(where, "director LIKE '%" + escape(directorField.getText()) + "%'");
        }
        if (!actorField.getText().trim().isEmpty()) {
            appendWhere(where, "actors LIKE '%" + escape(actorField.getText()) + "%'");
        }
        if (!reviewField.getText().trim().isEmpty()) {
            appendWhere(where, "review LIKE '%" + escape(reviewField.getText()) + "%'");
        }
        if (!commentsField.getText().trim().isEmpty()) {
            appendWhere(where, "comments LIKE '%" + escape(commentsField.getText()) + "%'");
        }
        // IMDB Score
        if (imdbScoreCheck.isSelected()) {
            int selected = imdbScoreCombo.getSelectedIndex();
            if (selected > 0) {
                double lower = Double.parseDouble(imdbScoreCombo.getItemAt(selected));
                if (lower == 9.0) {
                    appendWhere(where, "imdb_score >= 9.0 AND imdb_score <= 10.0");
                } else {
                    double upper = lower + 1.0;
                    appendWhere(where, "imdb_score >= " + lower + " AND imdb_score < " + upper);
                }
            }
        }
        // Year
        if (yearCheck.isSelected() && !"Unknown".equals(yearCombo.getSelectedItem())) {
            appendWhere(where, "year = " + yearCombo.getSelectedItem());
        }
        // Duration
        if (durationCheck.isSelected() && !"Unknown".equals(durationCombo.getSelectedItem())) {
            appendWhere(where, "CAST(running_time AS INTEGER) >= " + durationCombo.getSelectedItem());
        }
        // Genres
        if (genreCheck.isSelected()) {
            StringBuilder genreWhere = new StringBuilder();
            for (int i = 0; i < genres.length; i++) {
                if (genreBoxes[i].isSelected()) {
                    if (genreWhere.length() > 0) genreWhere.append(" OR ");
                    genreWhere.append("genre LIKE '%" + genres[i] + "%'");
                }
            }
            if (genreWhere.length() > 0) {
                appendWhere(where, "(" + genreWhere.toString() + ")");
            }
        }
        loadMoviesByQuery(where.toString());
        refreshButton.setVisible(true);
        isFiltered = true;
    }
    private void loadMoviesByQuery(String whereClause) {
        try {
            Connection conn = dbManager.getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM movies";
            if (whereClause != null && !whereClause.isEmpty()) {
                sql += " WHERE " + whereClause;
            }
            ResultSet rs = stmt.executeQuery(sql);
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
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
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading movies.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void appendWhere(StringBuilder where, String clause) {
        if (where.length() > 0) where.append(" AND ");
        where.append(clause);
    }
    private String escape(String s) {
        return s.replace("'", "''");
    }
    // --- Sağ tık menüsü: Add/Remove Favorites ---
    private void setupMovieTableContextMenu() {
        JPopupMenu movieTablePopup = new JPopupMenu();
        JMenuItem addToFavoritesMenu = new JMenuItem("Add to Favorites");
        JMenuItem removeFromFavoritesMenu = new JMenuItem("Remove from Favorites");
        addToFavoritesMenu.addActionListener(e -> addSelectedMovieToFavorites());
        removeFromFavoritesMenu.addActionListener(e -> removeSelectedMovieFromFavorites());
        movieTablePopup.add(addToFavoritesMenu);
        movieTablePopup.add(removeFromFavoritesMenu);
        movieTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }
            private void showContextMenu(MouseEvent e) {
                int row = movieTable.rowAtPoint(e.getPoint());
                if (row != -1) {
                    movieTable.setRowSelectionInterval(row, row);
                    movieTablePopup.show(movieTable, e.getX(), e.getY());
                }
            }
        });
    }
}
