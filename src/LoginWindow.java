import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginWindow extends JFrame implements ActionListener {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton, signupButton;

    // DB credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/weather_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public LoginWindow() {
        setTitle("Login Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 230, 250));

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(150, 20, 100, 30);
        add(titleLabel);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(70, 70, 80, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(140, 70, 200, 25);
        add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(70, 110, 80, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(140, 110, 200, 25);
        add(passField);

        signupButton = new JButton("Sign Up");
        signupButton.setBounds(140, 160, 90, 30);
        signupButton.addActionListener(e -> {
            new SignUpPage().setVisible(true);
        });
        add(signupButton);


        loginButton = new JButton("Login");
        loginButton.setBounds(250, 160, 90, 30);
        loginButton.addActionListener(this);
        add(loginButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful!");
            new MainMenuWindow();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginWindow().setVisible(true);
        });
    }
}

