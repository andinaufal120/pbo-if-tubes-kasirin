package kasirin.ui;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlUserDAO;
import kasirin.data.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/// Form login yang modern dan sederhana untuk autentikasi pengguna
/// @author yamaym
public class LoginForm extends JFrame {
    // Komponen UI utama
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private MySqlUserDAO userDAO;

    public LoginForm() {
        // Inisialisasi DAO untuk operasi database
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        userDAO = (MySqlUserDAO) factory.getUserDAO();

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Konfigurasi window utama
        setTitle("Kasirin - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null); // Center di layar
    }

    /// Inisialisasi semua komponen UI
    private void initializeComponents() {
        // Field input username dengan placeholder effect
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Field input password dengan styling yang sama
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Tombol login dengan styling modern
        loginButton = new JButton("Masuk");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBackground(new Color(59, 130, 246)); // Blue-500
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol register dengan styling secondary
        registerButton = new JButton("Daftar Akun Baru");
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(new Color(107, 114, 128)); // Gray-500
        registerButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /// Setup layout menggunakan GridBagLayout untuk kontrol yang lebih baik
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Panel utama dengan padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Header/Title
        JLabel titleLabel = new JLabel("Masuk ke Kasirin");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55)); // Gray-800
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Silakan masukkan kredensial Anda");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128)); // Gray-500
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Label dan field username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        usernameLabel.setForeground(new Color(55, 65, 81)); // Gray-700
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);

        // Label dan field password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passwordLabel.setForeground(new Color(55, 65, 81)); // Gray-700
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        // Tombol login
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        // Tombol register
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 0, 0);
        mainPanel.add(registerButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    /// Setup event handlers untuk tombol dan keyboard
    private void setupEventHandlers() {
        // Handler untuk tombol login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Handler untuk tombol register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterForm();
            }
        });

        // Enter key untuk login dari field password
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        // Enter key untuk pindah ke password field dari username
        usernameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                passwordField.requestFocus();
            }
        });
    }

    /// Melakukan proses login dan validasi
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Username dan password tidak boleh kosong!");
            return;
        }

        try {
            // Coba autentikasi user melalui DAO
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                // Login berhasil - buka halaman utama
                showSuccessMessage("Login berhasil! Selamat datang, " + user.getName());

                // Buka halaman utama aplikasi
                IndexPage indexPage = new IndexPage(user);
                indexPage.setVisible(true);

                // Tutup form login
                dispose();
            } else {
                // Login gagal
                showErrorMessage("Username atau password salah!");
                passwordField.setText(""); // Clear password field
                passwordField.requestFocus();
            }
        } catch (Exception ex) {
            showErrorMessage("Terjadi kesalahan saat login: " + ex.getMessage());
        }
    }

    /// Membuka form registrasi
    private void openRegisterForm() {
        RegisterForm registerForm = new RegisterForm(this);
        registerForm.setVisible(true);
        setVisible(false); // Sembunyikan login form
    }

    /// Menampilkan pesan error dengan styling yang konsisten
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

    /// Method untuk menampilkan kembali login form (dipanggil dari register form)
    public void showLoginForm() {
        setVisible(true);
    }

    // Main method untuk testing
    public static void main(String[] args) {
        // Set look and feel yang lebih modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}
