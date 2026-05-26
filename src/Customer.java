/**
 * Customer.java
 * 
 * Represents a bank customer.
 * Each customer has personal details and a list of accounts they own.
 * Customers can open and close accounts.
 */

import java.util.ArrayList;

public class Customer {

    // Unique customer ID (e.g. "C001")
    private String customerID;

    // Customer's full name
    private String name;

    // Customer's home address
    private String address;

    // Customer's phone number
    private String phoneNumber;

    // Customer's email address
    private String email;

    // List of accounts belonging to this customer
    private ArrayList<Account> accounts;

    /**
     * Constructor to create a new customer
     * @param customerID  - unique ID
     * @param name        - full name
     * @param address     - home address
     * @param phoneNumber - phone number
     * @param email       - email address
     */
    public Customer(String customerID, String name, String address, String phoneNumber, String email) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.accounts = new ArrayList<Account>();
    }

    // ---------- Getters ----------

    public String getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    // ---------- Setters ----------

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ---------- Account Management ----------

    /**
     * Open (add) a new account for this customer.
     * @param account - the account to add
     */
    public void openAccount(Account account) {
        accounts.add(account);
    }

    /**
     * Close (remove) an account by account number.
     * Throws BankingException if the account is not found.
     * @param accountNumber - the account to remove
     */
    public void closeAccount(String accountNumber) throws BankingException {
        Account found = null;

        // Loop through accounts to find the matching one
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(accountNumber)) {
                found = accounts.get(i);
                break;
            }
        }

        if (found == null) {
            throw new BankingException("Account not found: " + accountNumber);
        }

        accounts.remove(found);
    }

    /**
     * Find and return an account by its account number.
     * Returns null if not found.
     * @param accountNumber - the account to look for
     */
    public Account getAccount(String accountNumber) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountNumber().equals(accountNumber)) {
                return accounts.get(i);
            }
        }
        return null;
    }

    /**
     * Returns a summary of the customer's details
     */
    public String getCustomerSummary() {
        return "ID: " + customerID
                + " | Name: " + name
                + " | Phone: " + phoneNumber
                + " | Email: " + email
                + " | Accounts: " + accounts.size();
    }
}
