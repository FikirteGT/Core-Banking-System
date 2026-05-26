# Core Banking System - Java OOP Project

## Project Structure

```
Core-Banking-System/
├── src/
│   ├── BankingException.java     - Custom exception class
│   ├── Account.java              - Base account class
│   ├── SavingsAccount.java       - Savings account (min balance rule)
│   ├── CurrentAccount.java       - Current account (overdraft rule)
│   ├── FixedDepositAccount.java  - Fixed deposit (maturity lock)
│   ├── Customer.java             - Customer class
│   ├── Bank.java                 - Bank class (manages customers & accounts)
│   ├── FileHandler.java          - Reads/writes data to text files
│   ├── DatabaseHandler.java      - Reads/writes data to SQLite database
│   ├── GUI.java                  - Swing graphical user interface
│   └── Main.java                 - Entry point
├── data/                         - Auto-created: stores customers.txt, accounts.txt, bank.db
├── lib/                          - Place sqlite-jdbc JAR here
└── README.md
```

## Setup Instructions

### Step 1 - Download SQLite JDBC Driver
1. Go to: https://github.com/xerial/sqlite-jdbc/releases
2. Download the latest `sqlite-jdbc-x.x.x.jar` file
3. Place it inside the `lib/` folder

### Step 2 - Compile the project
Open a terminal/command prompt in the project root folder and run:

**Windows:**
```
javac -cp lib\sqlite-jdbc-*.jar -d out src\*.java
```

### Step 3 - Run the project
**Windows:**
```
java -cp out;lib\sqlite-jdbc-*.jar Main
```

## Features

| Tab          | What you can do                                      |
|--------------|------------------------------------------------------|
| Customers    | Add, Remove, Update, View all customers              |
| Accounts     | Open (Savings/Current/FixedDeposit), Close, View all |
| Transactions | Deposit, Withdraw, Transfer, View history            |
| Loans        | Apply for a loan (credited to account)               |

## Account Types

- **Savings** - Has a minimum balance. Cannot withdraw below it.
- **Current** - Has an overdraft limit. Can go negative up to the limit.
- **FixedDeposit** - Money is locked. Cannot withdraw until matured.

## Data Storage

- All data is saved to `data/bank.db` (SQLite database)
- Backup text files are also saved to `data/customers.txt` and `data/accounts.txt`
- Data is loaded automatically when the app starts
