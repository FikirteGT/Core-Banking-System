/**
 * DatabaseHandler.java
 * 
 * Handles all database operations using JDBC and SQLite.
 * SQLite stores everything in a single file (bank.db) so no server is needed.
 * 
 * SETUP: Download sqlite-jdbc-x.x.x.jar and place it in the "lib" folder.
 * Download from: https://github.com/xerial/sqlite-jdbc/releases
 * 
 * Tables created:
 *   - customers (customerID, name, address, phoneNumber, email)
 *   - accounts  (accountNumber, customerID, accountType, balance, interestRate, extraField)
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseHandler {

    // Path to the SQLite database file
    private static final String DB_URL = "jdbc:sqlite:data/bank.db";

    // The active database connection
    private Connection connection;

    /**
     * Constructor - opens the database connection and creates tables if needed
     */
    public DatabaseHandler() {
        try {
            // Connect to the SQLite database (creates the file if it doesn't exist)
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connected successfully.");
            createTables();
        } catch (Exception e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    /**
     * Creates the customers and accounts tables if they don't already exist.
     */
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Create customers table
            stmt.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "customerID TEXT PRIMARY KEY, "
                    + "name TEXT, "
                    + "address TEXT, "
                    + "phoneNumber TEXT, "
                    + "email TEXT)");

            // Create accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "accountNumber TEXT PRIMARY KEY, "
                    + "customerID TEXT, "
                    + "accountType TEXT, "
                    + "balance REAL, "
                    + "interestRate REAL, "
                    + "extraField REAL)");

            stmt.close();
        } catch (Exception e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // ---------- Customer DB Operations ----------

    /**
     * Insert a new customer into the database.
     * @param customer - the customer to save
     */
    public void insertCustomer(Customer customer) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO customers VALUES (?, ?, ?, ?, ?)");
            ps.setString(1, customer.getCustomerID());
            ps.setString(2, customer.getName());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getPhoneNumber());
            ps.setString(5, customer.getEmail());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error inserting customer: " + e.getMessage());
        }
    }

    /**
     * Delete a customer from the database by their ID.
     * @param customerID - the ID of the customer to delete
     */
    public void deleteCustomer(String customerID) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM customers WHERE customerID = ?");
            ps.setString(1, customerID);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    /**
     * Load all customers from the database.
     * @return list of Customer objects
     */
    public ArrayList<Customer> loadAllCustomers() {
        ArrayList<Customer> customers = new ArrayList<Customer>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM customers");

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getString("customerID"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"));
                customers.add(c);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }

        return customers;
    }

    // ---------- Account DB Operations ----------

    /**
     * Insert or update an account in the database.
     * @param account - the account to save
     */
    public void insertAccount(Account account) {
        try {
            double extraField = 0;

            // Determine the extra field based on account type
            if (account instanceof SavingsAccount) {
                extraField = ((SavingsAccount) account).getMinimumBalance();
            } else if (account instanceof CurrentAccount) {
                extraField = ((CurrentAccount) account).getOverdraftLimit();
            } else if (account instanceof FixedDepositAccount) {
                extraField = ((FixedDepositAccount) account).getMaturityMonths();
            }

            PreparedStatement ps = connection.prepareStatement(
                    "INSERT OR REPLACE INTO accounts VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getCustomerID());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            ps.setDouble(5, account.getInterestRate());
            ps.setDouble(6, extraField);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error inserting account: " + e.getMessage());
        }
    }

    /**
     * Delete an account from the database by account number.
     * @param accountNumber - the account number to delete
     */
    public void deleteAccount(String accountNumber) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM accounts WHERE accountNumber = ?");
            ps.setString(1, accountNumber);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    /**
     * Load all accounts from the database.
     * @return list of Account objects
     */
    public ArrayList<Account> loadAllAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");

            while (rs.next()) {
                String accountNumber = rs.getString("accountNumber");
                String customerID    = rs.getString("customerID");
                String accountType   = rs.getString("accountType");
                double balance       = rs.getDouble("balance");
                double interestRate  = rs.getDouble("interestRate");
                double extraField    = rs.getDouble("extraField");

                Account account = null;

                if (accountType.equals("Savings")) {
                    account = new SavingsAccount(accountNumber, customerID, interestRate, extraField);
                } else if (accountType.equals("Current")) {
                    account = new CurrentAccount(accountNumber, customerID, interestRate, extraField);
                } else if (accountType.equals("FixedDeposit")) {
                    account = new FixedDepositAccount(accountNumber, customerID, interestRate, (int) extraField);
                }

                if (account != null) {
                    account.setBalance(balance);
                    accounts.add(account);
                }
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }

        return accounts;
    }

    /**
     * Close the database connection when done.
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (Exception e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
