import javax.swing.*;
import java.awt.*;

public class ComponentDemo extends JFrame {

    public ComponentDemo() {
        setTitle("Input Component Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField nameField = new JTextField(15);

        JComboBox<String> deptCombo = new JComboBox<>(
            new String[]{"Informatika", "Elektronika", "Mesin"}
        );

        JCheckBox activeCheck = new JCheckBox("Mahasiswa aktif", true);

        JRadioButton regulerRadio = new JRadioButton("Reguler", true);
        JRadioButton ekstensiRadio = new JRadioButton("Ekstensi");

        ButtonGroup group = new ButtonGroup();
        group.add(regulerRadio);
        group.add(ekstensiRadio);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(regulerRadio);
        radioPanel.add(ekstensiRadio);

        JTextArea outputArea = new JTextArea(6, 25);
        outputArea.setEditable(false);

        JButton showButton = new JButton("Tampilkan");

        showButton.addActionListener(e -> {

            if (nameField.getText().isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nama wajib diisi.",
                    "Input tidak valid",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String nama = nameField.getText();
            String jurusan = (String) deptCombo.getSelectedItem();
            boolean aktif = activeCheck.isSelected();
            String kelas = regulerRadio.isSelected()
                ? "Reguler"
                : "Ekstensi";

            outputArea.setText(
                "Nama: " + nama +
                "\nJurusan: " + jurusan +
                "\nAktif: " + aktif +
                "\nKelas: " + kelas
            );
        });


        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Jurusan:"));
        formPanel.add(deptCombo);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(activeCheck);

        formPanel.add(new JLabel("Kelas:"));
        formPanel.add(radioPanel);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        add(showButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ComponentDemo::new);
    }
}
