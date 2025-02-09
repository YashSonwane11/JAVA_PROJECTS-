import java.sql.*;
import java.util.Scanner;

public class demo {
    public static void main(String[] args) {
        Scanner me = new Scanner(System.in);

        try {
            // Load SQLite JDBC Driver
            Class.forName("org.sqlite.JDBC");

            // Connect to SQLite database
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:bank.db")) {
                // Create tables if not exist
                createTables(conn);

                System.out.println("How many accounts?: ");
                int n = me.nextInt();
                Account[] accounts = new Account[n];

                for (int i = 0; i < n; i++) {
                    accounts[i] = new Account();
                    accounts[i].getData(me, conn);
                }

                System.out.println("============================");
                System.out.println("Account Holder's Status");
                for (Account account : accounts) {
                    account.display();
                }

                System.out.println("============================");
                System.out.println("Enter Amount to Transfer: ");
                int amount = me.nextInt();
                System.out.println("From Account No: ");
                int fromAcc = me.nextInt();
                System.out.println("To Account No: ");
                int toAcc = me.nextInt();

                Account fromAccount = null, toAccount = null;
                for (Account account : accounts) {
                    if (account.acno == fromAcc) {
                        fromAccount = account;
                    }
                    if (account.acno == toAcc) {
                        toAccount = account;
                    }
                }

                if (fromAccount != null && toAccount != null) {
                    fromAccount.moneyTransfer(toAccount, amount, conn);
                } else {
                    System.out.println("Invalid account number(s).");
                }

                System.out.println("============================");
                System.out.println("After Money Transfer");
                for (Account account : accounts) {
                    account.display();
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        me.close();
    }

    private static void createTables(Connection conn) throws SQLException {
        String createAccountsTable = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "acno INT PRIMARY KEY, " +
                "cname VARCHAR(255) NOT NULL, " +
                "balance INT NOT NULL)";
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS Transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "from_acno INT, " +
                "to_acno INT, " +
                "amount INT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createAccountsTable);
            stmt.execute(createTransactionsTable);
        }
    }
}

class Account {
    public int acno;
    public String cname;
    public int balance;

    public void getData(Scanner me, Connection conn) throws SQLException {
        System.out.println("Enter the Customer Name:");
        me.nextLine(); // Consume leftover newline
        cname = me.nextLine();

        System.out.println("Enter Account Number:");
        acno = me.nextInt();

        System.out.println("Enter Balance in A/c:");
        balance = me.nextInt();

        // Store in SQLite database
        String sql = "INSERT INTO Accounts (acno, cname, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, acno);
            stmt.setString(2, cname);
            stmt.setInt(3, balance);
            stmt.executeUpdate();
            System.out.println("Account saved to database.");
        }
    }

    public void display() {
        System.out.println(acno + "\t" + cname + "\t" + balance);
    }

    public void moneyTransfer(Account toAccount, int amount, Connection conn) throws SQLException {
        if (balance >= amount) {
            balance -= amount;
            toAccount.balance += amount;

            // Update balances in database
            try (PreparedStatement stmt1 = conn.prepareStatement("UPDATE Accounts SET balance = ? WHERE acno = ?");
                 PreparedStatement stmt2 = conn.prepareStatement("UPDATE Accounts SET balance = ? WHERE acno = ?");
                 PreparedStatement stmt3 = conn.prepareStatement("INSERT INTO Transactions (from_acno, to_acno, amount) VALUES (?, ?, ?)");) {

                stmt1.setInt(1, balance);
                stmt1.setInt(2, acno);
                stmt1.executeUpdate();

                stmt2.setInt(1, toAccount.balance);
                stmt2.setInt(2, toAccount.acno);
                stmt2.executeUpdate();

                stmt3.setInt(1, acno);
                stmt3.setInt(2, toAccount.acno);
                stmt3.setInt(3, amount);
                stmt3.executeUpdate();
            }

            System.out.println("Transfer Successful!");
        } else {
            System.out.println("Insufficient balance for transfer.");
        }
    }
}