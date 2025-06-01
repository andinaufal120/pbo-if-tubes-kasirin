//package kasirin.ui;
//
//import kasirin.data.dao.DAOFactory;
//import kasirin.data.dao.MySqlUserDAO;
//import kasirin.data.model.User;
//import kasirin.data.model.Role;
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.List;
//
///// Form untuk manajemen pengguna (CRUD operations)
///// Hanya dapat diakses oleh ADMIN dan OWNER
///// @author yamaym
//public class UserManagementForm extends JFrame {
//    // Komponen UI utama
//    private JTable userTable;
//    private DefaultTableModel tableModel;
//    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
//    private MySqlUserDAO userDAO;
//    private User currentUser;
//
//    // Kolom tabel
//    private final String[] columnNames = {"ID", "Nama", "Username", "Role", "Store ID"};
//
//    public UserManagementForm(User currentUser) {
//        this.currentUser = currentUser;
//
//        // Cek authorization
//        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.OWNER) {
//            JOptionPane.showMessageDialog(null, "Akses ditolak! Anda tidak memiliki izin.", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // Inisialisasi DAO
//        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
//        userDAO = (MySqlUserDAO) factory.getUserDAO();
//
//        initializeComponents();
//        setupLayout();
//        setupEventHandlers();
//        loadUserData();
//
//        // Konfigurasi window
//        setTitle("Manajemen Pengguna - Kasirin");
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setSize(800, 600);
//        setLocationRelativeTo(null);
//    }
//
//    /// Inisialisasi komponen UI
//    private void initializeComponents() {
//        // Inisialisasi tabel dengan model
//        tableModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false; // Tabel read-only
//            }
//        };
//
//        userTable = new JTable(tableModel);
//        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        userTable.setRowHeight(25);
//        userTable.getTableHeader().setReorderingAllowed(false);
//
//        // Tombol-tombol aksi
//        addButton = createStyledButton("Tambah User", new Color(34, 197, 94));
//        editButton = createStyledButton("Edit User", new Color(59, 130, 246));
//        deleteButton = createStyledButton("Hapus User", new Color(239, 68, 68));
//        refreshButton = createStyledButton("Refresh", new Color(107, 114, 128));
//        backButton = createStyledButton("Kembali", new Color(156, 163, 175));
//    }
//
//    /// Helper method untuk membuat tombol dengan styling konsisten
//    private JButton createStyledButton(String text, Color bgColor) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("SansSerif", Font.BOLD, 12));
//        button.setBackground(bgColor);
//        button.setForeground(Color.WHITE);
//        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
//        button.setFocusPainted(false);
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        return button;
//    }
//
//    /// Setup layout form
//    private void setupLayout() {
//        setLayout(new BorderLayout());
//
//        // Header panel
//        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        headerPanel.setBackground(Color.WHITE);
//        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
//        JLabel titleLabel = new JLabel("Manajemen Pengguna");
//        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
//        headerPanel.add(titleLabel);
//
//        // Tabel dengan scroll pane
//        JScrollPane scrollPane = new JScrollPane(userTable);
//        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
//        buttonPanel.setBackground(Color.WHITE);
//        buttonPanel.add(addButton);
//        buttonPanel.add(editButton);
//        buttonPanel.add(deleteButton);
//        buttonPanel.add(refreshButton);
//        buttonPanel.add(backButton);
//
//        // Tambahkan ke layout utama
//        add(headerPanel, BorderLayout.NORTH);
//        add(scrollPane, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    /// Setup event handlers
//    private void setupEventHandlers() {
//        addButton.addActionListener(e -> openAddUserDialog());
//        editButton.addActionListener(e -> openEditUserDialog());
//        deleteButton.addActionListener(e -> deleteSelectedUser());
//        refreshButton.addActionListener(e -> loadUserData());
//        backButton.addActionListener(e -> dispose());
//    }
//
//    /// Load data user ke tabel
//    private void loadUserData() {
//        try {
//            // Clear existing data
//            tableModel.setRowCount(0);
//
//            // Load users from database
//            List<User> users = userDAO.getAllUsers();
//
//            for (User user : users) {
//                Object[] rowData = {
//                        user.getId(),
//                        user.getName(),
//                        user.getUsername(),
//                        user.getRole().getValue().toUpperCase(),
//                        user.getStoreID()
//                };
//                tableModel.addRow(rowData);
//            }
//
//        } catch (Exception e) {
//            showErrorMessage("Error loading user data: " + e.getMessage());
//        }
//    }
//
//    /// Buka dialog untuk menambah user baru
//    private void openAddUserDialog() {
//        UserFormDialog dialog = new UserFormDialog(this, "Tambah User Baru", null);
//        dialog.setVisible(true);
//
//        if (dialog.isConfirmed()) {
//            User newUser = dialog.getUser();
//            try {
//                int userId = userDAO.insertUser(newUser);
//                if (userId > 0) {
//                    showSuccessMessage("User berhasil ditambahkan!");
//                    loadUserData();
//                } else {
//                    showErrorMessage("Gagal menambahkan user!");
//                }
//            } catch (Exception e) {
//                showErrorMessage("Error: " + e.getMessage());
//            }
//        }
//    }
//
//    /// Buka dialog untuk edit user
//    private void openEditUserDialog() {
//        int selectedRow = userTable.getSelectedRow();
//        if (selectedRow == -1) {
//            showErrorMessage("Pilih user yang akan diedit!");
//            return;
//        }
//
//        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
//        User user = userDAO.findUser(userId);
//
//        if (user != null) {
//            UserFormDialog dialog = new UserFormDialog(this, "Edit User", user);
//            dialog.setVisible(true);
//
//            if (dialog.isConfirmed()) {
//                User updatedUser = dialog.getUser();
//                try {
//                    int result = userDAO.updateUser(userId, updatedUser);
//                    if (result > 0) {
//                        showSuccessMessage("User berhasil diupdate!");
//                        loadUserData();
//                    } else {
//                        showErrorMessage("Gagal mengupdate user!");
//                    }
//                } catch (Exception e) {
//                    showErrorMessage("Error: " + e.getMessage());
//                }
//            }
//        }
//    }
//
//    /// Hapus user yang dipilih
//    private void deleteSelectedUser() {
//        int selectedRow = userTable.getSelectedRow();
//        if (selectedRow == -1) {
//            showErrorMessage("Pilih user yang akan dihapus!");
//            return;
//        }
//
//        int userId = (Integer) tableModel.getValueAt(selectedRow, 0);
//        String userName = (String) tableModel.getValueAt(selectedRow, 1);
//
//        // Konfirmasi penghapusan
//        int option = JOptionPane.showConfirmDialog(
//                this,
//                "Apakah Anda yakin ingin menghapus user '" + userName + "'?",
//                "Konfirmasi Hapus",
//                JOptionPane.YES_NO_OPTION,
//                JOptionPane.WARNING_MESSAGE
//        );
//
//        if (option == JOptionPane.YES_OPTION) {
//            try {
//                int result = userDAO.deleteUser(userId);
//                if (result > 0) {
//                    showSuccessMessage("User berhasil dihapus!");
//                    loadUserData();
//                } else {
//                    showErrorMessage("Gagal menghapus user!");
//                }
//            } catch (Exception e) {
//                showErrorMessage("Error: " + e.getMessage());
//            }
//        }
//    }
//
//    /// Helper methods untuk pesan
//    private void showErrorMessage(String message) {
//        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
//    }
//
//    private void showSuccessMessage(String message) {
//        JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
//    }
//}
//
///// Dialog form untuk input/edit user data
//class UserFormDialog extends JDialog {
//    private JTextField nameField, usernameField, storeIdField;
//    private JPasswordField passwordField;
//    private JComboBox<Role> roleComboBox;
//    private JButton saveButton, cancelButton;
//    private boolean confirmed = false;
//    private User user;
//    private boolean isEdit;
//
//    public UserFormDialog(Frame parent, String title, User existingUser) {
//        super(parent, title, true);
//        this.user = existingUser;
//        this.isEdit = (existingUser != null);
//
//        initializeComponents();
//        setupLayout();
//        setupEventHandlers();
//
//        if (isEdit) {
//            populateFields();
//        }
//
//        setSize(400, 350);
//        setLocationRelativeTo(parent);
//        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//    }
//
//    private void initializeComponents() {
//        nameField = new JTextField(20);
//        usernameField = new JTextField(20);
//        passwordField = new JPasswordField(20);
//        storeIdField = new JTextField(20);
//        roleComboBox = new JComboBox<>(Role.values());
//
//        saveButton = new JButton("Simpan");
//        saveButton.setBackground(new Color(34, 197, 94));
//        saveButton.setForeground(Color.WHITE);
//
//        cancelButton = new JButton("Batal");
//        cancelButton.setBackground(new Color(107, 114, 128));
//        cancelButton.setForeground(Color.WHITE);
//    }
//
//    private void setupLayout() {
//        setLayout(new BorderLayout());
//
//        JPanel formPanel = new JPanel(new GridBagLayout());
//        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Form fields
//        addFormField(formPanel, gbc, "Nama:", nameField, 0);
//        addFormField(formPanel, gbc, "Username:", usernameField, 1);
//        addFormField(formPanel, gbc, "Password:", passwordField, 2);
//        addFormField(formPanel, gbc, "Role:", roleComboBox, 3);
//        addFormField(formPanel, gbc, "Store ID:", storeIdField, 4);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout());
//        buttonPanel.add(saveButton);
//        buttonPanel.add(cancelButton);
//
//        add(formPanel, BorderLayout.CENTER);
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
//        gbc.gridx = 0; gbc.gridy = row;
//        panel.add(new JLabel(label), gbc);
//
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        panel.add(field, gbc);
//        gbc.fill = GridBagConstraints.NONE;
//    }
//
//    private void setupEventHandlers() {
//        saveButton.addActionListener(e -> saveUser());
//        cancelButton.addActionListener(e -> dispose());
//    }
//
//    private void populateFields() {
//        if (user != null) {
//            nameField.setText(user.getName());
//            usernameField.setText(user.getUsername());
//            // Don't populate password for security
//            roleComboBox.setSelectedItem(user.getRole());
//            storeIdField.setText(String.valueOf(user.getStoreID()));
//        }
//    }
//
//    private void saveUser() {
//        // Validation
//        String name = nameField.getText().trim();
//        String username = usernameField.getText().trim();
//        String password = new String(passwordField.getPassword());
//        String storeIdText = storeIdField.getText().trim();
//
//        if (name.isEmpty() || username.isEmpty() || storeIdText.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        if (!isEdit && password.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Password harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        try {
//            int storeId = Integer.parseInt(storeIdText);
//            Role role = (Role) roleComboBox.getSelectedItem();
//
//            if (isEdit) {
//                // Update existing user
//                user.setName(name);
//                user.setUsername(username);
//                if (!password.isEmpty()) {
//                    user.setPassword(password);
//                }
//                user.setRole(role);
//                user.setStoreID(storeId);
//            } else {
//                // Create new user
//                user = new User(storeId, name, username, password);
//                user.setRole(role);
//            }
//
//            confirmed = true;
//            dispose();
//
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(this, "Store ID harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }
//
//    public boolean isConfirmed() {
//        return confirmed;
//    }
//
//    public User getUser() {
//        return user;
//    }
//}
