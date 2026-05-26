/**
 * Main.java
 * 
 * The entry point of the banking system application.
 * 
 * This class:
 *   1. Creates the Bank object
 *   2. Connects to the database
 *   3. Loads existing data from the database (and files as backup)
 *   4. Creates and displays the GUI window
 */

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // Step 1: Create the bank
        Bank bank = new Bank("Java National Bank", "123 Main Street, Nairobi");

        // Step 2: Connect to the database
        DatabaseHandler dbHandler = new DatabaseHandler();

        // Step 3: Load existing customers and accounts from the database
        java.util.ArrayList<Customer> savedCustomers = dbHandler.loadAllCustomers();
        java.util.ArrayList<Account> savedAccounts   = dbHandler.loadAllAccounts();

        // Add loaded customers to the bank
        for (int i = 0; i < savedCustomers.size(); i++) {
            bank.addCustomer(savedCustomers.get(i));
        }

        // Add loaded accounts to the bank and link them to their customers
        for (int i = 0; i < savedAccounts.size(); i++) {
            Account account = savedAccounts.get(i);
            bank.addAccount(account);

            // Link account to the matching customer
            Customer customer = bank.findCustomer(account.getCustomerID());
            if (customer != null) {
                customer.openAccount(account);
            }
        }

        // Update the ID counters so new IDs don't clash with existing ones
        // (We count existing customers and accounts to set the counters)
        for (int i = 0; i < savedCustomers.size(); i++) {
            bank.generateCustomerID(); // advances the counter
        }
        for (int i = 0; i < savedAccounts.size(); i++) {
            bank.generateAccountNumber(); // advances the counter
        }

        // Step 4: Launch the GUI on the Event Dispatch Thread (Swing best practice)
        final Bank finalBank       = bank;
        final DatabaseHandler finalDB = dbHandler;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GUI gui = new GUI(finalBank, finalDB);
                gui.setVisible(true);
            }
        });
    }
}
