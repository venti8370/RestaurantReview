import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class RestaurantReviewApp {
    private JFrame mainFrame;
    private JTable reviewTable;
    private DefaultTableModel tableModel;

    public RestaurantReviewApp() {
        mainFrame = new JFrame("Restaurant Reviews");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);
        mainFrame.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Restaurant", "Reviewer", "Rating", "Review", "Action"}, 0);
        reviewTable = new JTable(tableModel);

        loadReviews();

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Review");
        addButton.addActionListener(e -> {
            mainFrame.setVisible(false);
            new ReviewEntryFrame(this).setVisible(true);
        });
        buttonPanel.add(addButton);

        JScrollPane scrollPane = new JScrollPane(reviewTable);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.add(buttonPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    public void loadReviews() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reviews ORDER BY rating DESC")) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("restaurant"));
                row.add(rs.getString("reviewer"));
                row.add(rs.getInt("rating"));
                row.add(rs.getString("review"));

                JButton removeButton = new JButton("Remove");
                int reviewId = rs.getInt("id");
                removeButton.addActionListener(e -> removeReview(reviewId));
                row.add(removeButton);

                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error loading reviews.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        reviewTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        reviewTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
    }

    private void removeReview(int reviewId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM reviews WHERE id = ?")) {
            pstmt.setInt(1, reviewId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                loadReviews();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Review not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Error removing review.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setVisible(boolean visible) {
        mainFrame.setVisible(visible);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RestaurantReviewApp::new);
    }
}
