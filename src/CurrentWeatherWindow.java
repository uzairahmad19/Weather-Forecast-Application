import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.json.*;

public class CurrentWeatherWindow extends JFrame {

    private JTextField cityField;
    private JLabel tempLabel, conditionLabel, humidityLabel, windLabel, iconLabel;
    private final String API_KEY = "6d284285726f487e84a82e896ca1bfed"; // Replace with your API key

    public CurrentWeatherWindow() {
        setTitle("Current Weather");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background
        ImageIcon bg = new ImageIcon("resources/weather_bg.jpg");
        JLabel background = new JLabel(bg);
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Input Panel
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);

        // Create and customize city field
        cityField = new JTextField(20);
        cityField.setFont(new Font("SansSerif", Font.PLAIN, 18)); // Increase font size for city field
        cityField.setPreferredSize(new Dimension(250, 40)); // Increase width and height of city field

        // Create and customize button
        JButton getWeatherBtn = new JButton("Get Weather");
        getWeatherBtn.setFont(new Font("SansSerif", Font.PLAIN, 18)); // Increase font size for button
        getWeatherBtn.setPreferredSize(new Dimension(200, 40)); // Increase size of button
        getWeatherBtn.addActionListener(e -> fetchWeather());



        // Set the font color of the "Enter City" label to white
        JLabel cityLabel = new JLabel("Enter City:");
        cityLabel.setForeground(Color.BLACK); // Change label color to white
        cityLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));

        inputPanel.add(cityLabel);
        inputPanel.add(cityField);
        inputPanel.add(getWeatherBtn);
        background.add(inputPanel, BorderLayout.NORTH);

        // Weather Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 1));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        tempLabel = new JLabel("Temperature: ");
        conditionLabel = new JLabel("Condition: ");
        humidityLabel = new JLabel("Humidity: ");
        windLabel = new JLabel("Wind Speed: ");
        iconLabel = new JLabel(); // for weather icon

        Font font = new Font("SansSerif", Font.BOLD, 18);
        for (JLabel label : new JLabel[]{tempLabel, conditionLabel, humidityLabel, windLabel}) {
            label.setFont(font);
            label.setForeground(Color.BLACK);
            infoPanel.add(label);
        }

        infoPanel.add(iconLabel);
        background.add(infoPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();
        if (city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a city.");
            return;
        }

        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" +
                    URLEncoder.encode(city, "UTF-8") +
                    "&appid=" + API_KEY + "&units=metric";

            URL url = new URL(apiUrl);
            HttpURLConnection conne = (HttpURLConnection) url.openConnection();
            conne.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conne.getInputStream())
            );

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) json.append(line);
            reader.close();

            JSONObject obj = new JSONObject(json.toString());
            JSONObject main = obj.getJSONObject("main");
            JSONObject wind = obj.getJSONObject("wind");
            JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);
            String iconId = weather.getString("icon");

            double temperature = main.getDouble("temp");
            String condition = weather.getString("main");

            tempLabel.setText("Temperature: " + temperature + "°C");
            conditionLabel.setText("Condition: " + condition);
            humidityLabel.setText("Humidity: " + main.getInt("humidity") + "%");
            windLabel.setText("Wind Speed: " + wind.getDouble("speed") + " m/s");

            // Weather Icon - Load from local resources/icons folder
            String iconPath = "resources/icons/" + iconId + ".gif";
            File iconFile = new File(iconPath);

            if (iconFile.exists()) {
                ImageIcon animatedIcon = new ImageIcon(iconPath);
                Image scaled = animatedIcon.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                iconLabel.setIcon(new ImageIcon(scaled));
                iconLabel.setText(""); // Clear any fallback text
            } else {
                iconLabel.setIcon(null);
                iconLabel.setText("No icon found");
            }

            // Save to DB using JDBC
            saveToDatabase(city, temperature, condition);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching weather data.");
        }
    }

    private void saveToDatabase(String city, double temperature, String condition) {
        String url = "jdbc:mysql://localhost:3306/weather_app";
        String user = "root"; // your DB username
        String password = "root"; // your DB password

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);

            String sql = "INSERT INTO weather_history (city, temperature, `condition`) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, city);
            stmt.setDouble(2, temperature);
            stmt.setString(3, condition);

            stmt.executeUpdate();
            conn.close();
            System.out.println("Weather data saved to database.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save to database.");
        }
    }

    public static void main(String[] args) {
        new CurrentWeatherWindow();
    }
}
