package kasirin.ui;

import kasirin.data.model.User;
import kasirin.data.model.Role;
import kasirin.data.model.Store;
import kasirin.service.StoreService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

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
    private JButton createStoreButton;
    private JPanel storeListPanel;

    // Services
    private StoreService storeService;

    public IndexPage(User user) {
        this.currentUser = user;
        this.storeService = new StoreService();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadStores();

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
        menuPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Panel untuk daftar toko
        storeListPanel = new JPanel();
        storeListPanel.setLayout(new BoxLayout(storeListPanel, BoxLayout.Y_AXIS));
        storeListPanel.setOpaque(false);
        storeListPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

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

        // Tombol buat toko dengan desain modern
        createStoreButton = new JButton("Buat Toko Baru") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien hijau untuk tombol buat toko
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
        createStoreButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createStoreButton.setForeground(Color.WHITE);
        createStoreButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        createStoreButton.setFocusPainted(false);
        createStoreButton.setContentAreaFilled(false);
        createStoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Buat menu cards dengan desain yang lebih modern
        createMenuCards();
    }

    /// Membuat kartu menu dengan desain modern dan shadow
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
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtonsPanel.setOpaque(false);
        headerButtonsPanel.add(createStoreButton);
        headerButtonsPanel.add(logoutButton);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);

        // Panel utama dengan welcome message, menu, dan daftar toko
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Panel welcome dengan spacing yang lebih baik
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Judul untuk daftar toko
        JLabel storeListTitle = new JLabel("Toko Anda");
        storeListTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        storeListTitle.setForeground(new Color(17, 24, 39));
        storeListTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        storeListTitle.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 0));

        // Tambahkan komponen ke panel utama
        mainPanel.add(welcomePanel);
        mainPanel.add(menuPanel);
        mainPanel.add(storeListTitle);
        mainPanel.add(storeListPanel);

        // Scroll pane untuk panel utama
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Tambahkan komponen ke layout utama
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);
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

        // Handler untuk tombol buat toko
        createStoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateStoreForm();
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

    /// Load daftar toko yang dimiliki/diakses oleh user
    private void loadStores() {
        try {
            // Ambil daftar toko dari database
            List<Store> stores = storeService.getStoresByUserId(currentUser.getId());

            // Clear panel
            storeListPanel.removeAll();

            if (stores.isEmpty()) {
                // Tampilkan pesan jika tidak ada toko
                JLabel emptyLabel = new JLabel("Anda belum memiliki toko. Klik tombol 'Buat Toko Baru' untuk membuat toko.");
                emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                emptyLabel.setForeground(new Color(107, 114, 128));
                emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                storeListPanel.add(emptyLabel);
            } else {
                // Tampilkan daftar toko
                for (Store store : stores) {
                    storeListPanel.add(createStoreCard(store));
                    storeListPanel.add(Box.createVerticalStrut(15)); // Spacing antar kartu
                }
            }

            // Refresh UI
            storeListPanel.revalidate();
            storeListPanel.repaint();

        } catch (Exception e) {
            showErrorMessage("Error loading stores: " + e.getMessage());
        }
    }

    /// Membuat kartu untuk menampilkan informasi toko
    private JPanel createStoreCard(final Store store) {
        // Panel utama kartu dengan shadow dan rounded corners
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(3, 3, getWidth()-3, getHeight()-3, 12, 12);
                g2.setColor(new Color(0, 0, 0, 5));
                g2.fillRoundRect(2, 2, getWidth()-2, getHeight()-2, 12, 12);

                // Background putih
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                g2.dispose();
            }
        };
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel untuk informasi toko
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Nama toko
        JLabel nameLabel = new JLabel(store.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(17, 24, 39));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tipe toko
        JLabel typeLabel = new JLabel("Tipe: " + (store.getType() != null && !store.getType().isEmpty() ? store.getType() : "N/A"));
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeLabel.setForeground(new Color(107, 114, 128));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Alamat toko (truncated jika terlalu panjang)
        String addressText = store.getAddress();
        if (addressText != null && addressText.length() > 100) {
            addressText = addressText.substring(0, 97) + "...";
        }
        JLabel addressLabel = new JLabel("Alamat: " + (addressText != null && !addressText.isEmpty() ? addressText : "N/A"));
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressLabel.setForeground(new Color(107, 114, 128));
        addressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tambahkan komponen ke panel info
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(typeLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(addressLabel);

        // Panel untuk tombol aksi
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);

        // Tombol kelola toko
        JButton manageButton = new JButton("Kelola") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien biru
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(59, 130, 246),
                        0, getHeight(), new Color(37, 99, 235)
                );
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        manageButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        manageButton.setForeground(Color.WHITE);
        manageButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        manageButton.setFocusPainted(false);
        manageButton.setContentAreaFilled(false);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Event handler untuk tombol kelola
        manageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStoreManagement(store);
            }
        });

        actionPanel.add(manageButton);

        // Tambahkan panel ke kartu
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(18, 23, 22, 27));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
            }
        });

        return card;
    }

    /// Buka form pembuatan toko baru
    private void openCreateStoreForm() {
        CreateStoreForm storeForm = new CreateStoreForm(currentUser, this);
        storeForm.setVisible(true);
    }

    /// Refresh daftar toko (dipanggil setelah membuat toko baru)
    public void refreshStoreList() {
        loadStores();
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

    private void openStoreManagement(Store store) {
        showInfoMessage("Kelola Toko: " + store.getName(), "Fitur kelola toko akan segera tersedia!");
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
