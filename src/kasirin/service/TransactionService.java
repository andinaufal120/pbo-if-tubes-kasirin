package kasirin.service;

import kasirin.data.model.*;
import kasirin.data.dao.*;
import java.sql.*;
import java.util.List;
public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final TransactionDetailDAO transactionDetailDAO;
    private final ProductVariationDAO productVariationDAO;
    private final DatabaseConnection dbConnection;

    public TransactionService() {
        this.dbConnection = new DatabaseConnection();
        this.transactionDAO = new TransactionDAO(dbConnection);
        this.transactionDetailDAO = new TransactionDetailDAO(dbConnection);
        this.productVariationDAO = new ProductVariationDAO(dbConnection);
    }

    /**
     * Memproses transaksi lengkap dari awal sampai akhir
     * @param items List item yang dibeli
     * @param user User yang melakukan transaksi
     * @param store Toko tempat transaksi dilakukan
     * @param totalAmount Total jumlah transaksi
     * @return true jika transaksi berhasil, false jika gagal
     */
    public boolean processCompleteTransaction(List<TransactionItem> items, User user, Store store, double totalAmount) {
        Connection conn = null;
        try {
            // 1. Dapatkan koneksi database dan mulai transaksi
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false);

            // 2. Buat record transaksi utama
            Transaction transaction = new Transaction(
                    store.getId(),
                    user.getId(),
                    new Timestamp(System.currentTimeMillis()),
                    totalAmount
            );

            int transactionId = transactionDAO.insertTransaction(transaction, conn);
            if (transactionId <= 0) {
                throw new SQLException("Failed to insert transaction");
            }

            // 3. Proses setiap item dalam transaksi
            for (TransactionItem item : items) {
                // Buat detail transaksi
                TransactionDetail detail = new TransactionDetail(
                        transactionId,
                        item.getVariationId(),
                        item.getQuantity(),
                        item.getPrice()
                );

                // Simpan detail transaksi
                transactionDetailDAO.insertTransactionDetail(detail, conn);

                // Kurangi stok produk
                productVariationDAO.reduceStock(
                        item.getVariationId(),
                        item.getQuantity(),
                        conn
                );
            }

            // 4. Commit transaksi jika semua berhasil
            conn.commit();
            return true;

        } catch (SQLException e) {
            // Rollback jika terjadi error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            System.err.println("Transaction failed: " + e.getMessage());
            return false;
        } finally {
            // Pastikan koneksi ditutup
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Mendapatkan riwayat transaksi berdasarkan toko
     * @param storeId ID toko
     * @return List transaksi
     */
    public List<Transaction> getTransactionHistory(int storeId) {
        return transactionDAO.getTransactionsByStore(storeId);
    }

    /**
     * Mendapatkan detail transaksi
     * @param transactionId ID transaksi
     * @return List detail transaksi
     */
    public List<TransactionDetail> getTransactionDetails(int transactionId) {
        return transactionDetailDAO.getDetailsByTransaction(transactionId);
    }
}