package kasirin.ui;

import kasirin.data.model.Store;
import kasirin.data.model.User;
import kasirin.service.StoreService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/// Form untuk membuat toko baru
/// @author yamaym
public class CreateStoreForm extends JFrame {
    // Komponen UI
    private JTextField nameField;
    private JTextField typeField;
    private JTextArea addressArea;
    private JButton createButton;
    private JButton cancelButton;

    // Data dan services
    private User currentUser;
    private StoreService storeService;
    private IndexPage parentPage;

    public CreateStoreForm(User user, IndexPage parent) {
        this.currentUser = user;
        this.parentPage = parent;
        this.storeService = new StoreService();

        initializeComponents();
        setupLayout();
        setupEventHandlers();

        // Konfigurasi window
        setTitle("Buat Toko Baru - Kasirin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /// Inisialisasi komponen UI dengan styling modern
    private void initializeComponents() {
        // Field nama toko
        nameField = createStyledTextField();

        // Field tipe toko
        typeField = createStyledTextField();

        // Text area untuk alamat
        addressArea = new JTextArea(5, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        addressArea.setBackground(new Color(249, 250, 251));

        // Tombol buat toko dengan gradien hijau
        createButton = new JButton("Buat Toko") {
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
        createButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createButton.setForeground(Color.WHITE);
        createButton.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        createButton.setFocusPainted(false);
        createButton.setContentAreaFilled(false);
        createButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tombol batal dengan desain outline
        cancelButton = new JButton("Batal") {
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
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setForeground(new Color(75, 85, 99));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        cancelButton.setFocusPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /// Helper method untuk membuat text field dengan styling modern
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

    /// Setup layout form dengan desain modern
    private void setupLayout() {
        // Background dengan gradien halus
        JPanel backgroundPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradien background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        0, getHeight(), new Color(248, 250, 252)
                );
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        // Panel utama dengan shadow dan rounded corners
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

                // Background putih
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;

        // Header dengan styling yang menarik
        JLabel titleLabel = new JLabel("Buat Toko Baru");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(17, 24, 39));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 25, 0);
        mainPanel.add(titleLabel, gbc);

        // Reset untuk form fields
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 4, 0);

        // Form fields dengan spacing yang optimal
        addFormField(mainPanel, gbc, "Nama Toko", nameField, 1);
        addFormField(mainPanel, gbc, "Tipe Toko", typeField, 2);

        // Alamat dengan text area
        JLabel addressLabel = new JLabel("Alamat");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressLabel.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(addressLabel, gbc);

        // Scroll pane untuk text area alamat
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add(addressScrollPane, gbc);

        // Panel untuk tombol dengan layout FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(20, 0, 0, 0);
        mainPanel.add(buttonPanel, gbc);

        // Wrapper untuk centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(mainPanel);

        backgroundPanel.add(wrapperPanel, BorderLayout.CENTER);
        add(backgroundPanel);
    }

    /// Helper method untuk menambahkan field ke form
    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int row) {
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(55, 65, 81));
        gbc.gridx = 0; gbc.gridy = row * 2 - 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        // Field
        gbc.gridy = row * 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);

        // Reset insets untuk label berikutnya
        gbc.insets = new Insets(10, 0, 4, 0);
    }

    /// Setup event handlers
    private void setupEventHandlers() {
        // Handler untuk tombol buat toko
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createStore();
            }
        });

        // Handler untuk tombol batal
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /// Proses pembuatan toko baru
    private void createStore() {
        // Validasi input
        String name = nameField.getText().trim();
        String type = typeField.getText().trim();
        String address = addressArea.getText().trim();

        if (name.isEmpty()) {
            showErrorMessage("Nama toko tidak boleh kosong!");
            nameField.requestFocus();
            return;
        }

        // Buat objek Store
        Store newStore = new Store(name, type, address);

        try {
            // Simpan ke database dan link dengan user
            int storeId = storeService.createStore(newStore, currentUser.getId());

            if (storeId > 0) {
                showSuccessMessage("Toko berhasil dibuat!");

                // Refresh daftar toko di IndexPage
                parentPage.refreshStoreList();

                // Tutup form
                dispose();
            } else if (storeId == -2) {
                showErrorMessage("Toko berhasil dibuat tetapi gagal menghubungkan dengan user!");
            } else {
                showErrorMessage("Gagal membuat toko. Silakan coba lagi.");
            }
        } catch (Exception ex) {
            showErrorMessage("Terjadi kesalahan: " + ex.getMessage());
        }
    }

    /// Helper methods untuk menampilkan pesan
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Sukses",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
