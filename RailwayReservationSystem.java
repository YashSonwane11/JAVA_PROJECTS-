import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RailwayReservationSystem {
    private static final String DB_URL = "jdbc:sqlite:railway.db";
    private Connection connection;
    private JFrame frame;
    private JPanel panel;
    private JTextField nameField;
    private JTextField ageField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JTextField fromStationField;
    private JTextField toStationField;
    private JTextField ticketField;
    private JButton reserveButton;
    private JTextArea resultArea;

    public RailwayReservationSystem() {
        initializeGUI();
        connectToDatabase();
    }

    private void initializeGUI() {
        frame = new JFrame("Railway Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();
        panel.setLayout(new FlowLayout());

        nameField = new JTextField(20);
        ageField = new JTextField(10);
        maleRadioButton = new JRadioButton("Male");
        femaleRadioButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadioButton);
        genderGroup.add(femaleRadioButton);

        fromStationField = new JTextField(20);
        toStationField = new JTextField(20);
        ticketField = new JTextField(10);
        reserveButton = new JButton("Reserve");
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);

        panel.add(new JLabel("Name: "));
        panel.add(nameField);
        panel.add(new JLabel("Age: "));
        panel.add(ageField);
        panel.add(new JLabel("Gender: "));
        panel.add(maleRadioButton);
        panel.add(femaleRadioButton);
        panel.add(new JLabel("From Station: "));
        panel.add(fromStationField);
        panel.add(new JLabel("To Station: "));
        panel.add(toStationField);
        panel.add(new JLabel("Ticket: "));
        panel.add(ticketField);
        panel.add(reserveButton);
        panel.add(new JScrollPane(resultArea));

        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText().trim();
                    int age = Integer.parseInt(ageField.getText().trim());
                    String gender = maleRadioButton.isSelected() ? "Male" : "Female";
                    String fromStation = fromStationField.getText().trim();
                    String toStation = toStationField.getText().trim();
                    int ticket = Integer.parseInt(ticketField.getText().trim());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateTime = dateFormat.format(new Date());

                    reserveTicket(name, age, gender, fromStation, toStation, ticket, dateTime);
                    displayReservations();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter valid numbers for age and ticket.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Search functionality
        JButton searchButton = new JButton("Search by Name");
        JTextField searchField = new JTextField(20);
        panel.add(searchButton);
        panel.add(searchField);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchName = searchField.getText().trim();
                searchReservations(searchName);
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reservations ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT, "
                    + "age INT, "
                    + "gender TEXT, "
                    + "from_station TEXT, "
                    + "to_station TEXT, "
                    + "ticket INT, "
                    + "date_time TEXT"
                    + ")";
            PreparedStatement statement = connection.prepareStatement(createTableSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reserveTicket(String name, int age, String gender,
                                String fromStation, String toStation, int ticket, String dateTime) {
        try {
            String insertSQL = "INSERT INTO reservations (name, age, gender, from_station, to_station, ticket, date_time) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, gender);
            statement.setString(4, fromStation);
            statement.setString(5, toStation);
            statement.setInt(6, ticket);
            statement.setString(7, dateTime);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchReservations(String name) {
        resultArea.setText("");
        try {
            String selectSQL = "SELECT * FROM reservations WHERE name = ?";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String resultName = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String fromStation = resultSet.getString("from_station");
                String toStation = resultSet.getString("to_station");
                int ticket = resultSet.getInt("ticket");
                String dateTime = resultSet.getString("date_time");

                resultArea.append("ID: " + id + ", Name: " + resultName
                        + ", Age: " + age + ", Gender: " + gender
                        + ", From Station: " + fromStation
                        + ", To Station: " + toStation
                        + ", Ticket: " + ticket
                        + ", Date and Time: " + dateTime + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayReservations() {
        resultArea.setText("");
        try {
            String selectSQL = "SELECT * FROM reservations";
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                String fromStation = resultSet.getString("from_station");
                String toStation = resultSet.getString("to_station");
                int ticket = resultSet.getInt("ticket");
                String dateTime = resultSet.getString("date_time");

                resultArea.append("ID: " + id + ", Name: " + name
                        + ", Age: " + age + ", Gender: " + gender
                        + ", From Station: " + fromStation
                        + ", To Station: " + toStation
                        + ", Ticket: " + ticket
                        + ", Date and Time: " + dateTime + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RailwayReservationSystem();
            }
        });
    }
}
