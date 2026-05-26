/**
 * FixedDepositAccount.java
 * 
 * A subclass of Account that represents a Fixed Deposit Account.
 * Money is locked for a fixed number of months (maturity period).
 * Withdrawals are NOT allowed before the maturity period ends.
 */
public class FixedDepositAccount extends Account {

    // How many months the money is locked for
    private int maturityMonths;

    // Whether the maturity period has ended (set manually for simplicity)
    private boolean matured;

    /**
     * Constructor for FixedDepositAccount
     * @param accountNumber  - unique account ID
     * @param customerID     - owner's ID
     * @param interestRate   - interest rate
     * @param maturityMonths - number of months until maturity
     */
    public FixedDepositAccount(String accountNumber, String customerID, double interestRate, int maturityMonths) {
        // Call the parent (Account) constructor
        super(accountNumber, customerID, "FixedDeposit", interestRate);
        this.maturityMonths = maturityMonths;
        this.matured = false;
    }

    public int getMaturityMonths() {
        return maturityMonths;
    }

    public boolean isMatured() {
        return matured;
    }

    /**
     * Mark the account as matured (money can now be withdrawn)
     */
    public void setMatured(boolean matured) {
        this.matured = matured;
    }

    /**
     * Override withdraw - only allowed after maturity
     */
    @Override
    public void withdraw(double amount) throws BankingException {
        if (!matured) {
            throw new BankingException("Cannot withdraw. Fixed deposit has not matured yet. Maturity period: " + maturityMonths + " months.");
        }
        super.withdraw(amount);
    }

    /**
     * Returns account summary including maturity info
     */
    @Override
    public String getAccountSummary() {
        return super.getAccountSummary()
                + " | Maturity: " + maturityMonths + " months"
                + " | Matured: " + (matured ? "Yes" : "No");
    }
}
