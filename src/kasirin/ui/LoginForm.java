package kasirin.ui;

import kasirin.data.dao.DAOFactory;
import kasirin.data.dao.MySqlUserDAO;
import kasirin.data.model.User;
import kasirin.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/// Form login yang modern dan sederhana untuk autentikasi pengguna
/// @author yamaym
public class LoginForm extends JFrame {

    // Komponen UI utama
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserService userService;

    public LoginForm() {
        this.userService = new UserService();
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setTitle("Kasirin - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    /// Inisialisasi semua komponen UI dengan desain modern yang lebih menarik
    private void initializeComponents() {
        // Field input username dengan desain modern dan rounded corners
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        usernameField.setBackground(new Color(249, 250, 251));

        // Field input password dengan styling yang konsisten
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        passwordField.setBackground(new Color(249, 250, 251));

        // Tombol login dengan gradien dan shadow effect
        loginButton = new JButton("Masuk") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        0, getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol register dengan desain outline modern
        registerButton = new JButton("Daftar Akun Baru") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background putih dengan border
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Border dengan warna abu-abu
                g2.setColor(new Color(209, 213, 219));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        registerButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        registerButton.setForeground(new Color(75, 85, 99));
        registerButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        registerButton.setFocusPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /// Setup layout dengan desain yang lebih modern dan spacing yang lebih baik
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Background dengan gradien halus
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien background dari putih ke abu-abu sangat muda
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        0, getHeight(), new Color(248, 250, 252)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Panel utama dengan shadow effect dan rounded corners
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 16, 16);

                // Background putih dengan rounded corners
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 0, 12, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Header dengan ikon dan styling yang lebih menarik
        JLabel titleLabel = new JLabel("Masuk ke Kasirin");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(17, 24, 39));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Subtitle dengan warna yang lebih soft
        JLabel subtitleLabel = new JLabel("Silakan masukkan kredensial Anda");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 35, 0);
        mainPanel.add(subtitleLabel, gbc);

        // Label dan field dengan spacing yang lebih baik
        gbc.gridwidth = 1;
        gbc.insets = new Insets(12, 0, 6, 0);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(new Color(55, 65, 81));
        gbc.gridy = 4;
        gbc.insets = new Insets(12, 0, 6, 0);
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 30, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);

        // Tombol dengan spacing yang lebih baik
        gbc.gridy = 6;
        gbc.insets = new Insets(15, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(8, 0, 0, 0);
        mainPanel.add(registerButton, gbc);

        // Wrapper untuk centering dengan padding yang lebih besar
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
        wrapperPanel.add(mainPanel);

        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /// Setup event handlers untuk tombol dan keyboard
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            userService.performLogin(this, username, password, this);
        });

        registerButton.addActionListener(e -> {
            RegisterForm registerForm = new RegisterForm(this);
            registerForm.setVisible(true);
            setVisible(false);
        });

        passwordField.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            userService.performLogin(this, username, password, this);
        });

        usernameField.addActionListener(e -> passwordField.requestFocus());
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
