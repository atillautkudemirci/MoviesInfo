package com.moviesinfo.ui;

import javax.swing.*;
import java.awt.*;
import com.moviesinfo.database.DatabaseManager;

public class RegisterFrame extends JFrame {
    private JTextField firstNameField, lastNameField, usernameField;
    private JPasswordField passwordField, retypePasswordField;
    private JButton registerButton, cancelButton;
    private DatabaseManager dbManager;

    public RegisterFrame(DatabaseManager dbManager) {
        super("Register");
        this.dbManager = dbManager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Sol panel (görsel, başlık, açıklama)
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(220,245,245), getWidth(), getHeight(), new Color(180,225,210)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(340, 500));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(38, 28, 38, 28));

        // Logo veya film görseli
        ImageIcon logoIcon = null;
        JLabel imageLabel = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/logo.png");
            System.out.println("register logo.png resource: " + logoUrl);
            if (logoUrl != null) {
                logoIcon = new ImageIcon(logoUrl);
            } else {
                logoIcon = new ImageIcon(getClass().getResource("/movie.png"));
            }
        } catch (Exception e) {}
        if (logoIcon != null) {
            Image img = logoIcon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } else {
            imageLabel.setText("🎬");
            imageLabel.setFont(new Font("Arial", Font.BOLD, 64));
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalStrut(18));

        JLabel logoText = new JLabel("<html><div style='text-align:center;'>Film Information<br><span style='font-size:16px;'>Desktop Application</span></div></html>");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoText.setForeground(new Color(26, 80, 105));
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(logoText);
        leftPanel.add(Box.createVerticalStrut(16));

        JLabel desc = new JLabel("<html><div style='text-align:center;'>Create your account to manage and rate your favorite movies. Enjoy a seamless desktop experience for your personal film library!</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(44, 62, 80));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(desc);
        leftPanel.add(Box.createVerticalStrut(18));

        // Anahtar ikonu
        JLabel keyIcon = new JLabel();
        java.net.URL keyUrl = getClass().getResource("/key.png");
        if (keyUrl != null) {
            ImageIcon keyPng = new ImageIcon(keyUrl);
            Image img = keyPng.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            keyIcon.setIcon(new ImageIcon(img));
        } else {
            keyIcon.setText("\uD83D\uDD11");
            keyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        }
        keyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(keyIcon);
        leftPanel.add(Box.createVerticalGlue());

        // Sağ panel (form)
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(new Color(227, 242, 253)); // Hafif açık mavi
        rightPanel.setPreferredSize(new Dimension(360, 430));
        rightPanel.setLayout(null);

        JLabel welcome = new JLabel("Register");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(new Color(40, 80, 100));
        welcome.setBounds(90, 28, 250, 36);
        rightPanel.add(welcome);

        // Form inputları
        firstNameField = new JTextField();
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        firstNameField.setBounds(60, 80, 240, 46);
        firstNameField.setBorder(BorderFactory.createTitledBorder("First Name"));
        rightPanel.add(firstNameField);

        lastNameField = new JTextField();
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lastNameField.setBounds(60, 130, 240, 46);
        lastNameField.setBorder(BorderFactory.createTitledBorder("Last Name"));
        rightPanel.add(lastNameField);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        usernameField.setBounds(60, 180, 240, 46);
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        rightPanel.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBounds(60, 230, 240, 46);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        rightPanel.add(passwordField);

        retypePasswordField = new JPasswordField();
        retypePasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        retypePasswordField.setBounds(60, 280, 240, 46);
        retypePasswordField.setBorder(BorderFactory.createTitledBorder("Retype Password"));
        rightPanel.add(retypePasswordField);

        registerButton = new JButton("REGISTER");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        registerButton.setBackground(new Color(25, 118, 210)); // #1976d2
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBounds(60, 340, 115, 44);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(registerButton);

        cancelButton = new JButton("CANCEL");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        cancelButton.setBackground(new Color(220, 53, 69));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBounds(185, 340, 115, 44);
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(cancelButton);

        JLabel loginLabel = new JLabel("<html><u>Already have an account? Login</u></html>");
        loginLabel.setForeground(new Color(25, 118, 210)); // #1976d2
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.setFont(new Font("Arial", Font.BOLD, 15));
        loginLabel.setBounds(60, 400, 240, 22);
        rightPanel.add(loginLabel);

        // Panel ayrımı (ince çizgi)
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(220, 220, 220));
                g.fillRect(0, 0, 2, getHeight());
            }
        };
        divider.setPreferredSize(new Dimension(2, 430));

        add(leftPanel, BorderLayout.WEST);
        add(divider, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Eventler
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginFrame(dbManager).setVisible(true);
            }
        });
        registerButton.addActionListener(e -> handleRegister());
        cancelButton.addActionListener(e -> dispose());
    }

    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String retypePassword = new String(retypePasswordField.getPassword());
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        if (!password.equals(retypePassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }
        try {
            var pstmt = dbManager.getConnection().prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, 'user')"
            );
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.");
            dispose();
            new LoginFrame(dbManager).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage());
        }
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(230, 236, 245));
        field.setForeground(new Color(44, 62, 80));
        field.setCaretColor(new Color(44, 62, 80));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }
    private void styleTextField(JPasswordField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setBackground(new Color(230, 236, 245));
        field.setForeground(new Color(44, 62, 80));
        field.setCaretColor(new Color(44, 62, 80));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }
}
