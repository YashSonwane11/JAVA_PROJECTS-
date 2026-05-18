import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Railway_Reservation_System {
    private static final String DB_URL = "jdbc:sqlite:railway.db";

    private Connection connection;
    private JFrame frame;
    private JPanel reservationPanel;
    private JPanel searchPanel;
    private JTabbedPane tabbedPane;
    private JTextField nameField;
    private JTextField ageField;
    private JRadioButton maleRadioButton;
    private JRadioButton femaleRadioButton;
    private JTextField fromStationField;
    private JTextField toStationField;
    private JTextField ticketField;
    private JButton reserveButton;
    private JTextArea resultArea;
    private JTextField searchTextField;

    public Railway_Reservation_System() {
        initializeGUI();
        connectToDatabase();
    }

    private void initializeGUI() {
        frame = new JFrame("Railway Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        reservationPanel = new JPanel();
        reservationPanel.setLayout(new FlowLayout());

        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Reservation", reservationPanel);
        tabbedPane.addTab("Search", searchPanel);

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
        searchTextField = new JTextField(20);

        reservationPanel.add(new JLabel("Name: "));
        reservationPanel.add(nameField);
        reservationPanel.add(new JLabel("Age: "));
        reservationPanel.add(ageField);
        reservationPanel.add(new JLabel("Gender: "));
        reservationPanel.add(maleRadioButton);
        reservationPanel.add(femaleRadioButton);
        reservationPanel.add(new JLabel("From Station: "));
        reservationPanel.add(fromStationField);
        reservationPanel.add(new JLabel("To Station: "));
        reservationPanel.add(toStationField);
        reservationPanel.add(new JLabel("Ticket: "));
        reservationPanel.add(ticketField);
        reservationPanel.add(reserveButton);
        reservationPanel.add(resultArea);

        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = maleRadioButton.isSelected() ? "Male" : "Female";
                String fromStation = fromStationField.getText();
                String toStation = toStationField.getText();
                int ticket = Integer.parseInt(ticketField.getText());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime = dateFormat.format(new Date());

                reserveTicket(name, age, gender, fromStation, toStation, ticket, dateTime);
                displayReservations();
            }
        });

        JButton searchButton = new JButton("Search by Name");
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchName = searchTextField.getText();
                searchReservations(searchName);
            }
        });

        frame.add(tabbedPane);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 300);
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
            String createTableSQL = "CREATE TABLE IF NOT EXISTS reservations (id INTEGER PRIMARY KEY"
            		+ ", name TEXT, age INT, gender TEXT, from_station TEXT, to_station TEXT, ticket INT, date_time TEXT)";
            PreparedStatement statement = connection.prepareStatement(createTableSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void reserveTicket(String name, int age, String gender, String fromStation, String toStation, int ticket, String dateTime) {
        try {
            String insertSQL = "INSERT INTO reservations (name, age, gender, from_station"
            		+ ", to_station, ticket, date_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            String selectSQL = "SELECT * FROM reservations WHERE name=?";
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
                resultArea.append("ID: " + id + ", Name: " + resultName + ", Age: " + age + 
                		", Gender: " + gender + ", From Station: " + fromStation + ", To Station: " + toStation + 
                		", Ticket: " + ticket + ", Date and Time: " + dateTime + "\n");
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
                resultArea.append("ID: " + id + ", Name: " + name + ", Age: " + age + ", Gender: " + gender + 
                		", From Station: " + fromStation + ", To Station: " + toStation + ", Ticket: " + ticket + 
                		", Date and Time: " + dateTime + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Railway_Reservation_System();
            }
        });
    }
}
