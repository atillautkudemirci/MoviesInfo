package com.moviesinfo.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.moviesinfo.database.DatabaseManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> roleBox;
    private DatabaseManager dbManager;
    private Runnable onUserEdited;
    private String originalUsername;

    public EditUserDialog(Window owner, DatabaseManager dbManager, String username, Runnable onUserEdited) {
        super(owner, "Edit user", ModalityType.APPLICATION_MODAL);
        this.dbManager = dbManager;
        this.onUserEdited = onUserEdited;
        this.originalUsername = username;
        setLayout(new BorderLayout(10,10));
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
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Username"), c);
        c.gridx = 1; usernameField = new JTextField(14); usernameField.setPreferredSize(new Dimension(140, 22)); form.add(usernameField, c);
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Password"), c);
        c.gridx = 1; passwordField = new JPasswordField(14); passwordField.setPreferredSize(new Dimension(140, 22)); form.add(passwordField, c);
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Role"), c);
        c.gridx = 1; roleBox = new JComboBox<>(new String[] {"user", "admin"}); form.add(roleBox, c);
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("First Name"), c);
        c.gridx = 1; firstNameField = new JTextField(14); firstNameField.setPreferredSize(new Dimension(140, 22)); form.add(firstNameField, c);
        c.gridx = 0; c.gridy++;
        form.add(new JLabel("Last Name"), c);
        c.gridx = 1; lastNameField = new JTextField(14); lastNameField.setPreferredSize(new Dimension(140, 22)); form.add(lastNameField, c);
        shadowPanel.add(form, BorderLayout.CENTER);
        add(shadowPanel, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editBtn = new JButton("# Edit user");
        JButton closeBtn = new JButton("✖ Close");
        btnPanel.add(editBtn); btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
        editBtn.setBackground(new Color(33, 150, 243));
        editBtn.setForeground(Color.WHITE);
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(Color.DARK_GRAY);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        // Kullanıcı bilgilerini doldur
        try {
            PreparedStatement pstmt = dbManager.getConnection().prepareStatement(
                "SELECT username, password, role, first_name, last_name FROM users WHERE username=?"
            );
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                passwordField.setText(rs.getString("password"));
                roleBox.setSelectedItem(rs.getString("role"));
                firstNameField.setText(rs.getString("first_name"));
                lastNameField.setText(rs.getString("last_name"));
            }
            usernameField.setEditable(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading user: " + ex.getMessage());
        }
        editBtn.addActionListener(e -> {
            try {
                String password = new String(passwordField.getPassword());
                String role = roleBox.getSelectedItem().toString();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                if (password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Password, first name ve last name boş olamaz.");
                    return;
                }
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(
                    "UPDATE users SET password=?, role=?, first_name=?, last_name=? WHERE username=?"
                );
                pstmt.setString(1, password);
                pstmt.setString(2, role);
                pstmt.setString(3, firstName);
                pstmt.setString(4, lastName);
                pstmt.setString(5, originalUsername);
                pstmt.executeUpdate();
                if (onUserEdited != null) onUserEdited.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error editing user: " + ex.getMessage());
            }
        });
        closeBtn.addActionListener(e -> dispose());
        getContentPane().setBackground(new Color(227, 242, 253));
        setPreferredSize(new Dimension(420, 320));
        pack();
        setLocationRelativeTo(owner);
    }
}
