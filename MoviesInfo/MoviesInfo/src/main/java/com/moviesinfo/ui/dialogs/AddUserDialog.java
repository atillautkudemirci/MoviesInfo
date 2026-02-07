package com.moviesinfo.ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.moviesinfo.database.DatabaseManager;
import java.sql.PreparedStatement;

public class AddUserDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> roleBox;
    private DatabaseManager dbManager;
    private Runnable onUserAdded;

    public AddUserDialog(Window owner, DatabaseManager dbManager, Runnable onUserAdded) {
        super(owner, "Add user", ModalityType.APPLICATION_MODAL);
        this.dbManager = dbManager;
        this.onUserAdded = onUserAdded;
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
        JButton addBtn = new JButton("+ Add user");
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
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = roleBox.getSelectedItem().toString();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username, password, first name ve last name boş olamaz.");
                    return;
                }
                PreparedStatement pstmt = dbManager.getConnection().prepareStatement(
                    "INSERT INTO users (username, password, role, first_name, last_name) VALUES (?, ?, ?, ?, ?)"
                );
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, role);
                pstmt.setString(4, firstName);
                pstmt.setString(5, lastName);
                pstmt.executeUpdate();
                if (onUserAdded != null) onUserAdded.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
            }
        });
        closeBtn.addActionListener(e -> dispose());
        getContentPane().setBackground(new Color(232, 245, 233));
        setPreferredSize(new Dimension(420, 320));
        pack();
        setLocationRelativeTo(owner);
    }
}
