package kasirin.ui;

import kasirin.data.model.User;
import kasirin.data.model.Role;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/// Halaman utama aplikasi setelah user berhasil login
/// Menampilkan dashboard dan menu navigasi utama
/// @author yamaym
public class IndexPage extends JFrame {
    // Data user yang sedang login
    private User currentUser;

    // Komponen UI utama
    private JLabel welcomeLabel;
    private JLabel userInfoLabel;
    private JPanel menuPanel;
    private JButton logoutButton;

    public IndexPage(User user) {
        this.currentUser = user;

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Konfigurasi window utama
        setTitle("Kasirin - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setLocationRelativeTo(null);
    }

    /// Inisialisasi semua komponen UI dengan desain modern yang lebih menarik
    private void initializeComponents() {
        // Label selamat datang dengan typography yang lebih baik
        welcomeLabel = new JLabel("Selamat Datang di Kasirin!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(17, 24, 39));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Label informasi user dengan styling yang lebih soft
        String userInfo = String.format("Masuk sebagai: %s (%s) - Role: %s",
                currentUser.getName(),
                currentUser.getUsername(),
                currentUser.getRole().getValue().toUpperCase()
        );
        userInfoLabel = new JLabel(userInfo);
        userInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userInfoLabel.setForeground(new Color(107, 114, 128));
        userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel untuk menu dengan grid yang lebih responsif
        menuPanel = new JPanel(new GridLayout(2, 3, 25, 25));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Tombol logout dengan desain modern
        logoutButton = new JButton("Keluar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien merah untuk logout
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(239, 68, 68),
                        0, getHeight(), new Color(220, 38, 38)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


    }

    /// Helper method untuk membuat kartu menu dengan desain modern dan shadow
    private void addMenuCard(String title, String description, Color bgColor, ActionListener action) {
        // Panel utama kartu dengan shadow dan rounded corners
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect dengan multiple layers
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(6, 6, getWidth()-6, getHeight()-6, 16, 16);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 16, 16);
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 16, 16);

                // Gradien background untuk kartu
                GradientPaint gradient = new GradientPaint(
                        0, 0, bgColor,
                        0, getHeight(), bgColor.darker()
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.dispose();
            }
        };
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));

        // Panel konten dengan layout yang lebih baik
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Ikon placeholder (bisa diganti dengan ikon sesungguhnya)
        JLabel iconLabel = new JLabel("●");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        iconLabel.setForeground(new Color(255, 255, 255, 180));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Judul kartu dengan typography yang lebih baik
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Deskripsi dengan line height yang lebih baik
        JLabel descLabel = new JLabel("<html><div style='text-align: center; line-height: 1.4;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan komponen dengan spacing yang optimal
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(descLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Event handler dengan efek hover yang lebih smooth
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Efek hover dengan transformasi subtle
                card.setBorder(BorderFactory.createEmptyBorder(32, 27, 38, 33));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Kembalikan ke ukuran normal
                card.setBorder(BorderFactory.createEmptyBorder(35, 30, 35, 30));
            }
        });

        menuPanel.add(card);
    }

    /// Setup layout utama aplikasi dengan desain yang lebih modern
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Background dengan gradien yang lebih halus
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien background yang lebih sophisticated
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(248, 250, 252),
                        0, getHeight(), new Color(241, 245, 249)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Header panel dengan desain yang lebih modern
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background putih dengan shadow halus
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Shadow di bagian bawah header
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.setColor(new Color(0, 0, 0, 4));
                g2.fillRect(0, getHeight()-1, getWidth(), 1);

                g2.dispose();
            }
        };
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // Panel kiri header dengan info user
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setOpaque(false);
        userInfoPanel.add(userInfoLabel);

        // Panel kanan header dengan tombol logout
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Panel utama dengan welcome message dan menu
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Panel welcome dengan spacing yang lebih baik
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Tambahkan komponen ke layout utama
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);
        add(backgroundPanel, BorderLayout.CENTER);
    }

    /// Setup event handlers untuk tombol dan menu
    private void setupEventHandlers() {
        // Handler untuk tombol logout
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogout();
            }
        });

        // Handler untuk window closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                performLogout();
            }
        });
    }

    /// Proses logout dan kembali ke login form
    private void performLogout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin keluar?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // Tutup window saat ini
            dispose();

            // Buka kembali login form
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new LoginForm().setVisible(true);
                }
            });
        }
    }

    // Method untuk membuka berbagai modul aplikasi
    // TODO: Implementasi akan ditambahkan sesuai kebutuhan

    private void openProductManagement() {
        showInfoMessage("Modul Manajemen Produk", "Fitur manajemen produk akan segera tersedia!");
    }

    private void openTransactionManagement() {
        showInfoMessage("Modul Transaksi", "Fitur kasir dan transaksi akan segera tersedia!");
    }

    private void openReports() {
        showInfoMessage("Modul Laporan", "Fitur laporan penjualan akan segera tersedia!");
    }

    private void openUserManagement() {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER) {
            showInfoMessage("Manajemen Pengguna", "Fitur manajemen pengguna akan segera tersedia!");
        } else {
            showErrorMessage("Akses ditolak! Anda tidak memiliki izin untuk mengakses fitur ini.");
        }
    }

    private void openStoreManagement() {
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER) {
            showInfoMessage("Manajemen Toko", "Fitur manajemen toko akan segera tersedia!");
        } else {
            showErrorMessage("Akses ditolak! Anda tidak memiliki izin untuk mengakses fitur ini.");
        }
    }

    private void openSettings() {
        showInfoMessage("Pengaturan", "Fitur pengaturan akan segera tersedia!");
    }

    /// Helper methods untuk menampilkan pesan
    private void showInfoMessage(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /// Getter untuk current user (untuk digunakan modul lain)
    public User getCurrentUser() {
        return currentUser;
    }
}
