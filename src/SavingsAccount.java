/**
 * SavingsAccount.java
 * 
 * A subclass of Account that represents a Savings Account.
 * Savings accounts have a minimum balance requirement.
 * If a withdrawal would bring the balance below the minimum, it is blocked.
 */
public class SavingsAccount extends Account {

    // The minimum balance that must stay in the account at all times
    private double minimumBalance;

    /**
     * Constructor for SavingsAccount
     * @param accountNumber  - unique account ID
     * @param customerID     - owner's ID
     * @param interestRate   - interest rate
     * @param minimumBalance - minimum balance required
     */
    public SavingsAccount(String accountNumber, String customerID, double interestRate, double minimumBalance) {
        // Call the parent (Account) constructor
        super(accountNumber, customerID, "Savings", interestRate);
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    /**
     * Override withdraw to check minimum balance rule.
     * The balance after withdrawal must not go below minimumBalance.
     */
    @Override
    public void withdraw(double amount) throws BankingException {
        if (amount <= 0) {
            throw new BankingException("Withdrawal amount must be greater than zero.");
        }
        if ((getBalance() - amount) < minimumBalance) {
            throw new BankingException("Cannot withdraw. Balance would fall below minimum balance of $" + minimumBalance);
        }
        // Call the parent withdraw method
        super.withdraw(amount);
    }

    /**
     * Returns account summary including minimum balance info
     */
    @Override
    public String getAccountSummary() {
        return super.getAccountSummary() + " | Minimum Balance: $" + minimumBalance;
    }
}
