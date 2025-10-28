package oops_ia3_urk24cs1112;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NurseryAppMock extends JFrame {

    private JTable plantTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private double totalBill = 0.0;

    private static final String URL = "jdbc:postgresql://localhost:5432/nursery_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "jonathan3604"; 

    public NurseryAppMock() {
        setTitle("Plant Nursery Management System");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MAIN PANEL WITH EARTHY BACKGROUND
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(235, 245, 230));  // soft greenish background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // HEADER LABEL
        JLabel header = new JLabel("Available Plant Products", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setForeground(new Color(34, 85, 34));  // deep green
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        // TABLE
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Price (Rs)", "Stock"}, 0
        );
        plantTable = new JTable(tableModel);
        plantTable.setRowHeight(25);
        plantTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        plantTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        plantTable.getTableHeader().setBackground(new Color(200, 230, 200)); // light green header
        JScrollPane scrollPane = new JScrollPane(plantTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(180, 210, 180), 2));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(new Color(235, 245, 230));

        JButton refreshBtn = new JButton("Refresh");
        JButton buyBtn = new JButton("Buy Plant");
        JButton resetBtn = new JButton("Reset Inventory");

        totalLabel = new JLabel("Total: Rs 0.00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(new Color(50, 100, 50));

        Color buttonColor = new Color(120, 180, 120); // background
        Color textColor = Color.WHITE;                // text color for contrast

        // Refresh Button
        refreshBtn.setBackground(buttonColor);
        refreshBtn.setForeground(textColor);
        refreshBtn.setOpaque(true);
        refreshBtn.setContentAreaFilled(true);
        refreshBtn.setFocusPainted(false);

        // Buy Button
        buyBtn.setBackground(buttonColor);
        buyBtn.setForeground(textColor);
        buyBtn.setOpaque(true);
        buyBtn.setContentAreaFilled(true);
        buyBtn.setFocusPainted(false);

        // Reset Button (slightly lighter green)
        resetBtn.setBackground(new Color(160, 200, 160));
        resetBtn.setForeground(textColor);
        resetBtn.setOpaque(true);
        resetBtn.setContentAreaFilled(true);
        resetBtn.setFocusPainted(false);



        bottomPanel.add(refreshBtn);
        bottomPanel.add(buyBtn);
        bottomPanel.add(resetBtn);
        bottomPanel.add(totalLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        // EVENT HANDLERS
        refreshBtn.addActionListener(e -> loadPlantData());
        buyBtn.addActionListener(e -> handlePurchase());
        resetBtn.addActionListener(e -> resetInventory());

        loadPlantData();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void loadPlantData() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM plants ORDER BY id";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void handlePurchase() {
        int selectedRow = plantTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a plant to buy.");
            return;
        }

        int id = (int) tableModel.getValueAt(selectedRow, 0);
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
            totalLabel.setText(String.format("Total: Rs %.2f", totalBill));

            int newStock = stock - qty;

            try (Connection conn = connect();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "UPDATE plants SET stock = ? WHERE id = ?")) {
                pstmt.setInt(1, newStock);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

            tableModel.setValueAt(newStock, selectedRow, 4);
            JOptionPane.showMessageDialog(this,
                    "Purchased " + qty + " × " + name + " for Rs " + String.format("%.2f", subtotal));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void resetInventory() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Reset inventory to default values?", "Confirm Reset", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String resetSQL = """
                TRUNCATE plants RESTART IDENTITY;
                INSERT INTO plants (name, category, price, stock)
                VALUES
                ('Aloe Vera', 'Succulent', 5.50, 20),
                ('Peace Lily', 'Indoor', 8.99, 15),
                ('Rose', 'Flowering', 12.75, 10),
                ('Snake Plant', 'Indoor', 9.25, 12);
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(resetSQL);
            JOptionPane.showMessageDialog(this, "Inventory reset successfully!");
            loadPlantData();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error resetting inventory: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Use cross-platform Look and Feel to ensure buttons render correctly on macOS
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            NurseryAppMock app = new NurseryAppMock();
            app.setVisible(true);
        });
    }

}
