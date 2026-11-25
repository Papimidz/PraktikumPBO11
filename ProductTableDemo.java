import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProductTableDemo extends JFrame {

    public ProductTableDemo() {
        setTitle("Product Table Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        List<Product> products = new ArrayList<>();

        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));

        DefaultTableModel productModel = new DefaultTableModel(
            new Object[]{"ID", "Nama Produk", "Harga (Rp)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Product p : products) {
            productModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getPrice()
            });
        }

        JTable productTable = new JTable(productModel);

        JLabel selectedLabel = new JLabel("Pilih produk dari tabel");

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();

                if (row >= 0) {
                    Product selected = products.get(row);

                    selectedLabel.setText(
                        "Dipilih: " + selected.getId() +
                        " - " + selected.getName() +
                        " (Rp" + selected.getPrice() + ")"
                    );
                }
            }
        });

        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(selectedLabel, BorderLayout.SOUTH);

        setSize(600, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProductTableDemo::new);
    }
}
