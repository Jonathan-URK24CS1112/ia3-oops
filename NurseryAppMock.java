package ia3_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class NurseryAppMock extends JFrame {

    private JTable plantTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double totalBill = 0.0;

    public NurseryAppMock() {
        // Window setup
        setTitle("Plant Nursery Management System (Mock Version)");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Header label
        JLabel header = new JLabel("Available Plant Products", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        mainPanel.add(header, BorderLayout.NORTH);

        // TABLE
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price ($)", "Stock"}, 0
        );
        plantTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(plantTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton refreshBtn = new JButton("🔄 Refresh");
        JButton buyBtn = new JButton("🛒 Buy Plant");
        totalLabel = new JLabel("Total: Rs 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        bottomPanel.add(refreshBtn);
        bottomPanel.add(buyBtn);
        bottomPanel.add(totalLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add everything to window
        add(mainPanel);

        // EVENT HANDLERS 
        refreshBtn.addActionListener(e -> loadPlantData());
        buyBtn.addActionListener(e -> handlePurchase());

        // Initial data load
        loadPlantData();
    }

    // Loads plant data
    private void loadPlantData() {
        tableModel.setRowCount(0);
        System.out.println("[DEBUG] Loading plant data..."); // TODO: Replace with actual DB logic later

        // Mock plant list
        Object[][] mockPlants = {
                {1, "Aloe Vera", "Succulent", 5.50, 20},
                {2, "Peace Lily", "Indoor", 8.99, 15},
                {3, "Rose", "Flowering", 12.75, 10},
                {4, "Snake Plant", "Indoor", 9.25, 12}
        };

        for (Object[] row : mockPlants) {
            tableModel.addRow(row);
        }

        System.out.println("[DEBUG] Finished loading mock data.");
    }

    // Buying a plant
    private void handlePurchase() {
        int selectedRow = plantTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plant to buy.");
            return;
        }

        String name = (String) tableModel.getValueAt(selectedRow, 1);
        double price = (double) tableModel.getValueAt(selectedRow, 3);
        int stock = (int) tableModel.getValueAt(selectedRow, 4);

        String qtyStr = JOptionPane.showInputDialog(this,
                "Enter quantity to purchase for " + name + ":", "1");
        if (qtyStr == null || qtyStr.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive!");
                return;
            }
            if (qty > stock) {
                JOptionPane.showMessageDialog(this, "Not enough stock available!");
                return;
            }

            double subtotal = price * qty;
            totalBill += subtotal;
            totalLabel.setText(String.format("Total: Rs.2f", totalBill));

            // Stock update
            System.out.println("[DEBUG] Updating stock for " + name + " (new stock = " + (stock - qty) + ")"); 
            System.out.println("[DEBUG] Will replace this with SQL UPDATE later.");

            JOptionPane.showMessageDialog(this,
                    "Purchased " + qty + " × " + name + " for Rs" + String.format("Rs.2f", subtotal));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NurseryAppMock app = new NurseryAppMock();
            app.setVisible(true);
        });
    }
}
