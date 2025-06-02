package kasirin.ui;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlUserDAO;
import kasirin.data.model.User;
import kasirin.data.model.Role;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/// Form registrasi untuk membuat akun pengguna baru
/// @author yamaym
public class RegisterForm extends JFrame {
    // Komponen UI untuk input data
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<Role> roleComboBox;

    private JButton registerButton;
    private JButton backButton;

    // Reference ke form login dan DAO
    private LoginForm parentForm;
    private MySqlUserDAO userDAO;

    public RegisterForm(LoginForm parent) {
        this.parentForm = parent;

        // Inisialisasi DAO
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        userDAO = (MySqlUserDAO) factory.getUserDAO();

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Konfigurasi window
        setTitle("Kasirin - Daftar Akun Baru");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    /// Inisialisasi semua komponen UI dengan styling modern yang konsisten
    private void initializeComponents() {
        // Field nama lengkap dengan desain modern
        nameField = createStyledTextField();

        // Field username dengan styling yang sama
        usernameField = createStyledTextField();

        // Field password dengan desain yang konsisten
        passwordField = createStyledPasswordField();

        // Field konfirmasi password
        confirmPasswordField = createStyledPasswordField();

        // ComboBox untuk role dengan styling modern
        roleComboBox = new JComboBox<>(Role.values()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background dengan rounded corners
                g2.setColor(new Color(249, 250, 251));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        roleComboBox.setBackground(new Color(249, 250, 251));



        // Tombol register dengan gradien hijau
        registerButton = new JButton("Daftar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien background hijau
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(34, 197, 94),
                        0, getHeight(), new Color(22, 163, 74)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol kembali dengan desain outline
        backButton = new JButton("Kembali ke Login") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background putih
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border abu-abu
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(new Color(75, 85, 99));
        backButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /// Helper method untuk membuat text field dengan styling modern dan konsisten
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background dengan rounded corners
                g2.setColor(new Color(249, 250, 251));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border dengan warna yang soft
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setBackground(new Color(249, 250, 251));
        field.setOpaque(false);
        return field;
    }

    /// Helper method untuk membuat password field dengan styling modern dan konsisten
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background dengan rounded corners
                g2.setColor(new Color(249, 250, 251));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border dengan warna yang soft
                g2.setColor(new Color(229, 231, 235));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        field.setBackground(new Color(249, 250, 251));
        field.setOpaque(false);
        return field;
    }

    /// Setup layout form dengan desain yang lebih modern dan spacing yang optimal
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Background with subtle gradient
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        0, getHeight(), new Color(248, 250, 252)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Main panel with shadow and rounded corners
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 16, 16);
                g2.setColor(new Color(0, 0, 0, 4));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 16, 16);

                // White background
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 45, 40, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Daftar Akun Baru");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(17, 24, 39));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Reset for form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 0, 2, 15); // Added right padding for labels

        // Form fields in proper order
        // Row 1: Name
        JLabel nameLabel = new JLabel("Nama Lengkap");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(nameField, gbc);

        // Row 2: Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 2, 15);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(usernameField, gbc);

        // Row 3: Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 2, 15);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(passwordField, gbc);

        // Row 4: Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Konfirmasi Password");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        confirmPasswordLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 2, 15);
        mainPanel.add(confirmPasswordLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 15, 0);
        mainPanel.add(confirmPasswordField, gbc);

        // Row 5: Role
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 2, 15);
        mainPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 25, 0); // Extra bottom padding before buttons
        mainPanel.add(roleComboBox, gbc);

        // Row 6: Register Button
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(registerButton, gbc);

        // Row 7: Back Button
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(backButton, gbc);

        // Wrapper for centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        wrapperPanel.add(mainPanel);

        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /// Helper method untuk menambahkan field ke form dengan styling yang konsisten
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        // Label dengan styling yang lebih modern
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = row * 2 - 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        // Field dengan spacing yang optimal
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);

        // Reset insets untuk label berikutnya
        gbc.insets = new Insets(10, 0, 4, 0);
    }

    /// Setup event handlers untuk tombol dan validasi
    private void setupEventHandlers() {
        // Handler untuk tombol register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        // Handler untuk tombol kembali
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });

        // Handler untuk window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                goBackToLogin();
            }
        });
    }

    /// Melakukan proses registrasi dengan validasi lengkap
    private void performRegistration() {
        // Ambil data dari form
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        Role role = (Role) roleComboBox.getSelectedItem();

        // Validasi input kosong
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() ) {
            showErrorMessage("Semua field harus diisi!");
            return;
        }

        // Validasi password match
        if (!password.equals(confirmPassword)) {
            showErrorMessage("Password dan konfirmasi password tidak sama!");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        // Validasi panjang password
        if (password.length() < 6) {
            showErrorMessage("Password minimal 6 karakter!");
            return;
        }



        try {
            // Cek apakah username sudah ada
            if (userDAO.isUsernameExists(username)) {
                showErrorMessage("Username sudah digunakan! Pilih username lain.");
                usernameField.requestFocus();
                return;
            }

            // Buat user baru
            User newUser = new User(name, username, password);
            newUser.setRole(role);

            // Simpan ke database
            int userId = userDAO.insertUser(newUser);

            if (userId > 0) {
                showSuccessMessage("Registrasi berhasil! Silakan login dengan akun baru Anda.");

                // Clear form fields
                clearForm();

                // Kembali ke login form
                goBackToLogin();
            } else {
                showErrorMessage("Gagal membuat akun. Silakan coba lagi.");
            }

        } catch (Exception ex) {
            showErrorMessage("Terjadi kesalahan: " + ex.getMessage());
        }
    }

    /// Membersihkan semua field form
    private void clearForm() {
        nameField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    /// Kembali ke form login
    private void goBackToLogin() {
        parentForm.showLoginForm();
        dispose();
    }

    /// Menampilkan pesan error
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /// Menampilkan pesan sukses
    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
