package oops_ia3_urk24cs1112;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class DatabaseHelper {
    // Change these as per your PostgreSQL setup
    private static final String URL = "jdbc:postgresql://localhost:5432/nurserydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "jonathan3604";

    // Get connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Load all plants into the Swing table
    public static void loadPlantData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM plants ORDER BY id")) {

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
        }
    }

    // Update stock after a purchase
    public static void updateStock(int id, int newStock) {
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE plants SET stock = ? WHERE id = ?")) {
            pst.setInt(1, newStock);
            pst.setInt(2, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
