import java.io.*;
import java.util.HashMap;

/**
 * CamFlowSystem.java
 * Core engine of CamFlow v2.0.
 * Holds all accounts (Account[]) and full transaction history (Transaction[][]).
 * Enforces all business rules: fees, balance limits, status-based permissions.
 *
 * Institution : PKFokam Institute of Excellence
 * Course      : Java Lab — Mr. LELE Vaneck
 * Developer   : NGASSA DJONGA Christian Fredy
 *
 * Chapter 5 — Arrays: Account[] and Transaction[MAX_ACCOUNTS][MAX_TX]
 *
 * @author NGASSA DJONGA Christian Fredy
 * @version 2.0 — Freshman 2, 2026
 */
public class CamFlowSystem implements Serializable {

    // -------------------------------------------------------------------------
    // CONSTANTS (Chapter 1 — named constants)
    // -------------------------------------------------------------------------
    public static final int    MAX_ACCOUNTS          = 50;
    public static final double WITHDRAWAL_FEE_RATE   = 0.01;
    public static final double TRANSFER_FEE_RATE     = 0.015;
    public static final double MIN_BALANCE            = 500.0;
    public static final double SAVINGS_INTEREST_RATE  = 0.05;
    public static final double FEE_WAIVER_THRESHOLD   = 1000.0;

    /** Last N transactions to show in quick view */
    public static final int LAST_TX_COUNT = 5;

    private static final String ACCOUNTS_FILE = "camflow_accounts.dat";
    private static final String HISTORY_FILE  = "camflow_history.dat";

    // -------------------------------------------------------------------------
    // FIELDS
    // -------------------------------------------------------------------------

    /** Chapter 5 — 1D array: all accounts */
    private Account[] accounts;

    /** Chapter 5 — 2D array: history[accountIndex][txIndex] */
    private Transaction[][] history;

    private int accountCount;

    /** HashMap: admin username -> PIN */
    private HashMap<String, String> adminPINs;

    /** HashMap: admin username -> display name */
    private HashMap<String, String> adminNames;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------

    /**
     * Initializes the system — loads from file or seeds fresh data.
     */
    public CamFlowSystem() {
        accounts     = new Account[MAX_ACCOUNTS];
        history      = new Transaction[MAX_ACCOUNTS][Account.MAX_TX];
        accountCount = 0;
        adminPINs    = new HashMap<>();
        adminNames   = new HashMap<>();
        initAdmins();
        if (!loadFromFile()) seedAccounts();
    }

    /** Seeds the two admin credentials into the HashMap. */
    private void initAdmins() {
        adminPINs.put("admin1", "Admin1@2024");
        adminPINs.put("admin2", "Admin2@2024");
        adminNames.put("admin1", "Dr. KAMDEM Kamdem");
        adminNames.put("admin2", "Miss GRACE Grace");
    }

    /**
     * Seeds 8 student accounts on first run.
     * CF-0001..CF-0006 = STUDENT, CF-0007 = STAFF, CF-0008 = ADMINISTRATOR
     * (to demonstrate all three statuses with pre-seeded data)
     */
    private void seedAccounts() {
        addAccount(new Account("CF-0001","NGASSA","Fredy",     'S', 5000, "Ngassa@2024","Computer Engineering and Technology",2,19, Account.STATUS_STUDENT));
        addAccount(new Account("CF-0002","KAZE",  "Brayan",    'C',10000, "Kaze@2024",  "Mechatronics",2,18,                        Account.STATUS_STUDENT));
        addAccount(new Account("CF-0003","ALIOU", "Maryam",    'S', 7500, "Aliou@2024", "Computer Engineering and Technology",2,19, Account.STATUS_STUDENT));
        addAccount(new Account("CF-0004","DIALLO","Mouhamadou",'C',15000, "Diallo@2024","Mechatronics",3,20,                        Account.STATUS_STUDENT));
        addAccount(new Account("CF-0005","MVONDO","Antony",    'S', 3000, "Mvondo@2024","Computer Engineering and Technology",2,18, Account.STATUS_STUDENT));
        addAccount(new Account("CF-0006","TCHAMBA","Prisca",   'S', 8000, "Tchamba@2024","Computer Engineering and Technology",2,19,Account.STATUS_STUDENT));
        addAccount(new Account("CF-0007","NANVOU","Sonia",     'C', 6000, "Nanvou@2024","Computer Engineering and Technology",2,18, Account.STATUS_STAFF));
        addAccount(new Account("CF-0008","POKAM", "Paul",      'S',20000, "Pokam@2024", "Economic and Management",4,20,             Account.STATUS_ADMINISTRATOR));
        Account.setAccountCounter(8);
        saveAll();
    }

    // -------------------------------------------------------------------------
    // ACCOUNT MANAGEMENT
    // -------------------------------------------------------------------------

    /**
     * Adds an Account to the array.
     * Chapter 5 — storing into accounts[].
     */
    public boolean addAccount(Account a) {
        if (accountCount >= MAX_ACCOUNTS) return false;
        accounts[accountCount] = a;
        history[accountCount]  = new Transaction[Account.MAX_TX];
        accountCount++;
        return true;
    }

    /**
     * Finds account index by number. Returns -1 if not found.
     * Chapter 5 — for loop iterating accounts[].
     */
    public int findAccountByNumber(String num) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equalsIgnoreCase(num)) return i;
        }
        return -1;
    }

    public Account  getAccount(int idx)     { return (idx >= 0 && idx < accountCount) ? accounts[idx] : null; }
    public int      getAccountCount()       { return accountCount; }
    public Account[] getAccounts()          { return accounts; }

    /**
     * Closes an account.
     * RULE: balance MUST be 0.0 before closing (admin cannot delete if money inside).
     * Decrements static accountCounter.
     *
     * @param num        account number to close
     * @param adminUser  admin username performing the action (for audit)
     */
    public void closeAccount(String num, String adminUser) {
        int idx = findAccountByNumber(num);
        if (idx == -1)           { ConsoleUI.error("Account not found: " + num);  return; }
        Account a = accounts[idx];
        if (!a.isActive())       { ConsoleUI.error("Account already closed.");    return; }

        // RULE: cannot close if balance > 0
        if (a.getBalance() > 0.0) {
            ConsoleUI.error("Cannot close account with money inside!");
            ConsoleUI.warning("Current balance: " + ConsoleUI.formatAmount(a.getBalance()));
            ConsoleUI.info("The account holder must withdraw all funds first.");
            return;
        }

        a.setActive(false);
        // OOP NOTE — static counter decrement
        Account.setAccountCounter(Account.getAccountCounter() - 1);
        // Record in audit log
        a.recordAudit(adminUser, "ACCOUNT_CLOSED", "Closed by " + adminUser);
        ConsoleUI.success("Account " + num + " (" + a.getFullName() + ") closed successfully.");
        saveAll();
    }

    // -------------------------------------------------------------------------
    // FEE CALCULATION (Chapter 4 — value-returning method)
    // -------------------------------------------------------------------------

    /**
     * Calculates transaction fee.
     * Chapter 3 — switch on type, if/else for waiver.
     *
     * @param amount transaction amount
     * @param type   "WITHDRAWAL" or "TRANSFER"
     * @return fee in FCFA
     */
    public double calculateFee(double amount, String type) {
        double fee;
        switch (type) {
            case "WITHDRAWAL":
                fee = (amount < FEE_WAIVER_THRESHOLD) ? 0.0 : amount * WITHDRAWAL_FEE_RATE;
                break;
            case "TRANSFER":
                fee = amount * TRANSFER_FEE_RATE;
                break;
            default:
                fee = 0.0;
        }
        return fee;
    }

    // -------------------------------------------------------------------------
    // TRANSACTION OPERATIONS
    // -------------------------------------------------------------------------

    /**
     * Deposits amount into an account. No fee.
     */
    public void performDeposit(String num, double amount) {
        int idx = findAccountByNumber(num);
        if (idx == -1)              { ConsoleUI.error("Account not found: " + num); return; }
        Account a = accounts[idx];
        if (!a.isActive())          { ConsoleUI.error("Account is closed.");        return; }

        if (a.deposit(amount)) {
            Transaction tx = new Transaction("DEPOSIT", amount, 0.0, a.getBalance(),
                    "Deposit to " + num);
            recordTransaction(idx, tx);
            tx.printReceipt();
            saveAll();
        } else {
            ConsoleUI.error("Deposit failed — invalid amount.");
        }
    }

    /**
     * Withdraws from an account. Fee waived if amount < 1000 FCFA.
     * Balance - fee - amount must stay >= MIN_BALANCE.
     */
    public void performWithdrawal(String num, double amount) {
        int idx = findAccountByNumber(num);
        if (idx == -1)     { ConsoleUI.error("Account not found: " + num); return; }
        Account a = accounts[idx];
        if (!a.isActive()) { ConsoleUI.error("Account is closed.");        return; }

        double fee        = calculateFee(amount, "WITHDRAWAL");
        double totalDebit = amount + fee;

        if (a.getBalance() - totalDebit < MIN_BALANCE) {
            ConsoleUI.error("Insufficient funds.");
            ConsoleUI.info("Current balance : " + ConsoleUI.formatAmount(a.getBalance()));
            ConsoleUI.info("Total needed    : " + ConsoleUI.formatAmount(totalDebit + MIN_BALANCE));
            ConsoleUI.info("Min balance     : " + ConsoleUI.formatAmount(MIN_BALANCE) + " must remain.");
            return;
        }

        if (a.withdraw(totalDebit)) {
            Transaction tx = new Transaction("WITHDRAWAL", amount, fee, a.getBalance(),
                    "Withdrawal from " + num);
            recordTransaction(idx, tx);
            tx.printReceipt();
            saveAll();
        } else {
            ConsoleUI.error("Withdrawal failed.");
        }
    }

    /**
     * Transfers from sender to receiver.
     * Fee deducted from sender only. TRANSFER_OUT + TRANSFER_IN recorded separately.
     */
    public void performTransfer(String fromNum, String toNum, double amount) {
        int fromIdx = findAccountByNumber(fromNum);
        int toIdx   = findAccountByNumber(toNum);

        if (fromIdx == -1) { ConsoleUI.error("Sender account not found: "   + fromNum); return; }
        if (toIdx   == -1) { ConsoleUI.error("Receiver account not found: " + toNum);   return; }

        Account sender   = accounts[fromIdx];
        Account receiver = accounts[toIdx];

        if (!sender.isActive())              { ConsoleUI.error("Sender account is closed.");   return; }
        if (!receiver.isActive())            { ConsoleUI.error("Receiver account is closed."); return; }
        if (fromNum.equalsIgnoreCase(toNum)) { ConsoleUI.error("Cannot transfer to same account."); return; }

        double fee        = calculateFee(amount, "TRANSFER");
        double totalDebit = amount + fee;

        if (sender.getBalance() - totalDebit < MIN_BALANCE) {
            ConsoleUI.error("Insufficient funds for transfer.");
            ConsoleUI.info("Available (above min): " + ConsoleUI.formatAmount(sender.getBalance() - MIN_BALANCE));
            ConsoleUI.info("Required (amt + fee) : " + ConsoleUI.formatAmount(totalDebit));
            return;
        }

        sender.withdraw(totalDebit);
        receiver.deposit(amount);

        Transaction txOut = new Transaction("TRANSFER_OUT", amount, fee, sender.getBalance(),
                "Transfer to " + toNum + " (" + receiver.getFullName() + ")");
        Transaction txIn  = new Transaction("TRANSFER_IN",  amount, 0.0, receiver.getBalance(),
                "Transfer from " + fromNum + " (" + sender.getFullName() + ")");

        recordTransaction(fromIdx, txOut);
        recordTransaction(toIdx,   txIn);

        System.out.println(ConsoleUI.MAGENTA + ConsoleUI.center("=== TRANSFER RECEIPT ===") + ConsoleUI.RESET);
        txOut.printReceipt();
        txIn.printReceipt();
        saveAll();
    }

    // -------------------------------------------------------------------------
    // TRANSACTION RECORDING (Chapter 5 — 2D array)
    // -------------------------------------------------------------------------

    /**
     * Stores a Transaction into history[idx][txCount].
     * Chapter 5 — 2D array: history[accountIndex][transactionIndex].
     */
    public void recordTransaction(int idx, Transaction tx) {
        int txPos = accounts[idx].getTransactionCount() - 1;
        if (txPos >= 0 && txPos < Account.MAX_TX) {
            history[idx][txPos] = tx;
        }
    }

    // -------------------------------------------------------------------------
    // HISTORY DISPLAY
    // -------------------------------------------------------------------------

    /**
     * Prints the last LAST_TX_COUNT (5) transactions for an account.
     * Chapter 3 — for loop.
     */
    public void printLastTransactions(String num) {
        int idx = findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found."); return; }
        Account a   = accounts[idx];
        int count   = a.getTransactionCount();
        int start   = Math.max(0, count - LAST_TX_COUNT);

        ConsoleUI.printHeader("Last " + LAST_TX_COUNT + " Transactions — " + num, ConsoleUI.CYAN);
        if (count == 0) { ConsoleUI.info("No transactions yet."); return; }

        System.out.println(ConsoleUI.YELLOW
                + String.format("%-5s %-13s %15s %12s %18s", "ID","TYPE","AMOUNT","FEE","BALANCE AFTER")
                + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);

        for (int j = start; j < count; j++) {
            Transaction tx = history[idx][j];
            if (tx == null) continue;
            String color = (tx.getType().contains("DEPOSIT") || tx.getType().equals("TRANSFER_IN"))
                    ? ConsoleUI.GREEN : ConsoleUI.RED;
            System.out.println(color + tx.toTableRow() + ConsoleUI.RESET);
        }
    }

    /**
     * Prints the FULL transaction history table for an account.
     * Chapter 3 — for loop; Chapter 4 — required method.
     */
    public void printAccountHistory(String num) {
        int idx = findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found: " + num); return; }
        Account a = accounts[idx];
        ConsoleUI.printHeader("Full History — " + num + " (" + a.getFullName() + ")", ConsoleUI.CYAN);

        int count = a.getTransactionCount();
        if (count == 0) { ConsoleUI.info("No transactions recorded yet."); return; }

        System.out.println(ConsoleUI.YELLOW
                + String.format("%-5s %-13s %15s %12s %18s", "ID","TYPE","AMOUNT","FEE","BALANCE AFTER")
                + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);

        double totalDep = 0, totalWd = 0, totalFees = 0;
        for (int j = 0; j < count; j++) {
            Transaction tx = history[idx][j];
            if (tx == null) continue;
            String color = (tx.getType().contains("DEPOSIT") || tx.getType().equals("TRANSFER_IN"))
                    ? ConsoleUI.GREEN : ConsoleUI.RED;
            System.out.println(color + tx.toTableRow() + ConsoleUI.RESET);
            if (tx.getType().equals("DEPOSIT") || tx.getType().equals("TRANSFER_IN"))
                totalDep += tx.getAmount();
            else totalWd += tx.getAmount();
            totalFees += tx.getFee();
        }

        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN   + ConsoleUI.center("Total transactions : " + count)                      + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN  + ConsoleUI.center("Total deposited    : " + ConsoleUI.formatAmount(totalDep))  + ConsoleUI.RESET);
        System.out.println(ConsoleUI.RED    + ConsoleUI.center("Total withdrawn    : " + ConsoleUI.formatAmount(totalWd))   + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW + ConsoleUI.center("Total fees paid    : " + ConsoleUI.formatAmount(totalFees)) + ConsoleUI.RESET);
    }

    /** Prints full receipts for every transaction. */
    public void printFullStatement(String num) {
        int idx = findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found."); return; }
        Account a = accounts[idx];
        ConsoleUI.printHeader("Full Statement — " + a.getFullName(), ConsoleUI.MAGENTA);
        int count = a.getTransactionCount();
        if (count == 0) { ConsoleUI.info("No transactions yet."); return; }
        for (int j = 0; j < count; j++) {
            if (history[idx][j] != null) history[idx][j].printReceipt();
        }
    }

    // -------------------------------------------------------------------------
    // MONTHLY INTEREST
    // -------------------------------------------------------------------------

    /**
     * Applies monthly interest to a SINGLE account (for self-service users).
     * Only works if account is Savings.
     */
    public void applyInterestToOne(String num, String performedBy) {
        int idx = findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found."); return; }
        Account a = accounts[idx];
        if (!a.isActive())          { ConsoleUI.error("Account is closed."); return; }
        if (a.getAccountType() != 'S') {
            ConsoleUI.warning("Monthly interest only applies to Savings accounts.");
            return;
        }
        double before   = a.getBalance();
        a.applyMonthlyInterest();
        double interest = a.getBalance() - before;
        Transaction tx  = new Transaction("DEPOSIT", interest, 0.0, a.getBalance(),
                "Monthly Interest (" + ConsoleUI.formatRate(SAVINGS_INTEREST_RATE) + ")");
        recordTransaction(idx, tx);
        a.recordAudit(performedBy, "INTEREST_APPLIED", ConsoleUI.formatAmount(interest) + " credited");
        ConsoleUI.success("Interest credited: " + ConsoleUI.formatAmount(interest));
        saveAll();
    }

    /**
     * Applies monthly interest to ALL Savings accounts (admin only).
     * Chapter 3 — for loop + if to skip Current and inactive accounts.
     */
    public void applyInterestToAll(String adminUser) {
        int applied = 0;
        for (int i = 0; i < accountCount; i++) {
            Account a = accounts[i];
            if (a.isActive() && a.getAccountType() == 'S') {
                double before   = a.getBalance();
                a.applyMonthlyInterest();
                double interest = a.getBalance() - before;
                Transaction tx  = new Transaction("DEPOSIT", interest, 0.0, a.getBalance(),
                        "Monthly Interest (" + ConsoleUI.formatRate(SAVINGS_INTEREST_RATE) + ")");
                recordTransaction(i, tx);
                a.recordAudit(adminUser, "INTEREST_APPLIED",
                        ConsoleUI.formatAmount(interest) + " by " + adminUser);
                applied++;
                ConsoleUI.success(a.getAccountNumber() + " — interest: " + ConsoleUI.formatAmount(interest));
            }
        }
        ConsoleUI.info("Interest applied to " + applied + " Savings account(s).");
        saveAll();
    }

    // -------------------------------------------------------------------------
    // REPORTS
    // -------------------------------------------------------------------------

    /** Lists all active accounts in a formatted table. */
    public void listAllAccounts() {
        ConsoleUI.printHeader("All Active Accounts", ConsoleUI.BLUE);
        System.out.println(ConsoleUI.YELLOW
                + String.format("%-3s %-8s %-16s %-6s %-14s %16s %5s",
                        "#","AccNum","Owner","Type","Status","Balance","Txns")
                + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);

        int count = 0; double totalFunds = 0;
        for (int i = 0; i < accountCount; i++) {
            Account a = accounts[i];
            if (!a.isActive()) continue;
            count++;
            totalFunds += a.getBalance();
            String name = (a.getFullName().length() > 16)
                    ? a.getFullName().substring(0, 14) + ".." : a.getFullName();
            String statusColor;
            switch (a.getStatus()) {
                case Account.STATUS_ADMINISTRATOR: statusColor = ConsoleUI.MAGENTA; break;
                case Account.STATUS_STAFF:         statusColor = ConsoleUI.YELLOW;  break;
                default:                           statusColor = ConsoleUI.CYAN;
            }
            System.out.println(statusColor + String.format("%-3d %-8s %-16s %-6s %-14s %16s %5d",
                    count, a.getAccountNumber(), name,
                    a.getAccountType() == 'S' ? "SAV" : "CUR",
                    a.getStatus(),
                    ConsoleUI.formatAmount(a.getBalance()),
                    a.getTransactionCount()) + ConsoleUI.RESET);
        }
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN
                + ConsoleUI.center("Total: " + count + " accounts  |  Funds: " + ConsoleUI.formatAmount(totalFunds))
                + ConsoleUI.RESET);
    }

    /** Prints system-wide statistics. */
    public void printSystemStats() {
        ConsoleUI.printHeader("System Statistics", ConsoleUI.MAGENTA);
        int activeCount = 0, totalTx = 0;
        double totalFunds = 0, highBal = Double.MIN_VALUE, lowBal = Double.MAX_VALUE;
        String highAcc = "N/A", lowAcc = "N/A";
        int students = 0, staff = 0, admins = 0;

        for (int i = 0; i < accountCount; i++) {
            Account a = accounts[i];
            if (!a.isActive()) continue;
            activeCount++;
            totalFunds += a.getBalance();
            totalTx    += a.getTransactionCount();
            if (a.getBalance() > highBal) { highBal = a.getBalance(); highAcc = a.getAccountNumber() + " (" + a.getFullName() + ")"; }
            if (a.getBalance() < lowBal)  { lowBal  = a.getBalance(); lowAcc  = a.getAccountNumber() + " (" + a.getFullName() + ")"; }
            switch (a.getStatus()) {
                case Account.STATUS_STUDENT:       students++; break;
                case Account.STATUS_STAFF:         staff++;    break;
                case Account.STATUS_ADMINISTRATOR: admins++;   break;
            }
        }
        double avg = (activeCount > 0) ? totalFunds / activeCount : 0;

        System.out.println(ConsoleUI.CYAN    + ConsoleUI.center("Active Accounts   : " + activeCount)          + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN    + ConsoleUI.center("  Students: " + students + "  |  Staff: " + staff + "  |  Admins: " + admins) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN   + ConsoleUI.center("Total Funds       : " + ConsoleUI.formatAmount(totalFunds)) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN   + ConsoleUI.center("Highest Balance   : " + ConsoleUI.formatAmount(highBal))   + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN    + ConsoleUI.center("  => " + highAcc)                              + ConsoleUI.RESET);
        System.out.println(ConsoleUI.RED     + ConsoleUI.center("Lowest Balance    : " + ConsoleUI.formatAmount(lowBal))    + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN    + ConsoleUI.center("  => " + lowAcc)                               + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW  + ConsoleUI.center("Average Balance   : " + ConsoleUI.formatAmount(avg))      + ConsoleUI.RESET);
        System.out.println(ConsoleUI.MAGENTA + ConsoleUI.center("Total Transactions: " + totalTx)                + ConsoleUI.RESET);
    }

    // -------------------------------------------------------------------------
    // ADMIN AUTH
    // -------------------------------------------------------------------------

    public boolean checkAdminCredentials(String username, String pin) {
        String stored = adminPINs.get(username);
        return stored != null && stored.equals(pin);
    }
    public String getAdminName(String username) {
        return adminNames.getOrDefault(username, username);
    }

    // -------------------------------------------------------------------------
    // PERSISTENCE
    // -------------------------------------------------------------------------

    /** Saves accounts and history to disk after every transaction. */
    public void saveAll() {
        try {
            ObjectOutputStream oosA = new ObjectOutputStream(new FileOutputStream(ACCOUNTS_FILE));
            oosA.writeObject(accounts);
            oosA.writeInt(accountCount);
            oosA.writeInt(Account.getAccountCounter());
            oosA.writeInt(Transaction.getTxCounter());
            oosA.close();

            ObjectOutputStream oosH = new ObjectOutputStream(new FileOutputStream(HISTORY_FILE));
            oosH.writeObject(history);
            oosH.close();
        } catch (IOException e) {
            ConsoleUI.warning("Could not save data: " + e.getMessage());
        }
    }

    /** Loads saved data. Returns false on first run. */
    @SuppressWarnings("unchecked")
    public boolean loadFromFile() {
        if (!new File(ACCOUNTS_FILE).exists()) return false;
        try {
            ObjectInputStream oisA = new ObjectInputStream(new FileInputStream(ACCOUNTS_FILE));
            accounts     = (Account[]) oisA.readObject();
            accountCount = oisA.readInt();
            Account.setAccountCounter(oisA.readInt());
            Transaction.setTxCounter(oisA.readInt());
            oisA.close();

            ObjectInputStream oisH = new ObjectInputStream(new FileInputStream(HISTORY_FILE));
            history = (Transaction[][]) oisH.readObject();
            oisH.close();
            return true;
        } catch (Exception e) {
            ConsoleUI.warning("Could not load saved data. Starting fresh.");
            accounts     = new Account[MAX_ACCOUNTS];
            history      = new Transaction[MAX_ACCOUNTS][Account.MAX_TX];
            accountCount = 0;
            return false;
        }
    }
}
