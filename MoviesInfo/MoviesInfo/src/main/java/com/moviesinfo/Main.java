package com.moviesinfo;

import javax.swing.*;
import com.moviesinfo.ui.admin.MovieAdminPanel;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            com.moviesinfo.database.DatabaseManager dbManager = new com.moviesinfo.database.DatabaseManager();
            new com.moviesinfo.ui.LoginFrame(dbManager).setVisible(true);
        });
    }
}
