package kasirin.ui;

import java.util.UUID;
import java.util.List;
import kasirin.data.model.TransactionDetail;

public class Transaction {
    private List<TransactionDetail> details;

    // Constructor
    public Transaction(List<TransactionDetail> details) {
        this.details = details;
    }

    // Generate kode transaksi unik
    public static String generateTransactionCode() {
        return "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Hitung total transaksi dari detail
    public static double calculateTotal(List<TransactionDetail> details) {
        double total = 0;
        for (TransactionDetail detail : details) {
            total += detail.getQuantity() * detail.getPricePerUnit();
        }
        return total;
    }

    // Getter for details
    public List<TransactionDetail> getDetails() {
        return details;
    }

    // getTotal method
    public double getTotal() {
        return calculateTotal(this.details);
    }

    // Validasi transaksi (misal: total tidak negatif)
    public static boolean isValid(Transaction transaction) {
        return transaction.getTotal() >= 0;
    }
}