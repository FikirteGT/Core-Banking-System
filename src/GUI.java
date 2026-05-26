/**
 * GUI.java
 * 
 * Creates the graphical user interface for the banking system using Java Swing.
 * The window has tabs for:
 *   1. Customers  - Add, Remove, Update, View customers
 *   2. Accounts   - Open, Close, View accounts
 *   3. Transactions - Deposit, Withdraw, Transfer
 *   4. Loans      - Apply for a simple loan (deposit into account)
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GUI extends JFrame {

    // Reference to the bank object that holds all data
    private Bank bank;

    // Reference to the database handler for saving/loading
    private DatabaseHandler dbHandler;

    // ---------- Customer Tab Components ----------
    private JTextField custIDField, custNameField, custAddressField, custPhoneField, custEmailField;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    // ---------- Account Tab Components ----------
    private JTextField accCustIDField, accTypeField, accInterestField, accExtraField;
    private JTable accountTable;
    private DefaultTableModel accountTableModel;

    // ---------- Transaction Tab Components ----------
    private JTextField txnAccNumberField, txnAmountField, txnTargetAccField;

    // ---------- Loan Tab Components ----------
    private JTextField loanAccField, loanAmountField;

    /**
     * Constructor - sets up the main window and all tabs
     * @param bank      - the bank object
     * @param dbHandler - the database handler
     */
    public GUI(Bank bank, DatabaseHandler dbHandler) {
        this.bank = bank;
        this.dbHandler = dbHandler;

        // Set window title and basic properties
        setTitle("Core Banking System - " + bank.getName());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen

        // Create a tabbed pane to hold all sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Customers", buildCustomerPanel());
        tabbedPane.addTab("Accounts", buildAccountPanel());
        tabbedPane.addTab("Transactions", buildTransactionPanel());
        tabbedPane.addTab("Loans", buildLoanPanel());

        add(tabbedPane);
    }

    // =====================================================================
    // CUSTOMER PANEL
    // =====================================================================

    /**
     * Builds the Customers tab panel
     */
    private JPanel buildCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Input form at the top ---
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        formPanel.add(new JLabel("Customer ID:"));
        custIDField = new JTextField();
        formPanel.add(custIDField);

        formPanel.add(new JLabel("Name:"));
        custNameField = new JTextField();
        formPanel.add(custNameField);

        formPanel.add(new JLabel("Address:"));
        custAddressField = new JTextField();
        formPanel.add(custAddressField);

        formPanel.add(new JLabel("Phone Number:"));
        custPhoneField = new JTextField();
        formPanel.add(custPhoneField);

        formPanel.add(new JLabel("Email:"));
        custEmailField = new JTextField();
        formPanel.add(custEmailField);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton    = new JButton("Add Customer");
        JButton removeButton = new JButton("Remove Customer");
        JButton updateButton = new JButton("Update Customer");
        JButton refreshButton = new JButton("Refresh List");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(refreshButton);

        formPanel.add(buttonPanel);

        // --- Table to display customers ---
        String[] columns = {"Customer ID", "Name", "Address", "Phone", "Email"};
        customerTableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(customerTableModel);
        JScrollPane scrollPane = new JScrollPane(customerTable);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load existing customers into the table
        refreshCustomerTable();

        // --- Button Actions ---

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeCustomer();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshCustomerTable();
            }
        });

        return panel;
    }

    /**
     * Adds a new customer using the form fields
     */
    private void addCustomer() {
        String name    = custNameField.getText().trim();
        String address = custAddressField.getText().trim();
        String phone   = custPhoneField.getText().trim();
        String email   = custEmailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Name, Phone, and Email.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = bank.generateCustomerID();
        Customer customer = new Customer(id, name, address, phone, email);
        bank.addCustomer(customer);
        dbHandler.insertCustomer(customer);
        FileHandler.saveCustomers(bank.getCustomers());

        JOptionPane.showMessageDialog(this, "Customer added! ID: " + id, "Success", JOptionPane.INFORMATION_MESSAGE);
        clearCustomerFields();
        refreshCustomerTable();
    }

    /**
     * Removes a customer using the ID field
     */
    private void removeCustomer() {
        String id = custIDField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the Customer ID to remove.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            bank.removeCustomer(id);
            dbHandler.deleteCustomer(id);
            FileHandler.saveCustomers(bank.getCustomers());
            JOptionPane.showMessageDialog(this, "Customer removed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearCustomerFields();
            refreshCustomerTable();
        } catch (BankingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates a customer's details using the form fields
     */
    private void updateCustomer() {
        String id      = custIDField.getText().trim();
        String name    = custNameField.getText().trim();
        String address = custAddressField.getText().trim();
        String phone   = custPhoneField.getText().trim();
        String email   = custEmailField.getText().trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the Customer ID to update.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            bank.updateCustomer(id, name, address, phone, email);
            Customer updated = bank.findCustomer(id);
            dbHandler.insertCustomer(updated);
            FileHandler.saveCustomers(bank.getCustomers());
            JOptionPane.showMessageDialog(this, "Customer updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCustomerTable();
        } catch (BankingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the customer table with current data
     */
    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0); // clear existing rows
        ArrayList<Customer> customers = bank.getCustomers();
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            customerTableModel.addRow(new Object[]{
                c.getCustomerID(), c.getName(), c.getAddress(), c.getPhoneNumber(), c.getEmail()
            });
        }
    }

    /**
     * Clears all customer input fields
     */
    private void clearCustomerFields() {
        custIDField.setText("");
        custNameField.setText("");
        custAddressField.setText("");
        custPhoneField.setText("");
        custEmailField.setText("");
    }

    // =====================================================================
    // ACCOUNT PANEL
    // =====================================================================

    /**
     * Builds the Accounts tab panel
     */
    private JPanel buildAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));

        formPanel.add(new JLabel("Customer ID:"));
        accCustIDField = new JTextField();
        formPanel.add(accCustIDField);

        formPanel.add(new JLabel("Account Type (Savings/Current/FixedDeposit):"));
        accTypeField = new JTextField();
        formPanel.add(accTypeField);

        formPanel.add(new JLabel("Interest Rate (%):"));
        accInterestField = new JTextField();
        formPanel.add(accInterestField);

        formPanel.add(new JLabel("Extra (MinBal / Overdraft / Months):"));
        accExtraField = new JTextField();
        formPanel.add(accExtraField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton openButton    = new JButton("Open Account");
        JButton closeButton   = new JButton("Close Account");
        JButton refreshButton = new JButton("Refresh List");

        buttonPanel.add(openButton);
        buttonPanel.add(closeButton);
        buttonPanel.add(refreshButton);

        formPanel.add(buttonPanel);

        String[] columns = {"Account Number", "Customer ID", "Type", "Balance", "Interest Rate"};
        accountTableModel = new DefaultTableModel(columns, 0);
        accountTable = new JTable(accountTableModel);
        JScrollPane scrollPane = new JScrollPane(accountTable);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        refreshAccountTable();

        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openAccount();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeAccount();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshAccountTable();
            }
        });

        return panel;
    }

    /**
     * Opens a new account for a customer
     */
    private void openAccount() {
        String customerID  = accCustIDField.getText().trim();
        String accountType = accTypeField.getText().trim();
        String interestStr = accInterestField.getText().trim();
        String extraStr    = accExtraField.getText().trim();

        if (customerID.isEmpty() || accountType.isEmpty() || interestStr.isEmpty() || extraStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = bank.findCustomer(customerID);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Customer not found: " + customerID, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double interestRate = Double.parseDouble(interestStr);
            double extraField   = Double.parseDouble(extraStr);
            String accNumber    = bank.generateAccountNumber();
            Account account     = null;

            if (accountType.equalsIgnoreCase("Savings")) {
                account = new SavingsAccount(accNumber, customerID, interestRate, extraField);
            } else if (accountType.equalsIgnoreCase("Current")) {
                account = new CurrentAccount(accNumber, customerID, interestRate, extraField);
            } else if (accountType.equalsIgnoreCase("FixedDeposit")) {
                account = new FixedDepositAccount(accNumber, customerID, interestRate, (int) extraField);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account type. Use: Savings, Current, or FixedDeposit", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            bank.addAccount(account);
            customer.openAccount(account);
            dbHandler.insertAccount(account);
            FileHandler.saveAccounts(bank.getAccounts());

            JOptionPane.showMessageDialog(this, "Account opened! Number: " + accNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAccountTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Interest rate and extra field must be numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Closes an account - asks for account number via input dialog
     */
    private void closeAccount() {
        String accNumber = JOptionPane.showInputDialog(this, "Enter Account Number to close:");
        if (accNumber == null || accNumber.trim().isEmpty()) return;

        Account account = bank.findAccount(accNumber.trim());
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Remove from customer's list
            Customer customer = bank.findCustomer(account.getCustomerID());
            if (customer != null) {
                customer.closeAccount(accNumber.trim());
            }

            bank.removeAccount(accNumber.trim());
            dbHandler.deleteAccount(accNumber.trim());
            FileHandler.saveAccounts(bank.getAccounts());

            JOptionPane.showMessageDialog(this, "Account closed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAccountTable();
        } catch (BankingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the account table with current data
     */
    private void refreshAccountTable() {
        accountTableModel.setRowCount(0);
        ArrayList<Account> accounts = bank.getAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            Account a = accounts.get(i);
            accountTableModel.addRow(new Object[]{
                a.getAccountNumber(), a.getCustomerID(), a.getAccountType(),
                "$" + a.getBalance(), a.getInterestRate() + "%"
            });
        }
    }

    // =====================================================================
    // TRANSACTION PANEL
    // =====================================================================

    /**
     * Builds the Transactions tab panel
     */
    private JPanel buildTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

        formPanel.add(new JLabel("Account Number:"));
        txnAccNumberField = new JTextField();
        formPanel.add(txnAccNumberField);

        formPanel.add(new JLabel("Amount:"));
        txnAmountField = new JTextField();
        formPanel.add(txnAmountField);

        formPanel.add(new JLabel("Target Account (for Transfer only):"));
        txnTargetAccField = new JTextField();
        formPanel.add(txnTargetAccField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton depositButton  = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton transferButton = new JButton("Transfer");
        JButton historyButton  = new JButton("View History");

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(historyButton);

        formPanel.add(buttonPanel);

        // Text area to show transaction results
        JTextArea resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Button Actions ---

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accNum = txnAccNumberField.getText().trim();
                String amtStr = txnAmountField.getText().trim();
                Account account = bank.findAccount(accNum);

                if (account == null) {
                    resultArea.setText("Account not found: " + accNum);
                    return;
                }

                try {
                    double amount = Double.parseDouble(amtStr);
                    account.deposit(amount);
                    dbHandler.insertAccount(account);
                    FileHandler.saveAccounts(bank.getAccounts());
                    resultArea.setText("Deposit successful!\n" + account.getAccountSummary());
                } catch (BankingException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    resultArea.setText("Invalid amount entered.");
                }
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accNum = txnAccNumberField.getText().trim();
                String amtStr = txnAmountField.getText().trim();
                Account account = bank.findAccount(accNum);

                if (account == null) {
                    resultArea.setText("Account not found: " + accNum);
                    return;
                }

                try {
                    double amount = Double.parseDouble(amtStr);
                    account.withdraw(amount);
                    dbHandler.insertAccount(account);
                    FileHandler.saveAccounts(bank.getAccounts());
                    resultArea.setText("Withdrawal successful!\n" + account.getAccountSummary());
                } catch (BankingException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    resultArea.setText("Invalid amount entered.");
                }
            }
        });

        transferButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accNum    = txnAccNumberField.getText().trim();
                String targetNum = txnTargetAccField.getText().trim();
                String amtStr    = txnAmountField.getText().trim();

                Account source = bank.findAccount(accNum);
                Account target = bank.findAccount(targetNum);

                if (source == null) {
                    resultArea.setText("Source account not found: " + accNum);
                    return;
                }
                if (target == null) {
                    resultArea.setText("Target account not found: " + targetNum);
                    return;
                }

                try {
                    double amount = Double.parseDouble(amtStr);
                    source.transfer(target, amount);
                    dbHandler.insertAccount(source);
                    dbHandler.insertAccount(target);
                    FileHandler.saveAccounts(bank.getAccounts());
                    resultArea.setText("Transfer successful!\nFrom: " + source.getAccountSummary()
                            + "\nTo: " + target.getAccountSummary());
                } catch (BankingException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    resultArea.setText("Invalid amount entered.");
                }
            }
        });

        historyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accNum = txnAccNumberField.getText().trim();
                Account account = bank.findAccount(accNum);

                if (account == null) {
                    resultArea.setText("Account not found: " + accNum);
                    return;
                }

                ArrayList<String> history = account.getTransactions();
                if (history.isEmpty()) {
                    resultArea.setText("No transactions found for account: " + accNum);
                    return;
                }

                StringBuilder sb = new StringBuilder("Transaction History for " + accNum + ":\n");
                for (int i = 0; i < history.size(); i++) {
                    sb.append((i + 1) + ". " + history.get(i) + "\n");
                }
                resultArea.setText(sb.toString());
            }
        });

        return panel;
    }

    // =====================================================================
    // LOAN PANEL
    // =====================================================================

    /**
     * Builds the Loans tab panel.
     * A "loan" here deposits the loan amount into the account.
     */
    private JPanel buildLoanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Apply for Loan"));

        formPanel.add(new JLabel("Account Number:"));
        loanAccField = new JTextField();
        formPanel.add(loanAccField);

        formPanel.add(new JLabel("Loan Amount:"));
        loanAmountField = new JTextField();
        formPanel.add(loanAmountField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton applyButton = new JButton("Apply for Loan");
        buttonPanel.add(applyButton);
        formPanel.add(buttonPanel);

        JTextArea resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createTitledBorder("Result"));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String accNum = loanAccField.getText().trim();
                String amtStr = loanAmountField.getText().trim();

                Account account = bank.findAccount(accNum);
                if (account == null) {
                    resultArea.setText("Account not found: " + accNum);
                    return;
                }

                try {
                    double loanAmount = Double.parseDouble(amtStr);
                    if (loanAmount <= 0) {
                        resultArea.setText("Loan amount must be greater than zero.");
                        return;
                    }

                    // Loan is credited to the account as a deposit
                    account.deposit(loanAmount);
                    dbHandler.insertAccount(account);
                    FileHandler.saveAccounts(bank.getAccounts());

                    resultArea.setText("Loan of $" + loanAmount + " approved and credited!\n"
                            + account.getAccountSummary());
                } catch (BankingException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    resultArea.setText("Invalid loan amount entered.");
                }
            }
        });

        return panel;
    }
}
