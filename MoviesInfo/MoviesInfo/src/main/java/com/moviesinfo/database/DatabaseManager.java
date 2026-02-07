package com.moviesinfo.database;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:moviesinfo.db";
    private Connection conn;

    public DatabaseManager() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDatabase() throws SQLException {
        Statement stmt = conn.createStatement();
        // Movies tablosunu oluştur (varsa dokunma)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS movies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "imdb_score REAL," +
                "genre TEXT," +
                "year INTEGER," +
                "director TEXT," +
                "actors TEXT," +
                "review TEXT," +
                "comments TEXT," +
                "running_time TEXT"
                + ");");
        // örnek veri ekle (yoksa ekle)
        stmt.executeUpdate("INSERT OR IGNORE INTO movies (id, title, imdb_score, genre, year, director, actors, review, comments, running_time) VALUES " +
                "(1, 'The Shawshank Redemption', 9.3, 'Drama', 1994, 'Frank Darabont', 'Tim Robbins, Morgan Freeman', 'A masterpiece', 'Must watch', '142 minutes')," +
                "(2, 'The Godfather', 9.2, 'Crime', 1972, 'Francis Ford Coppola', 'Marlon Brando, Al Pacino', 'Classic', 'Legendary', '175 minutes')," +
                "(3, 'The Dark Knight', 9.0, 'Action', 2008, 'Christopher Nolan', 'Christian Bale, Heath Ledger', 'Best Batman', 'Great villain', '152 minutes')," +
                "(4, '12 Angry Men', 9.0, 'Drama', 1957, 'Sidney Lumet', 'Henry Fonda', 'Intense', 'Courtroom drama', '96 minutes')," +
                "(5, 'Schindler''s List', 8.9, 'Biography', 1993, 'Steven Spielberg', 'Liam Neeson', 'Touching', 'Emotional', '195 minutes')," +
                "(6, 'The Lord of the Rings', 8.8, 'Adventure', 2003, 'Peter Jackson', 'Elijah Wood, Ian McKellen', 'Epic', 'Fantasy classic', '201 minutes')," +
                "(7, 'Pulp Fiction', 8.9, 'Crime', 1994, 'Quentin Tarantino', 'John Travolta, Uma Thurman', 'Cult classic', 'Non-linear', '154 minutes')," +
                "(8, 'Forrest Gump', 8.8, 'Drama', 1994, 'Robert Zemeckis', 'Tom Hanks', 'Heartwarming', 'Inspirational', '142 minutes')," +
                "(9, 'Inception', 8.8, 'Action', 2010, 'Christopher Nolan', 'Leonardo DiCaprio', 'Mind-bending', 'Complex', '148 minutes')," +
                "(10, 'Fight Club', 8.8, 'Drama', 1999, 'David Fincher', 'Brad Pitt, Edward Norton', 'Cult', 'Twist ending', '139 minutes')");
        // Users tablosunu oluştur (varsa dokunma)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "role TEXT NOT NULL," +
                "first_name TEXT," +
                "last_name TEXT" +
                ");");
        // Eksik kolonları ekle (ALTER TABLE)
        ResultSet rs = conn.getMetaData().getColumns(null, null, "users", "first_name");
        if (!rs.next()) {
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN first_name TEXT;");
        }
        rs = conn.getMetaData().getColumns(null, null, "users", "last_name");
        if (!rs.next()) {
            stmt.executeUpdate("ALTER TABLE users ADD COLUMN last_name TEXT;");
        }
        // örnek admin ve user ekle
        stmt.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', 'admin', 'admin')");
        stmt.executeUpdate("INSERT OR IGNORE INTO users (username, password, role) VALUES ('user', 'user', 'user')");
        // Favoriler tablosunu oluştur (varsa dokunma)
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS favorites (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "movie_id INTEGER NOT NULL," +
                "added_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE(user_id, movie_id)" +
                ");");
        stmt.close();
    }

    public Connection getConnection() {
        return conn;
    }

    // Kullanıcıya film favorisi ekle
    public void addFavorite(int userId, int movieId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO favorites (user_id, movie_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }

    // Kullanıcıdan film favorisini sil
    public void removeFavorite(int userId, int movieId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND movie_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);
            stmt.executeUpdate();
        }
    }

    // Returns a list of favorite movie IDs for the given user
    public List<Integer> getFavoriteMovieIds(int userId) throws SQLException {
        List<Integer> favorites = new ArrayList<>();
        String sql = "SELECT movie_id FROM favorites WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                favorites.add(rs.getInt("movie_id"));
            }
        }
        return favorites;
    }
}
