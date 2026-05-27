import java.io.Serializable;

/**
 * Account.java
 * Represents a CamFlow campus mobile money account.
 * Version 2.0 — adds status (STUDENT, STAFF, ADMINISTRATOR),
 * audit log (last 5 for users, 15 for admins), and last 5 transactions display.
 *
 * Institution : PKFokam Institute of Excellence
 * Course      : Java Lab — Mr. LELE Vaneck
 * Developer   : NGASSA DJONGA Christian Fredy
 *
 * OOP NOTE: Every field is PRIVATE — only accessible through getters/setters.
 *
 * @author NGASSA DJONGA Christian Fredy
 * @version 2.0 — Freshman 2, 2026
 */
public class Account implements Serializable {

    // -------------------------------------------------------------------------
    // ACCOUNT STATUS ENUM-STYLE CONSTANTS (Chapter 1 — named constants)
    // -------------------------------------------------------------------------
    public static final String STATUS_STUDENT       = "STUDENT";
    public static final String STATUS_STAFF         = "STAFF";
    public static final String STATUS_ADMINISTRATOR = "ADMINISTRATOR";

    // -------------------------------------------------------------------------
    // STATIC FIELDS
    // -------------------------------------------------------------------------
    private static int accountCounter = 0;
    public  static final int MAX_TX   = 50;

    // -------------------------------------------------------------------------
    // PRIVATE INSTANCE FIELDS
    // -------------------------------------------------------------------------

    /** OOP: private — read via getAccountNumber() */
    private String accountNumber;

    /** OOP: private — always stored UPPERCASE */
    private String ownerLastName;

    /** OOP: private — normal case */
    private String ownerFirstName;

    /**
     * OOP: THE most protected field — balance can ONLY change
     * through deposit() and withdraw() which enforce all rules.
     */
    private double balance;

    /** 'S' = Savings, 'C' = Current */
    private char accountType;

    /** How many transactions recorded so far */
    private int transactionCount;

    /** true = open, false = closed */
    private boolean isActive;

    /** PIN — minimum 4 characters */
    private String pin;

    /** One of 5 predefined security questions */
    private String securityQuestion;

    /** Stored lowercase for case-insensitive matching */
    private String securityAnswer;

    /** Student's major/department */
    private String major;

    /** Semester number */
    private int semester;

    /** Age */
    private int age;

    /**
     * Status: STUDENT, STAFF, or ADMINISTRATOR
     * Controls audit log size and menu permissions.
     */
    private String status;

    /**
     * Circular audit log — stores last 5 (user) or 15 (admin) actions.
     * Each entry is an AuditLog object with who/what/when.
     */
    private AuditLog[] auditLog;

    /** Current number of audit entries stored (up to maxAudit). */
    private int auditCount;

    /** Max audit entries: 5 for STUDENT/STAFF, 15 for ADMINISTRATOR. */
    private int maxAudit;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------

    /**
     * Parameterized constructor — creates a fully initialized Account.
     *
     * OOP NOTE — this keyword: "this.balance = balance" sets THIS object's
     * field from the constructor parameter of the same name.
     *
     * OOP NOTE — static counter: accountCounter++ runs on every new Account.
     *
     * @param accountNumber  auto-generated (e.g. CF-0001)
     * @param ownerLastName  stored UPPERCASE
     * @param ownerFirstName normal case
     * @param accountType    'S' Savings or 'C' Current
     * @param initialBalance opening deposit
     * @param pin            minimum 4 characters
     * @param major          department/major
     * @param semester       current semester
     * @param age            student/staff age
     * @param status         STUDENT, STAFF, or ADMINISTRATOR
     */
    public Account(String accountNumber, String ownerLastName, String ownerFirstName,
                   char accountType, double initialBalance, String pin,
                   String major, int semester, int age, String status) {
        this.accountNumber  = accountNumber;
        this.ownerLastName  = ownerLastName.toUpperCase();
        this.ownerFirstName = ownerFirstName;
        this.accountType    = accountType;
        this.balance        = initialBalance;
        this.pin            = pin;
        this.major          = major;
        this.semester       = semester;
        this.age            = age;
        this.status         = (status != null) ? status.toUpperCase() : STATUS_STUDENT;

        this.transactionCount = 0;
        this.isActive         = true;
        this.securityQuestion = "";
        this.securityAnswer   = "";

        // Audit log size depends on status
        this.maxAudit  = this.status.equals(STATUS_ADMINISTRATOR)
                         ? AuditLog.MAX_AUDIT_ADMIN : AuditLog.MAX_AUDIT_USER;
        this.auditLog  = new AuditLog[this.maxAudit];
        this.auditCount = 0;

        // OOP NOTE — static field increment
        accountCounter++;
    }

    // -------------------------------------------------------------------------
    // STATIC METHODS
    // -------------------------------------------------------------------------

    /** @return total accounts ever created */
    public static int getAccountCounter() { return accountCounter; }

    /** Restore counter from file */
    public static void setAccountCounter(int value) { accountCounter = value; }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    /** OOP: only way to read accountNumber from outside this class */
    public String  getAccountNumber()   { return accountNumber; }
    public String  getOwnerLastName()   { return ownerLastName; }
    public String  getOwnerFirstName()  { return ownerFirstName; }
    public String  getFullName()        { return ownerLastName + " " + ownerFirstName; }

    /** OOP: only way to read balance — cannot be modified directly */
    public double  getBalance()         { return balance; }
    public char    getAccountType()     { return accountType; }
    public int     getTransactionCount(){ return transactionCount; }
    public boolean isActive()           { return isActive; }
    public String  getMajor()           { return major; }
    public int     getSemester()        { return semester; }
    public int     getAge()             { return age; }
    public String  getStatus()          { return status; }
    public String  getSecurityQuestion(){ return securityQuestion; }
    public int     getAuditCount()      { return auditCount; }
    public AuditLog[] getAuditLog()     { return auditLog; }

    // -------------------------------------------------------------------------
    // SETTERS
    // -------------------------------------------------------------------------

    /** OOP: setActive is the ONLY way to open/close an account from outside */
    public void setActive(boolean active)           { this.isActive = active; }
    public void setPin(String pin)                  { this.pin = pin; }
    public void setSecurityQuestion(String q)       { this.securityQuestion = q; }
    public void setSecurityAnswer(String a)         { this.securityAnswer = a.toLowerCase(); }
    public void setStatus(String status)            {
        this.status   = status.toUpperCase();
        this.maxAudit = this.status.equals(STATUS_ADMINISTRATOR)
                        ? AuditLog.MAX_AUDIT_ADMIN : AuditLog.MAX_AUDIT_USER;
        // Resize audit array if needed
        AuditLog[] newLog = new AuditLog[this.maxAudit];
        int copy = Math.min(auditCount, this.maxAudit);
        for (int i = 0; i < copy; i++) newLog[i] = auditLog[i];
        this.auditLog  = newLog;
        this.auditCount = copy;
    }

    // -------------------------------------------------------------------------
    // AUDIT LOG
    // -------------------------------------------------------------------------

    /**
     * Records an action in the circular audit log.
     * When full, oldest entry is dropped (shift left) and new one appended.
     *
     * @param performedBy account number or admin username
     * @param action      action label (e.g. "LOGIN")
     * @param detail      extra context (may be empty)
     */
    public void recordAudit(String performedBy, String action, String detail) {
        if (auditCount < maxAudit) {
            auditLog[auditCount++] = new AuditLog(performedBy, action, detail);
        } else {
            // Shift left to drop oldest — circular buffer
            for (int i = 0; i < maxAudit - 1; i++) {
                auditLog[i] = auditLog[i + 1];
            }
            auditLog[maxAudit - 1] = new AuditLog(performedBy, action, detail);
        }
    }

    /**
     * Prints the full audit log for this account.
     * Chapter 3 — for loop.
     */
    public void printAuditLog() {
        String label = status.equals(STATUS_ADMINISTRATOR) ? "Last 15 Actions" : "Last 5 Actions";
        ConsoleUI.printHeader(label + " — " + accountNumber + " (" + getFullName() + ")",
                ConsoleUI.YELLOW);
        if (auditCount == 0) {
            ConsoleUI.info("No actions recorded yet.");
            return;
        }
        System.out.println(ConsoleUI.YELLOW
                + String.format("  %-19s %-20s %s", "TIMESTAMP", "ACTION", "DETAIL")
                + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        for (int i = 0; i < auditCount; i++) {
            auditLog[i].print();
        }
    }

    // -------------------------------------------------------------------------
    // BUSINESS LOGIC
    // -------------------------------------------------------------------------

    /**
     * Deposits amount into this account.
     * OOP: only way balance can increase — encapsulation in action.
     *
     * @param amount must be positive
     * @return true if successful
     */
    public boolean deposit(double amount) {
        if (amount <= 0) return false;
        this.balance += amount;
        this.transactionCount++;
        return true;
    }

    /**
     * Withdraws totalDebit (amount + fee) from this account.
     * OOP: only way balance can decrease.
     *
     * @param totalDebit amount + fee combined
     * @return true if sufficient funds (respecting MIN_BALANCE)
     */
    public boolean withdraw(double totalDebit) {
        if (this.balance - totalDebit < CamFlowSystem.MIN_BALANCE) return false;
        this.balance -= totalDebit;
        this.transactionCount++;
        return true;
    }

    /**
     * Applies monthly interest (Savings accounts only).
     * Formula: balance += balance * SAVINGS_INTEREST_RATE / 100
     */
    public void applyMonthlyInterest() {
        if (this.accountType == 'S') {
            double interest = this.balance * CamFlowSystem.SAVINGS_INTEREST_RATE / 100.0;
            this.balance += interest;
            this.transactionCount++;
        }
    }

    // -------------------------------------------------------------------------
    // AUTHENTICATION
    // -------------------------------------------------------------------------

    /** @return true if inputPin matches stored PIN */
    public boolean checkPIN(String inputPin)       { return this.pin.equals(inputPin); }

    /** @return true if answer matches stored security answer (case-insensitive) */
    public boolean checkSecurityAnswer(String ans) { return this.securityAnswer.equals(ans.toLowerCase()); }

    /** @return true if security question and answer are both set */
    public boolean hasSecurityAnswer()             { return !securityQuestion.isEmpty() && !securityAnswer.isEmpty(); }

    // -------------------------------------------------------------------------
    // DISPLAY
    // -------------------------------------------------------------------------

    /**
     * Prints a centered, colored account summary.
     */
    public void displaySummary() {
        String typeStr   = (accountType == 'S') ? "Savings" : "Current";
        String statusStr = isActive ? "ACTIVE" : "CLOSED";
        String statusColor;
        switch (status) {
            case STATUS_ADMINISTRATOR: statusColor = ConsoleUI.MAGENTA; break;
            case STATUS_STAFF:         statusColor = ConsoleUI.YELLOW;  break;
            default:                   statusColor = ConsoleUI.CYAN;
        }

        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN    + ConsoleUI.center("Account  : " + accountNumber) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE   + ConsoleUI.center("Owner    : " + getFullName())  + ConsoleUI.RESET);
        System.out.println(statusColor       + ConsoleUI.center("Status   : " + status)         + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW  + ConsoleUI.center("Type     : " + typeStr)        + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN   + ConsoleUI.center("Balance  : " + ConsoleUI.formatAmount(balance)) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW  + ConsoleUI.center("Major    : " + major)          + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW  + ConsoleUI.center("Semester : " + semester + "  |  Age: " + age) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE   + ConsoleUI.center("Tx Count : " + transactionCount) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE   + ConsoleUI.center("Account  : " + statusStr)     + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW  + ConsoleUI.center("Security : " + (hasSecurityAnswer() ? "Set" : "Not set")) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
    }
}
