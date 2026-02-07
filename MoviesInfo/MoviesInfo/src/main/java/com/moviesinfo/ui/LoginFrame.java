package com.moviesinfo.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.moviesinfo.database.DatabaseManager;
import com.moviesinfo.ui.admin.MovieAdminPanel;

public class LoginFrame extends JFrame {
    private JPanel leftPanel, rightPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    private DatabaseManager dbManager;

    public LoginFrame(DatabaseManager dbManager) {
        super("Login");
        // Login penceresi için özel ikon
        // Login penceresi için ikon artık resource olarak yükleniyor
        ImageIcon icon = new ImageIcon(getClass().getResource("/pngegg.png"));
        setIconImage(icon.getImage());
        this.dbManager = dbManager;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 430);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Sol panel (film görseli, başlık, açıklama)
        leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Açık mavi-yeşil degrade arka plan
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new GradientPaint(0, 0, new Color(220,245,245), getWidth(), getHeight(), new Color(180,225,210)));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(340, 430));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(38, 28, 38, 28));

        // Logo veya film görseli
        ImageIcon logoIcon = null;
        JLabel imageLabel = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/logo.png");
            System.out.println("logo.png resource: " + logoUrl);
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
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalStrut(18));

        JLabel logoText = new JLabel("<html><div style='text-align:center;'>Movie Information<br><span style='font-size:16px;'>Desktop Application</span></div></html>");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoText.setForeground(new Color(26, 80, 105));
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(logoText);
        leftPanel.add(Box.createVerticalStrut(16));

        JLabel desc = new JLabel("<html><div style='text-align:center;'>Easily organize, discover, and manage your favorite movies. A seamless desktop experience for your personal film library.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(new Color(44, 62, 80));
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(desc);
        leftPanel.add(Box.createVerticalGlue());

        // Sağ panel (login formu)
        rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(360, 430));
        rightPanel.setLayout(null);

        JLabel welcome = new JLabel("Welcome");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcome.setForeground(new Color(40, 80, 100));
        welcome.setBounds(110, 28, 200, 36);
        rightPanel.add(welcome);

        // PNG anahtar ikonunu göster
        JLabel keyIcon = new JLabel();
        java.net.URL keyUrl = getClass().getResource("/key.png");
        if (keyUrl != null) {
            ImageIcon keyPng = new ImageIcon(keyUrl);
            Image img = keyPng.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            keyIcon.setIcon(new ImageIcon(img));
        } else {
            keyIcon.setText("\uD83D\uDD11");
            keyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
            keyIcon.setHorizontalAlignment(SwingConstants.CENTER);
        }
        keyIcon.setBounds(156, 70, 48, 48);
        rightPanel.add(keyIcon);

        JLabel loginText = new JLabel("Login to continue");
        loginText.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        loginText.setForeground(new Color(80, 100, 120));
        loginText.setBounds(120, 120, 200, 22);
        rightPanel.add(loginText);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setBounds(60, 140, 240, 46);
        usernameField.setBorder(BorderFactory.createTitledBorder("User"));
        rightPanel.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setBounds(60, 195, 240, 46);
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        rightPanel.add(passwordField);

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 17));
        loginButton.setBackground(new Color(0, 153, 136));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBounds(60, 255, 240, 44);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(loginButton);

        JLabel notRegLabel = new JLabel("Not registered yet?");
        notRegLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notRegLabel.setForeground(new Color(80, 100, 120));
        notRegLabel.setBounds(60, 330, 120, 22);
        rightPanel.add(notRegLabel);

        registerLabel = new JLabel("Create your account");
        registerLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerLabel.setForeground(new Color(33, 150, 243));
        registerLabel.setBounds(180, 330, 130, 22);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(registerLabel);

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

        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new RegisterFrame(dbManager).setVisible(true);
            }
        });
        loginButton.addActionListener(e -> handleLogin());

    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required.");
            return;
        }
        try {
            var pstmt = dbManager.getConnection().prepareStatement(
                "SELECT id, role FROM users WHERE username=? AND password=?"
            );
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            var rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String role = rs.getString("role");
                if (role.equals("admin")) {
                    JFrame adminFrame = new JFrame("Admin Panel");
                    adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    adminFrame.setSize(1200, 500);
                    adminFrame.setLocationRelativeTo(null);
                    adminFrame.setContentPane(new MovieAdminPanel(dbManager));
                    adminFrame.setVisible(true);
                } else {
                    new com.moviesinfo.ui.UserPanel(dbManager, userId).setVisible(true);
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage());
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password required.");
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Registration error: " + ex.getMessage());
        }
    }
}
