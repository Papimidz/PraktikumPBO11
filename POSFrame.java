import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

public class POSFrame extends JFrame {

    private final List<Product> products = new ArrayList<>();
    private final List<CartItem> cart = new ArrayList<>();

    private JTable productTable;
    private JTable cartTable;

    private JTextField qtyField;
    private JLabel selectedLabel;
    private JLabel totalLabel;
    private JLabel pointsLabel;

    private JTextArea receiptArea;

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    public POSFrame() {
        setTitle("POIN Off-Sales - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initProductData();
        initComponents();
        bindEvents();

        setSize(1000, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initProductData() {
        products.add(new Product("P001", "Air Mineral 600ml", 3000));
        products.add(new Product("P002", "Kopi Sachet", 5000));
        products.add(new Product("P003", "Roti isi", 8000));
        products.add(new Product("P004", "Snack Keripik", 6000));
        products.add(new Product("P005", "Minuman Botol", 12000));
    }

    private void initComponents() {

        setLayout(new BorderLayout(8, 8));

        createMenu();

        productModel = new DefaultTableModel(
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

        productTable = new JTable(productModel);
        productTable.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        productTable.setRowHeight(24);

        selectedLabel = new JLabel("Belum ada produk dipilih");

        qtyField = new JTextField("1", 5);

        JButton addButton = new JButton("Add to Cart");

        JPanel productBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        productBottom.add(selectedLabel);
        productBottom.add(new JLabel("Qty:"));
        productBottom.add(qtyField);
        productBottom.add(addButton);

        JPanel productPanel = new JPanel(new BorderLayout(5, 5));
        productPanel.setBorder(
            BorderFactory.createTitledBorder("Produk")
        );

        productPanel.add(
            new JScrollPane(productTable),
            BorderLayout.CENTER
        );

        productPanel.add(productBottom, BorderLayout.SOUTH);

        cartModel = new DefaultTableModel(
            new Object[]{
                "ID", "Nama Produk", "Qty", "Harga", "Subtotal"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(24);

        totalLabel = new JLabel("Total: Rp0");
        pointsLabel = new JLabel("Points: 0");

        JButton checkoutButton = new JButton("Checkout");
        JButton printButton = new JButton("Cetak");

        JPanel summaryPanel = new JPanel(new GridLayout(0, 1, 3, 3));

        summaryPanel.add(totalLabel);
        summaryPanel.add(pointsLabel);
        summaryPanel.add(checkoutButton);
        summaryPanel.add(printButton);

        receiptArea = new JTextArea(8, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(
            BorderFactory.createTitledBorder("Struk")
        );

        receiptPanel.add(
            new JScrollPane(receiptArea),
            BorderLayout.CENTER
        );

        JPanel cartBottom = new JPanel(new BorderLayout(5, 5));

        cartBottom.add(summaryPanel, BorderLayout.NORTH);
        cartBottom.add(receiptPanel, BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBorder(
            BorderFactory.createTitledBorder("Keranjang")
        );

        cartPanel.add(
            new JScrollPane(cartTable),
            BorderLayout.CENTER
        );

        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(
            new GridLayout(1, 2, 8, 0)
        );

        mainPanel.add(productPanel);
        mainPanel.add(cartPanel);

        add(mainPanel, BorderLayout.CENTER);

        addButton.addActionListener(
            e -> addSelectedProductToCart()
        );

        checkoutButton.addActionListener(
            e -> checkout()
        );

        printButton.addActionListener(
            e -> printReceipt()
        );
    }

    private void bindEvents() {

        productTable.getSelectionModel()
            .addListSelectionListener(e -> {

                if (!e.getValueIsAdjusting()) {

                    int row = productTable.getSelectedRow();

                    if (row >= 0) {

                        Product selected = products.get(row);

                        selectedLabel.setText(
                            "Dipilih: " + selected.getId()
                            + " - " + selected.getName()
                            + " (Rp" + selected.getPrice() + ")"
                        );
                    }
                }
            });
    }

    private void addSelectedProductToCart() {

        int row = productTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Silakan pilih produk terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int qty;

        try {
            qty = Integer.parseInt(qtyField.getText().trim());

            if (qty <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Qty harus berupa angka lebih dari 0.",
                "Input tidak valid",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Product selected = products.get(row);

        CartItem item = new CartItem(selected, qty);

        cart.add(item);

        refreshCartTable();
        updateSummary();

        qtyField.setText("1");
    }

    private void refreshCartTable() {

        cartModel.setRowCount(0);

        for (CartItem item : cart) {

            Product p = item.getProduct();

            cartModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                item.getQty(),
                p.getPrice(),
                item.getSubtotal()
            });
        }
    }

    private void updateSummary() {

        int total = 0;

        for (CartItem item : cart) {
            total += item.getSubtotal();
        }

        int points = total / 1000;

        totalLabel.setText(
            "Total: Rp" + String.format("%,d", total)
        );

        pointsLabel.setText(
            "Points: " + points
        );
    }

    private void checkout() {

        if (cart.isEmpty()) {

            JOptionPane.showMessageDialog(
                this,
                "Keranjang masih kosong.",
                "Checkout gagal",
                JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        StringBuilder receipt = new StringBuilder();

        receipt.append("===== POIN OFF-SALES =====\n");
        receipt.append("Toko: Demo Toko\n");
        receipt.append("==========================\n");

        int total = 0;

        for (CartItem item : cart) {

            Product p = item.getProduct();

            receipt.append(
                p.getId() + " - " + p.getName() + "\n"
            );

            receipt.append(
                "Qty: " + item.getQty()
                + " x Rp" + p.getPrice() + "\n"
            );

            receipt.append(
                "Subtotal: Rp" + item.getSubtotal() + "\n\n"
            );

            total += item.getSubtotal();
        }

        int points = total / 1000;

        receipt.append("==========================\n");
        receipt.append(
            "TOTAL: Rp" + String.format("%,d", total) + "\n"
        );

        receipt.append("POINTS DIDAPAT: " + points + "\n");
        receipt.append("==========================\n");
        receipt.append("Terima kasih! Silakan berkunjung kembali.");

        receiptArea.setText(receipt.toString());

        cart.clear();

        refreshCartTable();
        updateSummary();
    }

    private void printReceipt() {

        if (receiptArea.getText().isBlank()) {

            JOptionPane.showMessageDialog(
                this,
                "Belum ada struk yang dapat dicetak."
            );

            return;
        }

        try {
            receiptArea.print();

        } catch (PrinterException ex) {

            JOptionPane.showMessageDialog(
                this,
                "Gagal mencetak: " + ex.getMessage()
            );
        }
    }

    private void createMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        exitItem.addActionListener(e -> dispose());

        aboutItem.addActionListener(e ->
            JOptionPane.showMessageDialog(
                this,
                "POIN Off-Sales - Praktikum Java Swing"
            )
        );

        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
}
