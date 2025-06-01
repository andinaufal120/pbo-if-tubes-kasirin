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

    /// Inisialisasi semua komponen UI dengan styling modern
    private void initializeComponents() {
        // Label selamat datang dengan nama user
        welcomeLabel = new JLabel("Selamat Datang di Kasirin!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        welcomeLabel.setForeground(new Color(31, 41, 55)); // Gray-800
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Label informasi user yang sedang login
        String userInfo = String.format("Masuk sebagai: %s (%s) - Role: %s",
                currentUser.getName(),
                currentUser.getUsername(),
                currentUser.getRole().getValue().toUpperCase()
        );
        userInfoLabel = new JLabel(userInfo);
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userInfoLabel.setForeground(new Color(107, 114, 128)); // Gray-500
        userInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel untuk menu utama
        menuPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Tombol logout di header
        logoutButton = new JButton("Keluar");
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutButton.setBackground(new Color(239, 68, 68)); // Red-500
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Buat menu cards berdasarkan role user
        createMenuCards();
    }

    /// Membuat kartu menu berdasarkan role user
    private void createMenuCards() {
        // Menu yang tersedia untuk semua role
        addMenuCard("Produk", "Kelola produk dan variasi", new Color(59, 130, 246), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProductManagement();
            }
        });

        addMenuCard("Transaksi", "Proses penjualan dan kasir", new Color(34, 197, 94), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTransactionManagement();
            }
        });

        addMenuCard("Laporan", "Lihat laporan penjualan", new Color(168, 85, 247), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openReports();
            }
        });

        // Menu khusus untuk admin dan owner
        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.OWNER) {
            addMenuCard("Pengguna", "Kelola akun pengguna", new Color(245, 158, 11), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openUserManagement();
                }
            });

            addMenuCard("Toko", "Kelola informasi toko", new Color(239, 68, 68), new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openStoreManagement();
                }
            });
        }

        // Menu pengaturan untuk semua user
        addMenuCard("Pengaturan", "Pengaturan akun dan aplikasi", new Color(107, 114, 128), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettings();
            }
        });
    }

    /// Helper method untuk membuat kartu menu dengan styling konsisten
    private void addMenuCard(String title, String description, Color bgColor, ActionListener action) {
        // Panel utama kartu
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1),
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel konten dengan layout vertikal
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bgColor);

        // Judul kartu
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Deskripsi kartu
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(new Color(255, 255, 255, 200));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tambahkan komponen ke panel konten
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(descLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        // Event handler untuk klik kartu
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Efek hover - sedikit lebih gelap
                card.setBackground(bgColor.darker());
                contentPanel.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Kembalikan warna asli
                card.setBackground(bgColor);
                contentPanel.setBackground(bgColor);
            }
        });

        menuPanel.add(card);
    }

    /// Setup layout utama aplikasi
    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header panel dengan informasi user dan tombol logout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Panel kiri header dengan info user
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.add(userInfoLabel);

        // Panel kanan header dengan tombol logout
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.add(logoutButton);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Panel utama dengan welcome message dan menu
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(249, 250, 251)); // Gray-50

        // Panel welcome dengan padding
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(new Color(249, 250, 251));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Tambahkan komponen ke layout utama
        add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
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
