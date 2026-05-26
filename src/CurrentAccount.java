/**
 * CurrentAccount.java
 * 
 * A subclass of Account that represents a Current (Checking) Account.
 * Current accounts have an overdraft limit, meaning the customer
 * can withdraw more than their balance up to a certain limit.
 */
public class CurrentAccount extends Account {

    // How much the customer is allowed to go below zero
    private double overdraftLimit;

    /**
     * Constructor for CurrentAccount
     * @param accountNumber  - unique account ID
     * @param customerID     - owner's ID
     * @param interestRate   - interest rate
     * @param overdraftLimit - how much overdraft is allowed
     */
    public CurrentAccount(String accountNumber, String customerID, double interestRate, double overdraftLimit) {
        // Call the parent (Account) constructor
        super(accountNumber, customerID, "Current", interestRate);
        this.overdraftLimit = overdraftLimit;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    /**
     * Override withdraw to allow overdraft up to the overdraft limit.
     * The balance can go negative but not below -overdraftLimit.
     */
    @Override
    public void withdraw(double amount) throws BankingException {
        if (amount <= 0) {
            throw new BankingException("Withdrawal amount must be greater than zero.");
        }
        if ((getBalance() - amount) < -overdraftLimit) {
            throw new BankingException("Overdraft limit exceeded. Overdraft limit is $" + overdraftLimit);
        }
        // Directly update balance to allow negative values
        setBalance(getBalance() - amount);
        getTransactions().add("Withdrew: $" + amount + " | New Balance: $" + getBalance());
    }

    /**
     * Returns account summary including overdraft limit info
     */
    @Override
    public String getAccountSummary() {
        return super.getAccountSummary() + " | Overdraft Limit: $" + overdraftLimit;
    }
}
