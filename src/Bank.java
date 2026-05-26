/**
 * Bank.java
 * 
 * Represents the bank itself.
 * The bank holds a list of all customers and all accounts.
 * It provides methods to add, remove, update and search customers and accounts.
 */

import java.util.ArrayList;

public class Bank {

    // Name of the bank
    private String name;

    // Address of the bank's main branch
    private String address;

    // List of all customers registered with the bank
    private ArrayList<Customer> customers;

    // List of all accounts in the bank
    private ArrayList<Account> accounts;

    // Counter used to auto-generate customer IDs (e.g. C001, C002...)
    private int customerCounter;

    // Counter used to auto-generate account numbers (e.g. A001, A002...)
    private int accountCounter;

    /**
     * Constructor to create the bank
     * @param name    - bank name
     * @param address - bank address
     */
    public Bank(String name, String address) {
        this.name = name;
        this.address = address;
        this.customers = new ArrayList<Customer>();
        this.accounts = new ArrayList<Account>();
        this.customerCounter = 1;
        this.accountCounter = 1;
    }

    // ---------- Getters ----------

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    // ---------- ID Generators ----------

    /**
     * Generates the next customer ID like "C001", "C002", etc.
     */
    public String generateCustomerID() {
        String id = "C" + String.format("%03d", customerCounter);
        customerCounter++;
        return id;
    }

    /**
     * Generates the next account number like "A001", "A002", etc.
     */
    public String generateAccountNumber() {
        String number = "A" + String.format("%03d", accountCounter);
        accountCounter++;
        return number;
    }

    // ---------- Customer Management ----------

    /**
     * Add a new customer to the bank.
     * @param customer - the customer to add
     */
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    /**
     * Remove a customer by their ID.
     * Throws BankingException if customer is not found.
     * @param customerID - ID of the customer to remove
     */
    public void removeCustomer(String customerID) throws BankingException {
        Customer found = findCustomer(customerID);
        if (found == null) {
            throw new BankingException("Customer not found: " + customerID);
        }
        customers.remove(found);
    }

    /**
     * Update a customer's details.
     * Throws BankingException if customer is not found.
     */
    public void updateCustomer(String customerID, String name, String address, String phone, String email) throws BankingException {
        Customer customer = findCustomer(customerID);
        if (customer == null) {
            throw new BankingException("Customer not found: " + customerID);
        }
        customer.setName(name);
        customer.setAddress(address);
        customer.setPhoneNumber(phone);
        customer.setEmail(email);
    }

    /**
     * Find a customer by their ID.
     * Returns null if not found.
     * @param customerID - the ID to search for
     */
    public Customer findCustomer(String customerID) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getCustomerID().equals(customerID)) {
                return customers.get(i);
            }
        }
        return null;
    }

    // ---------- Account Management ----------

    /**
     * Add an account to the bank's master list.
     * @param account - the account to add
     */
    public void addAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Remove an account from the bank by account number.
     * Throws BankingException if not found.
     * @param accountNumber - the account number to remove
     */
    public void removeAccount(String accountNumber) throws BankingException {
        Account found = findAccount(accountNumber);
        if (found == null) {
            throw new BankingException("Account not found: " + accountNumber);
        }
        accounts.remove(found);
    }

    /**
     * Find an account by its account number.
     * Returns null if not found.
     * @param accountNumber - the account number to search for
     */
    public Account findAccount(String accountNumber) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(accountNumber)) {
                return accounts.get(i);
            }
        }
        return null;
    }
}
