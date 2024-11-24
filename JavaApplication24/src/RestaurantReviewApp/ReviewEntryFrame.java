import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReviewEntryFrame extends JFrame {
    private JTextField restaurantField;
    private JTextField reviewerField;
    private JComboBox<Integer> ratingField;
    private JTextArea reviewField;

    public ReviewEntryFrame(RestaurantReviewApp mainApp) {
        setTitle("Add Review");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Restaurant:"));
        restaurantField = new JTextField();
        formPanel.add(restaurantField);

        formPanel.add(new JLabel("Reviewer:"));
        reviewerField = new JTextField();
        formPanel.add(reviewerField);

        formPanel.add(new JLabel("Rating:"));
        ratingField = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        formPanel.add(ratingField);

        formPanel.add(new JLabel("Review:"));
        reviewField = new JTextArea();
        formPanel.add(new JScrollPane(reviewField));

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveReview(mainApp));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            setVisible(false);
            mainApp.setVisible(true);
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveReview(RestaurantReviewApp mainApp) {
        String restaurant = restaurantField.getText();
        String reviewer = reviewerField.getText();
        int rating = (int) ratingField.getSelectedItem();
        String review = reviewField.getText();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO reviews (restaurant, reviewer, rating, review) VALUES (?, ?, ?, ?)")) {
            pstmt.setString(1, restaurant);
            pstmt.setString(2, reviewer);
            pstmt.setInt(3, rating);
            pstmt.setString(4, review);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Review saved successfully.");
            setVisible(false);
            mainApp.setVisible(true);
            mainApp.loadReviews();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving review.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
