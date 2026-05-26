/**
 * Account.java
 * 
 * This is the base class for all types of bank accounts.
 * It holds common account details like account number, balance,
 * interest rate, and a list of transactions.
 * 
 * Subclasses (SavingsAccount, CurrentAccount, FixedDepositAccount)
 * will inherit from this class.
 */

import java.util.ArrayList;

public class Account {

    // Unique account number
    private String accountNumber;

    // Current balance in the account
    private double balance;

    // Annual interest rate (e.g. 3.5 means 3.5%)
    private double interestRate;

    // The ID of the customer who owns this account
    private String customerID;

    // Type of account: "Savings", "Current", or "FixedDeposit"
    private String accountType;

    // List of transaction history (each entry is a short description)
    private ArrayList<String> transactions;

    /**
     * Constructor to create a new account
     * @param accountNumber - unique ID for the account
     * @param customerID    - ID of the account owner
     * @param accountType   - type of account
     * @param interestRate  - interest rate for this account
     */
    public Account(String accountNumber, String customerID, String accountType, double interestRate) {
        this.accountNumber = accountNumber;
        this.customerID = customerID;
        this.accountType = accountType;
        this.interestRate = interestRate;
        this.balance = 0.0;
        this.transactions = new ArrayList<String>();
    }

    // ---------- Getters ----------

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAccountType() {
        return accountType;
    }

    public ArrayList<String> getTransactions() {
        return transactions;
    }

    // ---------- Setters ----------

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    // ---------- Banking Methods ----------

    /**
     * Deposit money into the account.
     * Throws BankingException if the amount is zero or negative.
     * @param amount - how much to deposit
     */
    public void deposit(double amount) throws BankingException {
        if (amount <= 0) {
            throw new BankingException("Deposit amount must be greater than zero.");
        }
        balance = balance + amount;
        transactions.add("Deposited: $" + amount + " | New Balance: $" + balance);
    }

    /**
     * Withdraw money from the account.
     * Throws BankingException if amount is invalid or balance is too low.
     * @param amount - how much to withdraw
     */
    public void withdraw(double amount) throws BankingException {
        if (amount <= 0) {
            throw new BankingException("Withdrawal amount must be greater than zero.");
        }
        if (amount > balance) {
            throw new BankingException("Insufficient balance. Current balance: $" + balance);
        }
        balance = balance - amount;
        transactions.add("Withdrew: $" + amount + " | New Balance: $" + balance);
    }

    /**
     * Transfer money to another account.
     * @param targetAccount - the account to send money to
     * @param amount        - how much to transfer
     */
    public void transfer(Account targetAccount, double amount) throws BankingException {
        // Withdraw from this account first
        this.withdraw(amount);
        // Then deposit into the target account
        targetAccount.deposit(amount);
        transactions.add("Transferred: $" + amount + " to Account " + targetAccount.getAccountNumber());
    }

    /**
     * Apply simple interest to the account balance.
     * Interest = balance * interestRate / 100
     */
    public void applyInterest() throws BankingException {
        double interest = balance * interestRate / 100;
        deposit(interest);
        transactions.add("Interest Applied: $" + interest);
    }

    /**
     * Returns a summary of the account as a String.
     * This method can be overridden by subclasses.
     */
    public String getAccountSummary() {
        return "Account Number: " + accountNumber
                + " | Type: " + accountType
                + " | Balance: $" + balance
                + " | Interest Rate: " + interestRate + "%";
    }
}
