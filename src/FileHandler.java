/**
 * FileHandler.java
 * 
 * Handles reading and writing data to text files.
 * Customer and account data is saved to the "data" folder.
 * Each line in the file represents one record.
 * 
 * Format for customers.txt:
 *   customerID,name,address,phoneNumber,email
 * 
 * Format for accounts.txt:
 *   accountNumber,customerID,accountType,balance,interestRate,extraField
 *   (extraField = minimumBalance for Savings, overdraftLimit for Current, maturityMonths for FixedDeposit)
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {

    // Path to the customers data file
    private static final String CUSTOMERS_FILE = "data/customers.txt";

    // Path to the accounts data file
    private static final String ACCOUNTS_FILE = "data/accounts.txt";

    /**
     * Save all customers to the customers.txt file.
     * Each customer is written as one line.
     * @param customers - list of customers to save
     */
    public static void saveCustomers(ArrayList<Customer> customers) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(CUSTOMERS_FILE));

            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                // Write each field separated by commas
                writer.write(c.getCustomerID() + "," + c.getName() + "," + c.getAddress()
                        + "," + c.getPhoneNumber() + "," + c.getEmail());
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    /**
     * Load all customers from the customers.txt file.
     * Returns a list of Customer objects.
     */
    public static ArrayList<Customer> loadCustomers() {
        ArrayList<Customer> customers = new ArrayList<Customer>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CUSTOMERS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                // Split the line by comma
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    Customer c = new Customer(parts[0], parts[1], parts[2], parts[3], parts[4]);
                    customers.add(c);
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("No existing customer data found. Starting fresh.");
        }

        return customers;
    }

    /**
     * Save all accounts to the accounts.txt file.
     * Each account is written as one line.
     * @param accounts - list of accounts to save
     */
    public static void saveAccounts(ArrayList<Account> accounts) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ACCOUNTS_FILE));

            for (int i = 0; i < accounts.size(); i++) {
                Account a = accounts.get(i);
                String extraField = "0"; // default extra field

                // Determine the extra field based on account type
                if (a instanceof SavingsAccount) {
                    extraField = String.valueOf(((SavingsAccount) a).getMinimumBalance());
                } else if (a instanceof CurrentAccount) {
                    extraField = String.valueOf(((CurrentAccount) a).getOverdraftLimit());
                } else if (a instanceof FixedDepositAccount) {
                    extraField = String.valueOf(((FixedDepositAccount) a).getMaturityMonths());
                }

                writer.write(a.getAccountNumber() + "," + a.getCustomerID() + ","
                        + a.getAccountType() + "," + a.getBalance() + ","
                        + a.getInterestRate() + "," + extraField);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    /**
     * Load all accounts from the accounts.txt file.
     * Returns a list of Account objects.
     */
    public static ArrayList<Account> loadAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(ACCOUNTS_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 6) {
                    String accountNumber = parts[0];
                    String customerID    = parts[1];
                    String accountType   = parts[2];
                    double balance       = Double.parseDouble(parts[3]);
                    double interestRate  = Double.parseDouble(parts[4]);
                    double extraField    = Double.parseDouble(parts[5]);

                    Account account = null;

                    // Create the correct subclass based on account type
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
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("No existing account data found. Starting fresh.");
        }

        return accounts;
    }
}
