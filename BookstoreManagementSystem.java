import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookstoreManagementSystem extends JFrame implements ActionListener {

    private static final String DB_URL = "jdbc:sqlite:book.db";
    private Connection conn;
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private JPanel addBookPanel;
    private JPanel searchBookPanel;
    private JPanel deleteBookPanel;

    private JTextField titleField, authorField, priceField;
    private JTextField searchField, deleteIdField;
    private JButton addButton, searchButton, showAllBooksButton, deleteButton;
    private JTextArea resultArea;
    private JLabel messageLabel;

    public BookstoreManagementSystem() {
        initializeGUI();
        connectToDatabase();
    }

    private void initializeGUI() {
        frame = new JFrame("Bookstore Management System");

        tabbedPane = new JTabbedPane();

        // ===== Add Book Panel =====
        addBookPanel = new JPanel(new FlowLayout());
        titleField = new JTextField(20);
        authorField = new JTextField(20);
        priceField = new JTextField(10);
        addButton = new JButton("Add Book");
        messageLabel = new JLabel("");

        addBookPanel.add(new JLabel("Title: "));
        addBookPanel.add(titleField);
        addBookPanel.add(new JLabel("Author: "));
        addBookPanel.add(authorField);
        addBookPanel.add(new JLabel("Price: "));
        addBookPanel.add(priceField);
        addBookPanel.add(addButton);
        addBookPanel.add(messageLabel);

        // ===== Search Book Panel =====
        searchBookPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search by Title");
        showAllBooksButton = new JButton("Show All Books");
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);

        searchBookPanel.add(new JLabel("Search Title: "));
        searchBookPanel.add(searchField);
        searchBookPanel.add(searchButton);
        searchBookPanel.add(showAllBooksButton);
        searchBookPanel.add(new JScrollPane(resultArea));

        // ===== Delete Book Panel =====
        deleteBookPanel = new JPanel(new FlowLayout());
        deleteIdField = new JTextField(10);
        deleteButton = new JButton("Delete by ID");

        deleteBookPanel.add(new JLabel("Enter Book ID to Delete: "));
        deleteBookPanel.add(deleteIdField);
        deleteBookPanel.add(deleteButton);

        // Add Tabs
        tabbedPane.addTab("Add Book", addBookPanel);
        tabbedPane.addTab("Search Book", searchBookPanel);
        tabbedPane.addTab("Delete Book", deleteBookPanel);

        // Action Listeners
        addButton.addActionListener(this);
        searchButton.addActionListener(this);
        showAllBooksButton.addActionListener(this);
        deleteButton.addActionListener(this);

        frame.add(tabbedPane);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setVisible(true);
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTableIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS books "
                    + "(id INTEGER PRIMARY KEY, title TEXT, author TEXT, price REAL)";
            PreparedStatement statement = conn.prepareStatement(createTableSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addBook(String title, String author, double price) {
        try {
            String insertSQL = "INSERT INTO books (title, author, price) VALUES (?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(insertSQL);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setDouble(3, price);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAllBooks() {
        resultArea.setText("");
        try {
            String selectSQL = "SELECT * FROM books";
            PreparedStatement statement = conn.prepareStatement(selectSQL);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");
                resultArea.append("ID: " + id + ", Title: " + title +
                        ", Author: " + author + ", Price: " + price + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchBooksByTitle(String title) {
        resultArea.setText("");
        try {
            String selectSQL = "SELECT * FROM books WHERE title LIKE ?";
            PreparedStatement statement = conn.prepareStatement(selectSQL);
            statement.setString(1, "%" + title + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String resultTitle = resultSet.getString("title");
                String author = resultSet.getString("author");
                double price = resultSet.getDouble("price");
                resultArea.append("ID: " + id + ", Title: " + resultTitle +
                        ", Author: " + author + ", Price: " + price + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBookById(int id) {
        try {
            String deleteSQL = "DELETE FROM books WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(deleteSQL);
            statement.setInt(1, id);
            int rows = statement.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Book deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "No book found with ID: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String title = titleField.getText();
            String author = authorField.getText();
            double price = Double.parseDouble(priceField.getText());
            addBook(title, author, price);

            titleField.setText("");
            authorField.setText("");
            priceField.setText("");
            messageLabel.setText("Record saved successfully!");

        } else if (e.getSource() == searchButton) {
            String searchTitle = searchField.getText();
            searchBooksByTitle(searchTitle);

        } else if (e.getSource() == showAllBooksButton) {
            showAllBooks();

        } else if (e.getSource() == deleteButton) {
            try {
                int id = Integer.parseInt(deleteIdField.getText());
                deleteBookById(id);
                deleteIdField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid numeric ID!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookstoreManagementSystem::new);
    }
}
