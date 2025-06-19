package kasirin.ui;

import kasirin.data.model.Product;
import kasirin.service.ProductService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductManagementForm extends JFrame {
    private ProductService productService;
    private JTable productTable;

    public ProductManagementForm() {
        productService = new ProductService();
        setTitle("Manajemen Produk");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Data produk
        List<Product> products = productService.getAllProducts();
        String[] columnNames = {"ID", "Nama Produk", "Harga"};
        String[][] data = new String[products.size()][3];

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            data[i][0] = String.valueOf(p.getId());
            data[i][1] = p.getName();
            data[i][2] = String.valueOf(p.getBasePrice());
        }

        productTable = new JTable(data, columnNames);
        add(new JScrollPane(productTable), BorderLayout.CENTER);
    }
}
