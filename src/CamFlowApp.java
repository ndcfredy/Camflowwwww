import java.util.Scanner;

/**
 * CamFlowApp.java
 * Entry point for CamFlow v2.0 — Campus Mobile Money & Savings System.
 *
 * NEW in v2.0:
 *   - Status system: STUDENT, STAFF, ADMINISTRATOR
 *   - Audit log: last 5 actions (users) / 15 actions (admins)
 *   - Last 5 transactions quick view
 *   - Self-service for Students/Staff (own account only)
 *   - Admins operate on ANY account
 *   - Account closure blocked if balance > 0
 *   - Updated logo and branding
 *
 * Institution : PKFokam Institute of Excellence
 * Course      : Java Lab — Mr. LELE Vaneck
 * Developer   : NGASSA DJONGA Christian Fredy
 *
 * @author NGASSA DJONGA Christian Fredy
 * @version 2.0 — Freshman 2, 2026
 */
public class CamFlowApp {

    // -------------------------------------------------------------------------
    // CONSTANTS (Chapter 1)
    // -------------------------------------------------------------------------
    private static final int    MAX_LOGIN_ATTEMPTS = 3;
    private static final String APP_VERSION        = "v2.0";
    private static final String APP_NAME           = "CamFlow -- Campus Mobile Money & Savings";

    // -------------------------------------------------------------------------
    // FIELDS
    // -------------------------------------------------------------------------
    private static CamFlowSystem system;
    private static Scanner       sc                 = new Scanner(System.in);
    private static String        loggedInAccountNum = null;
    private static String        loggedInAdmin      = null;

    // -------------------------------------------------------------------------
    // MAIN (Chapter 1 — entry point)
    // -------------------------------------------------------------------------

    /**
     * Application entry point. Shows splash, initializes system, runs login loop.
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        showSplash();
        system = new CamFlowSystem();

        // Chapter 3 — while loop: main login loop
        boolean running = true;
        while (running) {
            int choice = showMainMenu();
            // Chapter 3 — switch
            switch (choice) {
                case 1: handleAccountHolderLogin(); break;
                case 2: handleAdminLogin();          break;
                case 3: handleForgotPIN();           break;
                case 4: showAbout();                 break;
                case 0:
                    printGoodbye();
                    running = false;   // Chapter 3 — break on exit
                    break;
                default: ConsoleUI.error("Invalid choice.");
            }
        }
        sc.close();
    }

    // =========================================================================
    // SPLASH SCREEN  (new logo v2.0)
    // =========================================================================

    /**
     * Displays the ASCII art splash screen.
     * Chapter 1 — welcome banner.
     */
    private static void showSplash() {
        System.out.println(ConsoleUI.BOLD_BLUE  + ConsoleUI.thick() + ConsoleUI.RESET);
        ConsoleUI.blank();
        // CamFlow ASCII logo — only standard keyboard characters
        System.out.println(ConsoleUI.BOLD_CYAN  + ConsoleUI.center("  .---|.  .--.  .-.   .---. .-.   .---. .--. .-. .")  + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BOLD_CYAN  + ConsoleUI.center("  | |-' / /  \\ |  `-.| |-' | |   | |-'/ /  \\| | |")  + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN       + ConsoleUI.center("  | |  / /----\\| .` || |   | |   | | / /----\\ | |")   + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN       + ConsoleUI.center("  `-'  `-'    `'-'  '`-'   `-'   `-' `-'    ``---'")  + ConsoleUI.RESET);
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_YELLOW + ConsoleUI.center("*  PKFokam Institute of Excellence  *")  + ConsoleUI.RESET);
        System.out.println(ConsoleUI.WHITE       + ConsoleUI.center("Campus Mobile Money & Savings System")   + ConsoleUI.RESET);
        System.out.println(ConsoleUI.CYAN        + ConsoleUI.center("HashMap + File Persistence  |  " + APP_VERSION) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW      + ConsoleUI.center("Java Lab — Mr. LELE Vaneck")             + ConsoleUI.RESET);
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BLUE + ConsoleUI.center("[ STUDENT ]  [ STAFF ]  [ ADMINISTRATOR ]") + ConsoleUI.RESET);
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE  + ConsoleUI.thick() + ConsoleUI.RESET);
        pause(1200);
    }

    /** Shows the About page. */
    private static void showAbout() {
        ConsoleUI.printHeader("About CamFlow " + APP_VERSION, ConsoleUI.BOLD_CYAN);
        ConsoleUI.blank();
        ConsoleUI.printCenter(ConsoleUI.CYAN,    APP_NAME + " " + APP_VERSION);
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "PKFokam Institute of Excellence");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "Dept. of Computer Engineering and Technology");
        ConsoleUI.blank();
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "Course  : Java Lab");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "Lecturer: Mr. LELE Vaneck");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "End of Semester Project -- Freshman 2 -- 2026");
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_GREEN, "Developed by:");
        ConsoleUI.printCenter(ConsoleUI.GREEN,      "NGASSA DJONGA Christian Fredy");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.blank();
        ConsoleUI.printCenter(ConsoleUI.WHITE, "CamFlow simulates mobile money services like");
        ConsoleUI.printCenter(ConsoleUI.WHITE, "MTN MoMo and Orange Money for a university");
        ConsoleUI.printCenter(ConsoleUI.WHITE, "campus in Cameroon. Three user roles:");
        ConsoleUI.printCenter(ConsoleUI.CYAN,  "STUDENT | STAFF | ADMINISTRATOR");
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, "Technologies:");
        ConsoleUI.printCenter(ConsoleUI.CYAN,    "Java | OOP | Arrays | HashMap | File I/O | ANSI");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        pressEnter();
    }

    // =========================================================================
    // MAIN LOGIN MENU
    // =========================================================================

    /** @return chosen menu option */
    private static int showMainMenu() {
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN,  "CamFlow " + APP_VERSION + " -- Main Menu");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,     "PKFokam Institute of Excellence");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "1. Account Holder Login  (Student / Staff / Admin)");
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, "2. System Admin Login");
        ConsoleUI.printCenter(ConsoleUI.CYAN,    "3. Forgot PIN");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "4. About CamFlow");
        ConsoleUI.printCenter(ConsoleUI.RED,     "0. Exit");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        return getValidInt(sc, "Enter choice", 0, 4);
    }

    // =========================================================================
    // ACCOUNT HOLDER LOGIN
    // =========================================================================

    /**
     * Login flow for Students, Staff, and Administrator account holders.
     * After login, routes to the correct menu based on status.
     */
    private static void handleAccountHolderLogin() {
        ConsoleUI.printHeader("Account Holder Login", ConsoleUI.CYAN);
        ConsoleUI.prompt("Account Number (e.g. CF-0001)");
        String accNum = sc.nextLine().trim().toUpperCase();

        int idx = system.findAccountByNumber(accNum);
        if (idx == -1) { ConsoleUI.error("Account not found: " + accNum); return; }
        Account a = system.getAccount(idx);
        if (!a.isActive()) { ConsoleUI.error("This account is closed."); return; }

        // Chapter 3 — for loop for login attempts
        boolean authenticated = false;
        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            ConsoleUI.prompt("Enter PIN");
            String pin = sc.nextLine().trim();
            if (a.checkPIN(pin)) {
                authenticated = true;
                break;
            }
            int remaining = MAX_LOGIN_ATTEMPTS - attempt - 1;
            // Record failed attempt in audit log
            a.recordAudit(accNum, "LOGIN_FAILED",
                    "Attempt " + (attempt + 1) + " of " + MAX_LOGIN_ATTEMPTS);
            if (remaining > 0)
                ConsoleUI.error("Wrong PIN. " + remaining + " attempt(s) remaining.");
            else
                ConsoleUI.error("Too many failed attempts. Session locked.");
        }

        if (!authenticated) { system.saveAll(); return; }

        // Record successful login
        a.recordAudit(accNum, "LOGIN", "Session started");
        system.saveAll();

        loggedInAccountNum = accNum;

        // Welcome — LASTNAME uppercase, firstname normal
        String statusColor = getStatusColor(a.getStatus());
        System.out.println(ConsoleUI.BOLD_GREEN
                + ConsoleUI.center("Welcome, " + a.getOwnerLastName() + " " + a.getOwnerFirstName() + "!")
                + ConsoleUI.RESET);
        System.out.println(statusColor
                + ConsoleUI.center("[ " + a.getStatus() + " ]")
                + ConsoleUI.RESET);
        pause(800);

        // Route to correct menu by status
        switch (a.getStatus()) {
            case Account.STATUS_ADMINISTRATOR:
                runAdminAccountSession();
                break;
            default:
                runUserSession(); // STUDENT and STAFF get same menu
        }

        // Record logout
        a.recordAudit(accNum, "LOGOUT", "Session ended");
        system.saveAll();
        loggedInAccountNum = null;
    }

    // =========================================================================
    // USER SESSION (Student & Staff — own account only)
    // =========================================================================

    /**
     * Session menu for STUDENT and STAFF — all operations on their OWN account.
     * Chapter 3 — while loop.
     */
    private static void runUserSession() {
        boolean active = true;
        while (active) {
            int choice = showUserMenu();
            switch (choice) {
                case 1: handleTransactionMenu();    break;
                case 2: handleHistoryMenu();         break;
                case 3: showMyProfile();             break;
                case 4: handleReportMenu();          break;
                case 5: handleApplyMyInterest();     break;
                case 6: handleChangePIN();           break;
                case 7: handleSetSecurityQuestion(); break;
                case 0: active = false;              break;
                default: ConsoleUI.error("Invalid choice.");
            }
        }
    }

    /** Displays the user session menu. */
    private static int showUserMenu() {
        int idx    = system.findAccountByNumber(loggedInAccountNum);
        Account a  = system.getAccount(idx);
        String sc2 = getStatusColor(a.getStatus());

        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN,  "My CamFlow Menu");
        ConsoleUI.printCenter(sc2, a.getOwnerLastName() + " " + a.getOwnerFirstName()
                + "  [ " + a.getStatus() + " ]");
        ConsoleUI.printCenter(ConsoleUI.GREEN,  "Balance: " + ConsoleUI.formatAmount(a.getBalance()));
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.GREEN,   "1. Transactions  (Deposit / Withdraw / Transfer)");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "2. Transaction History");
        ConsoleUI.printCenter(ConsoleUI.CYAN,    "3. My Profile");
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, "4. Reports & Statistics  (my account)");
        ConsoleUI.printCenter(ConsoleUI.GREEN,   "5. Apply Monthly Interest  (my account)");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "6. Change My PIN");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "7. Set Security Question");
        ConsoleUI.printCenter(ConsoleUI.RED,     "0. Logout");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        return getValidInt(sc, "Enter choice", 0, 7);
    }

    // =========================================================================
    // ADMINISTRATOR ACCOUNT SESSION (own account holder who is ADMINISTRATOR)
    // =========================================================================

    /**
     * Session for an account holder whose status is ADMINISTRATOR.
     * Can operate on ANY account. Identical capability to System Admin
     * but authenticated through their account number + PIN.
     */
    private static void runAdminAccountSession() {
        boolean active = true;
        while (active) {
            int choice = showAdminAccountMenu();
            switch (choice) {
                case 1: handleAccountMenu();              break;
                case 2: handleAdminTransactionMenu();     break;
                case 3: handleAdminHistoryMenu();         break;
                case 4: handleReportMenu();               break;
                case 5:
                    ConsoleUI.printHeader("Apply Monthly Interest — All Savings", ConsoleUI.GREEN);
                    system.applyInterestToAll(loggedInAccountNum);
                    pressEnter();
                    break;
                case 6: handleChangePIN();                break;
                case 7: handleSetSecurityQuestion();      break;
                case 8: showAuditLog(loggedInAccountNum); break;
                case 0: active = false;                   break;
                default: ConsoleUI.error("Invalid choice.");
            }
        }
    }

    /** Admin account session menu. */
    private static int showAdminAccountMenu() {
        int idx   = system.findAccountByNumber(loggedInAccountNum);
        Account a = system.getAccount(idx);
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_MAGENTA, "Administrator Menu");
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, a.getFullName() + "  [ ADMINISTRATOR ]");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "1. Account Management  (any account)");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "2. Transactions  (any account)");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "3. Transaction History  (any account)");
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, "4. Reports & Statistics");
        ConsoleUI.printCenter(ConsoleUI.GREEN,   "5. Apply Monthly Interest  (all Savings)");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "6. Change My PIN");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,  "7. Set Security Question");
        ConsoleUI.printCenter(ConsoleUI.CYAN,    "8. My Audit Log  (last 15 actions)");
        ConsoleUI.printCenter(ConsoleUI.RED,     "0. Logout");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        return getValidInt(sc, "Enter choice", 0, 8);
    }

    // =========================================================================
    // SYSTEM ADMIN LOGIN (separate — admin1 / admin2)
    // =========================================================================

    /** System admin login with username + PIN (not account number). */
    private static void handleAdminLogin() {
        ConsoleUI.printHeader("System Admin Login", ConsoleUI.MAGENTA);
        ConsoleUI.prompt("Admin Username");
        String username = sc.nextLine().trim();

        boolean authenticated = false;
        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            ConsoleUI.prompt("Admin PIN");
            String pin = sc.nextLine().trim();
            if (system.checkAdminCredentials(username, pin)) {
                authenticated = true;
                break;
            }
            int remaining = MAX_LOGIN_ATTEMPTS - attempt - 1;
            if (remaining > 0)
                ConsoleUI.error("Wrong credentials. " + remaining + " attempt(s) remaining.");
            else
                ConsoleUI.error("Too many failed attempts.");
        }

        if (!authenticated) return;

        loggedInAdmin = username;
        System.out.println(ConsoleUI.BOLD_GREEN
                + ConsoleUI.center("Welcome, " + system.getAdminName(username) + "!")
                + ConsoleUI.RESET);
        System.out.println(ConsoleUI.MAGENTA + ConsoleUI.center("[ SYSTEM ADMIN ]") + ConsoleUI.RESET);
        pause(800);
        runSystemAdminSession();
        loggedInAdmin = null;
    }

    /** System admin session loop. */
    private static void runSystemAdminSession() {
        boolean active = true;
        while (active) {
            int choice = showSystemAdminMenu();
            switch (choice) {
                case 1: handleAccountMenu();          break;
                case 2: handleAdminTransactionMenu(); break;
                case 3: handleAdminHistoryMenu();     break;
                case 4: handleReportMenu();           break;
                case 5:
                    ConsoleUI.printHeader("Apply Monthly Interest — All Savings", ConsoleUI.GREEN);
                    system.applyInterestToAll(loggedInAdmin);
                    pressEnter();
                    break;
                case 0: active = false; ConsoleUI.info("System admin logged out."); break;
                default: ConsoleUI.error("Invalid choice.");
            }
        }
    }

    private static int showSystemAdminMenu() {
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_MAGENTA, "System Admin — " + system.getAdminName(loggedInAdmin));
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "1. Account Management");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "2. Transactions  (any account)");
        ConsoleUI.printCenter(ConsoleUI.WHITE,   "3. Transaction History  (any account)");
        ConsoleUI.printCenter(ConsoleUI.MAGENTA, "4. Reports & Statistics");
        ConsoleUI.printCenter(ConsoleUI.GREEN,   "5. Apply Monthly Interest  (all Savings)");
        ConsoleUI.printCenter(ConsoleUI.RED,     "0. Logout");
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        return getValidInt(sc, "Enter choice", 0, 5);
    }

    // =========================================================================
    // ACCOUNT MANAGEMENT  (admin only)
    // =========================================================================

    /** Account management sub-menu. */
    private static void handleAccountMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN, "Account Management");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.WHITE, "1. Open New Account");
            ConsoleUI.printCenter(ConsoleUI.WHITE, "2. List All Accounts");
            ConsoleUI.printCenter(ConsoleUI.CYAN,  "3. Search Account");
            ConsoleUI.printCenter(ConsoleUI.RED,   "4. Close Account  (balance must be 0)");
            ConsoleUI.printCenter(ConsoleUI.CYAN,  "5. View Account Audit Log");
            ConsoleUI.printCenter(ConsoleUI.YELLOW,"0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 5);
            switch (choice) {
                case 1: openNewAccount();  break;
                case 2: system.listAllAccounts(); pressEnter(); break;
                case 3: searchAccount();   break;
                case 4: closeAccount();    break;
                case 5:
                    ConsoleUI.prompt("Account Number for Audit Log");
                    String num = sc.nextLine().trim().toUpperCase();
                    showAuditLog(num);
                    break;
                case 0: back = true; break;
            }
        }
    }

    /** Opens a new account (admin only). */
    private static void openNewAccount() {
        ConsoleUI.printHeader("Open New Account", ConsoleUI.GREEN);

        ConsoleUI.prompt("Last Name");
        String lastName = sc.nextLine().trim();

        ConsoleUI.prompt("First Name");
        String firstName = sc.nextLine().trim();

        // Chapter 3 — do-while for account type validation
        char accType;
        do {
            ConsoleUI.prompt("Account Type  S=Savings / C=Current");
            String inp = sc.nextLine().trim().toUpperCase();
            accType = inp.isEmpty() ? ' ' : inp.charAt(0);
            if (accType != 'S' && accType != 'C') ConsoleUI.error("Enter S or C.");
        } while (accType != 'S' && accType != 'C');

        // Status selection
        ConsoleUI.printCenter(ConsoleUI.YELLOW, "Account Status:");
        ConsoleUI.printCenter(ConsoleUI.CYAN,   "1. STUDENT   2. STAFF   3. ADMINISTRATOR");
        int statusChoice = getValidInt(sc, "Status", 1, 3);
        String status;
        switch (statusChoice) {
            case 2:  status = Account.STATUS_STAFF;         break;
            case 3:  status = Account.STATUS_ADMINISTRATOR; break;
            default: status = Account.STATUS_STUDENT;
        }

        double opening = getValidDouble(sc, "Opening Deposit min 500 FCFA", 500.0, 10_000_000.0);

        String pin;
        do {
            ConsoleUI.prompt("Set PIN  (min 4 characters)");
            pin = sc.nextLine().trim();
            if (pin.length() < 4) ConsoleUI.error("PIN must be at least 4 characters.");
        } while (pin.length() < 4);

        ConsoleUI.prompt("Major / Department");
        String major    = sc.nextLine().trim();
        int    semester = getValidInt(sc, "Semester", 1, 10);
        int    age      = getValidInt(sc, "Age", 15, 60);

        // Auto-generate account number CF-XXXX
        String accNum = String.format("CF-%04d", Account.getAccountCounter() + 1);
        Account newAcc = new Account(accNum, lastName, firstName, accType,
                opening, pin, major, semester, age, status);

        if (system.addAccount(newAcc)) {
            String performer = (loggedInAdmin != null) ? loggedInAdmin : loggedInAccountNum;
            newAcc.recordAudit(performer, "ACCOUNT_OPENED", "Opened by " + performer);
            ConsoleUI.success("Account " + accNum + " created — " + newAcc.getFullName()
                    + " [" + status + "]");
            newAcc.displaySummary();
            system.saveAll();
        } else {
            ConsoleUI.error("System full. Cannot add more accounts.");
        }
        pressEnter();
    }

    /** Searches and displays an account summary. */
    private static void searchAccount() {
        ConsoleUI.printHeader("Search Account", ConsoleUI.CYAN);
        ConsoleUI.prompt("Account Number");
        String num = sc.nextLine().trim().toUpperCase();
        int idx = system.findAccountByNumber(num);
        if (idx == -1) ConsoleUI.error("Account not found: " + num);
        else system.getAccount(idx).displaySummary();
        pressEnter();
    }

    /**
     * Closes an account — blocked if balance > 0 FCFA.
     * The user must withdraw all funds first.
     */
    private static void closeAccount() {
        ConsoleUI.printHeader("Close Account", ConsoleUI.RED);
        ConsoleUI.prompt("Account Number to Close");
        String num = sc.nextLine().trim().toUpperCase();
        String performer = (loggedInAdmin != null) ? loggedInAdmin : loggedInAccountNum;
        system.closeAccount(num, performer);
        pressEnter();
    }

    /** Shows the audit log for any account. */
    private static void showAuditLog(String num) {
        int idx = system.findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found: " + num); return; }
        system.getAccount(idx).printAuditLog();
        pressEnter();
    }

    // =========================================================================
    // TRANSACTION MENU  (user — own account)
    // =========================================================================

    /**
     * Transaction sub-menu for logged-in user (own account only).
     * Chapter 4 — handleTransactionMenu() required method.
     */
    private static void handleTransactionMenu() {
        boolean back = false;
        while (!back) {
            // Show last 5 transactions as context
            system.printLastTransactions(loggedInAccountNum);
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN, "Transactions");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.GREEN,   "1. Deposit");
            ConsoleUI.printCenter(ConsoleUI.RED,     "2. Withdrawal  (PIN required)");
            ConsoleUI.printCenter(ConsoleUI.MAGENTA, "3. Transfer    (PIN required)");
            ConsoleUI.printCenter(ConsoleUI.YELLOW,  "0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 3);
            switch (choice) {
                case 1:
                    double dep = getValidDouble(sc, "Deposit amount FCFA", 1.0, 10_000_000.0);
                    system.performDeposit(loggedInAccountNum, dep);
                    pressEnter();
                    break;
                case 2:
                    double wd = getValidDouble(sc, "Withdrawal amount FCFA", 1.0, 10_000_000.0);
                    if (confirmPIN()) system.performWithdrawal(loggedInAccountNum, wd);
                    else ConsoleUI.error("PIN verification failed. Cancelled.");
                    pressEnter();
                    break;
                case 3:
                    ConsoleUI.prompt("Recipient Account Number");
                    String toNum = sc.nextLine().trim().toUpperCase();
                    double amt   = getValidDouble(sc, "Transfer amount FCFA", 1.0, 10_000_000.0);
                    if (confirmPIN()) system.performTransfer(loggedInAccountNum, toNum, amt);
                    else ConsoleUI.error("PIN verification failed. Cancelled.");
                    pressEnter();
                    break;
                case 0: back = true; break;
            }
        }
    }

    /** Admin transaction menu — operates on any account. */
    private static void handleAdminTransactionMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_MAGENTA, "Admin Transactions  (any account)");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.GREEN,   "1. Deposit");
            ConsoleUI.printCenter(ConsoleUI.RED,     "2. Withdrawal");
            ConsoleUI.printCenter(ConsoleUI.MAGENTA, "3. Transfer");
            ConsoleUI.printCenter(ConsoleUI.YELLOW,  "0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 3);
            if (choice == 0) { back = true; continue; }

            ConsoleUI.prompt("Account Number");
            String num = sc.nextLine().trim().toUpperCase();

            switch (choice) {
                case 1:
                    double d = getValidDouble(sc, "Amount FCFA", 1.0, 10_000_000.0);
                    system.performDeposit(num, d);
                    pressEnter();
                    break;
                case 2:
                    double w = getValidDouble(sc, "Amount FCFA", 1.0, 10_000_000.0);
                    system.performWithdrawal(num, w);
                    pressEnter();
                    break;
                case 3:
                    ConsoleUI.prompt("To Account Number");
                    String to = sc.nextLine().trim().toUpperCase();
                    double t  = getValidDouble(sc, "Amount FCFA", 1.0, 10_000_000.0);
                    system.performTransfer(num, to, t);
                    pressEnter();
                    break;
            }
        }
    }

    // =========================================================================
    // HISTORY MENU
    // =========================================================================

    /**
     * Transaction history sub-menu for logged-in user.
     * Chapter 4 — handleHistoryMenu() required method.
     */
    private static void handleHistoryMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN, "Transaction History");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.WHITE, "1. Last 5 Transactions");
            ConsoleUI.printCenter(ConsoleUI.WHITE, "2. Full History  (table)");
            ConsoleUI.printCenter(ConsoleUI.WHITE, "3. Full Statement  (receipts)");
            ConsoleUI.printCenter(ConsoleUI.CYAN,  "4. My Audit Log  (last 5 actions)");
            ConsoleUI.printCenter(ConsoleUI.YELLOW,"0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 4);
            switch (choice) {
                case 1: system.printLastTransactions(loggedInAccountNum); pressEnter(); break;
                case 2: system.printAccountHistory(loggedInAccountNum);   pressEnter(); break;
                case 3: system.printFullStatement(loggedInAccountNum);    pressEnter(); break;
                case 4: showAuditLog(loggedInAccountNum);                              break;
                case 0: back = true; break;
            }
        }
    }

    /** Admin history menu — any account. */
    private static void handleAdminHistoryMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_MAGENTA, "Admin — Transaction History");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.WHITE, "1. Last 5 Transactions");
            ConsoleUI.printCenter(ConsoleUI.WHITE, "2. Full History  (table)");
            ConsoleUI.printCenter(ConsoleUI.WHITE, "3. Full Statement  (receipts)");
            ConsoleUI.printCenter(ConsoleUI.CYAN,  "4. Account Audit Log");
            ConsoleUI.printCenter(ConsoleUI.YELLOW,"0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 4);
            if (choice == 0) { back = true; continue; }
            ConsoleUI.prompt("Account Number");
            String num = sc.nextLine().trim().toUpperCase();
            switch (choice) {
                case 1: system.printLastTransactions(num); pressEnter(); break;
                case 2: system.printAccountHistory(num);   pressEnter(); break;
                case 3: system.printFullStatement(num);    pressEnter(); break;
                case 4: showAuditLog(num);                              break;
            }
        }
    }

    // =========================================================================
    // REPORTS MENU
    // =========================================================================

    /**
     * Reports sub-menu — available to ALL users (own account stats for users,
     * full system stats for admins).
     * Chapter 4 — handleReportMenu() required method.
     */
    private static void handleReportMenu() {
        boolean isAdmin = isCurrentUserAdmin();
        boolean back    = false;
        while (!back) {
            ConsoleUI.blank();
            System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
            ConsoleUI.printCenter(ConsoleUI.BOLD_MAGENTA, "Reports & Statistics");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
            if (isAdmin) {
                ConsoleUI.printCenter(ConsoleUI.WHITE,   "1. System Statistics  (all accounts)");
                ConsoleUI.printCenter(ConsoleUI.WHITE,   "2. List All Active Accounts");
                ConsoleUI.printCenter(ConsoleUI.MAGENTA, "3. Full Statement  (any account)");
            } else {
                ConsoleUI.printCenter(ConsoleUI.WHITE,   "1. My Account Statistics");
                ConsoleUI.printCenter(ConsoleUI.WHITE,   "2. My Account Summary");
                ConsoleUI.printCenter(ConsoleUI.MAGENTA, "3. My Full Statement");
            }
            ConsoleUI.printCenter(ConsoleUI.YELLOW, "0. Back");
            System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);

            int choice = getValidInt(sc, "Enter choice", 0, 3);
            switch (choice) {
                case 1:
                    if (isAdmin) { system.printSystemStats(); pressEnter(); }
                    else         { system.printAccountHistory(loggedInAccountNum); pressEnter(); }
                    break;
                case 2:
                    if (isAdmin) { system.listAllAccounts(); pressEnter(); }
                    else         { system.getAccount(system.findAccountByNumber(loggedInAccountNum)).displaySummary(); pressEnter(); }
                    break;
                case 3:
                    if (isAdmin) {
                        ConsoleUI.prompt("Account Number");
                        String num = sc.nextLine().trim().toUpperCase();
                        system.printFullStatement(num);
                    } else {
                        system.printFullStatement(loggedInAccountNum);
                    }
                    pressEnter();
                    break;
                case 0: back = true; break;
            }
        }
    }

    // =========================================================================
    // MONTHLY INTEREST (self-service for user, any account for admin)
    // =========================================================================

    /** Applies interest to the logged-in user's own account. */
    private static void handleApplyMyInterest() {
        ConsoleUI.printHeader("Apply Monthly Interest", ConsoleUI.GREEN);
        system.applyInterestToOne(loggedInAccountNum, loggedInAccountNum);
        pressEnter();
    }

    // =========================================================================
    // PROFILE & SECURITY
    // =========================================================================

    /** Displays the logged-in user's profile. */
    private static void showMyProfile() {
        ConsoleUI.printHeader("My Profile", ConsoleUI.CYAN);
        system.getAccount(system.findAccountByNumber(loggedInAccountNum)).displaySummary();
        pressEnter();
    }

    /** Handles PIN change — current PIN must be confirmed first. */
    private static void handleChangePIN() {
        ConsoleUI.printHeader("Change PIN", ConsoleUI.YELLOW);
        int idx = system.findAccountByNumber(loggedInAccountNum);
        Account a = system.getAccount(idx);

        ConsoleUI.prompt("Current PIN");
        String currentPin = sc.nextLine().trim();
        if (!a.checkPIN(currentPin)) { ConsoleUI.error("Incorrect current PIN."); return; }

        String newPin;
        do {
            ConsoleUI.prompt("New PIN  (min 4 characters)");
            newPin = sc.nextLine().trim();
            if (newPin.length() < 4) ConsoleUI.error("PIN must be at least 4 characters.");
        } while (newPin.length() < 4);

        ConsoleUI.prompt("Confirm New PIN");
        String confirm = sc.nextLine().trim();
        if (!newPin.equals(confirm)) { ConsoleUI.error("PINs do not match."); return; }

        a.setPin(newPin);
        a.recordAudit(loggedInAccountNum, "PIN_CHANGE", "PIN updated successfully");
        system.saveAll();
        ConsoleUI.success("PIN changed successfully.");
        pressEnter();
    }

    /** Lets user set or update their security question from 5 predefined options. */
    private static void handleSetSecurityQuestion() {
        ConsoleUI.printHeader("Set Security Question", ConsoleUI.YELLOW);
        String[] questions = {
            "What is your mother's maiden name?",
            "What was the name of your first school?",
            "What is your childhood nickname?",
            "What is the name of your hometown?",
            "What was your first pet's name?"
        };
        // Chapter 3 — for loop
        for (int i = 0; i < questions.length; i++)
            ConsoleUI.printCenter(ConsoleUI.WHITE, (i + 1) + ". " + questions[i]);
        ConsoleUI.blank();

        int q = getValidInt(sc, "Choose question 1-5", 1, 5);
        ConsoleUI.prompt("Your Answer");
        String answer = sc.nextLine().trim();

        int idx   = system.findAccountByNumber(loggedInAccountNum);
        Account a = system.getAccount(idx);
        a.setSecurityQuestion(questions[q - 1]);
        a.setSecurityAnswer(answer);
        a.recordAudit(loggedInAccountNum, "SECURITY_UPDATED", "Security question set");
        system.saveAll();
        ConsoleUI.success("Security question set successfully.");
        pressEnter();
    }

    /** Forgot PIN flow: security question with 3 attempts. */
    private static void handleForgotPIN() {
        ConsoleUI.printHeader("Forgot PIN", ConsoleUI.CYAN);
        ConsoleUI.prompt("Your Account Number");
        String num = sc.nextLine().trim().toUpperCase();

        int idx = system.findAccountByNumber(num);
        if (idx == -1) { ConsoleUI.error("Account not found."); return; }
        Account a = system.getAccount(idx);
        if (!a.hasSecurityAnswer()) {
            ConsoleUI.warning("No security question set. Contact an administrator.");
            return;
        }

        ConsoleUI.printCenter(ConsoleUI.CYAN,  "Security Question:");
        ConsoleUI.printCenter(ConsoleUI.WHITE,  a.getSecurityQuestion());
        ConsoleUI.blank();

        boolean answered = false;
        for (int attempt = 0; attempt < MAX_LOGIN_ATTEMPTS; attempt++) {
            ConsoleUI.prompt("Your Answer");
            String ans = sc.nextLine().trim();
            if (a.checkSecurityAnswer(ans)) { answered = true; break; }
            int rem = MAX_LOGIN_ATTEMPTS - attempt - 1;
            if (rem > 0) ConsoleUI.error("Wrong answer. " + rem + " attempt(s) left.");
            else         ConsoleUI.error("Too many wrong answers.");
        }
        if (!answered) return;

        String newPin;
        do {
            ConsoleUI.prompt("New PIN  (min 4 characters)");
            newPin = sc.nextLine().trim();
            if (newPin.length() < 4) ConsoleUI.error("PIN must be at least 4 characters.");
        } while (newPin.length() < 4);

        a.setPin(newPin);
        a.recordAudit(num, "PIN_CHANGE", "PIN reset via security question");
        system.saveAll();
        ConsoleUI.success("PIN reset successfully. You can now log in.");
        pressEnter();
    }

    /** Re-confirms the logged-in user's PIN before a sensitive action. */
    private static boolean confirmPIN() {
        ConsoleUI.prompt("Confirm with your PIN");
        String pin = sc.nextLine().trim();
        return system.getAccount(system.findAccountByNumber(loggedInAccountNum)).checkPIN(pin);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /** @return true if the current session is admin (account or system). */
    private static boolean isCurrentUserAdmin() {
        if (loggedInAdmin != null) return true;
        if (loggedInAccountNum != null) {
            Account a = system.getAccount(system.findAccountByNumber(loggedInAccountNum));
            return a != null && a.getStatus().equals(Account.STATUS_ADMINISTRATOR);
        }
        return false;
    }

    /** Returns the ANSI color matching an account status. */
    private static String getStatusColor(String status) {
        switch (status) {
            case Account.STATUS_ADMINISTRATOR: return ConsoleUI.MAGENTA;
            case Account.STATUS_STAFF:         return ConsoleUI.YELLOW;
            default:                           return ConsoleUI.CYAN;
        }
    }

    // =========================================================================
    // INPUT VALIDATORS (Chapter 4 — reusable methods)
    // =========================================================================

    /**
     * Reads and validates a double within [min, max].
     * Chapter 3 — do-while. Chapter 4 — reusable validator.
     */
    public static double getValidDouble(Scanner sc, String prompt, double min, double max) {
        double value;
        do {
            ConsoleUI.prompt(prompt + " [" + String.format("%.0f", min)
                    + " - " + String.format("%.0f", max) + "]");
            while (!sc.hasNextDouble()) {
                ConsoleUI.error("Please enter a valid number.");
                ConsoleUI.prompt(prompt);
                sc.next();
            }
            value = sc.nextDouble();
            sc.nextLine();
            if (value < min || value > max)
                ConsoleUI.error("Value must be between " + String.format("%.0f", min)
                        + " and " + String.format("%.0f", max) + ".");
        } while (value < min || value > max);
        return value;
    }

    /**
     * Reads and validates an int within [min, max].
     * Chapter 3 — do-while. Chapter 4 — reusable validator.
     */
    public static int getValidInt(Scanner sc, String prompt, int min, int max) {
        int value;
        do {
            ConsoleUI.prompt(prompt + " [" + min + "-" + max + "]");
            while (!sc.hasNextInt()) {
                ConsoleUI.error("Please enter a whole number.");
                ConsoleUI.prompt(prompt);
                sc.next();
            }
            value = sc.nextInt();
            sc.nextLine();
            if (value < min || value > max)
                ConsoleUI.error("Enter a number between " + min + " and " + max + ".");
        } while (value < min || value > max);
        return value;
    }

    private static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    private static void pressEnter() {
        ConsoleUI.blank();
        ConsoleUI.printCenter(ConsoleUI.YELLOW, "Press Enter to continue...");
        sc.nextLine();
    }

    private static void printGoodbye() {
        ConsoleUI.blank();
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.star() + ConsoleUI.RESET);
        ConsoleUI.printCenter(ConsoleUI.BOLD_CYAN,  "Thank you for using CamFlow " + APP_VERSION + "!");
        ConsoleUI.printCenter(ConsoleUI.YELLOW,     "PKFokam Institute of Excellence");
        ConsoleUI.printCenter(ConsoleUI.WHITE,      "Java Lab — Mr. LELE Vaneck  |  Freshman 2, 2026");
        System.out.println(ConsoleUI.BOLD_BLUE + ConsoleUI.star() + ConsoleUI.RESET);
    }
}
