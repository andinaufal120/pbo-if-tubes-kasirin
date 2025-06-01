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
    private JTextField storeIdField;
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

    /// Inisialisasi semua komponen UI dengan styling modern
    private void initializeComponents() {
        // Field nama lengkap
        nameField = createStyledTextField();

        // Field username
        usernameField = createStyledTextField();

        // Field password
        passwordField = createStyledPasswordField();

        // Field konfirmasi password
        confirmPasswordField = createStyledPasswordField();

        // ComboBox untuk role
        roleComboBox = new JComboBox<>(Role.values());
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));


        // Tombol register
        registerButton = new JButton("Daftar");
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        registerButton.setBackground(new Color(34, 197, 94)); // Green-500
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol kembali
        backButton = new JButton("Kembali ke Login");
        backButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(107, 114, 128));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /// Helper method untuk membuat text field dengan styling konsisten
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    /// Helper method untuk membuat password field dengan styling konsisten
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    /// Setup layout form dengan GridBagLayout
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel utama dengan padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Header
        JLabel titleLabel = new JLabel("Daftar Akun Baru");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        mainPanel.add(titleLabel, gbc);

        // Reset untuk form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 3, 0);

        // Nama Lengkap
        addFormField(mainPanel, gbc, "Nama Lengkap:", nameField, 1);

        // Username
        addFormField(mainPanel, gbc, "Username:", usernameField, 2);

        // Password
        addFormField(mainPanel, gbc, "Password:", passwordField, 3);

        // Konfirmasi Password
        addFormField(mainPanel, gbc, "Konfirmasi Password:", confirmPasswordField, 4);

        // Role
        addFormField(mainPanel, gbc, "Role:", roleComboBox, 5);

        // Store ID

        // Tombol register
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(registerButton, gbc);

        // Tombol kembali
        gbc.gridy = 8;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(backButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    /// Helper method untuk menambahkan field ke form
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12)); // Changed from Font.MEDIUM to Font.BOLD
        label.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = row * 2 - 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        // Field
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);

        // Reset insets untuk label berikutnya
        gbc.insets = new Insets(8, 0, 3, 0);
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
                confirmPassword.isEmpty()) {
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

        // Validasi store ID adalah angka


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
