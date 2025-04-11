import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchHistoryWindow extends JFrame {

    public SearchHistoryWindow() {
        setTitle("Search History");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background image
        ImageIcon bg = new ImageIcon("resources/history_bg.jpg"); // make sure this image exists
        JLabel background = new JLabel(bg);
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Table to show search history
        String[] columns = {"City", "Temperature (°C)", "Condition", "Search Time"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        background.add(scrollPane, BorderLayout.CENTER);

        // Load history from DB
        loadSearchHistory(model);

        setVisible(true);
    }

    private void loadSearchHistory(DefaultTableModel model) {
        String url = "jdbc:mysql://localhost:3306/weather_app";
        String user = "root"; // Your DB user
        String password = "root"; // Your DB password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "SELECT city, temperature, `condition`, search_time " +
                    "FROM weather_history ORDER BY search_time DESC LIMIT 10";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String city = rs.getString("city");
                double temp = rs.getDouble("temperature");
                String condition = rs.getString("condition");
                Timestamp time = rs.getTimestamp("search_time");

                model.addRow(new Object[]{city, temp, condition, time.toString()});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load search history.");
        }
    }

    public static void main(String[] args) {
        new SearchHistoryWindow();
    }
}
