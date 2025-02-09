Bank Account Management System (Java + SQLite)

Description

This is a simple Java console-based application that manages bank accounts and supports money transfers between accounts using SQLite as the database. The project demonstrates how to use JDBC with SQLite to store customer details and transaction records.

Features

Create bank accounts with customer details and initial balance.

Store account details persistently in an SQLite database.

Display account information.

Transfer money between accounts with automatic balance updates.

Maintain a transaction history with timestamps.

Technologies Used

Java

SQLite (JDBC)

SQL

Setup & Installation

Clone this repository:

git clone https://github.com/your-username/bank-management-system.git
cd bank-management-system

Install SQLite if not already installed.

Ensure you have Java installed (JDK 8 or higher).

Compile and run the program:

javac demo.java
java demo

Database Structure

The application creates two tables:

Accounts: Stores account number, customer name, and balance.

Transactions: Logs all money transfers with a timestamp.

How It Works

The program prompts the user to create multiple bank accounts.

Account details are saved in the SQLite database.

The user can transfer money between accounts by specifying the amount and account numbers.

Transaction details are recorded in the database.

The updated account balances are displayed after the transfer.

Contribution

Feel free to fork this repository and submit pull requests for improvements.

License

This project is open-source and available under the MIT License.
