import java.io.Serializable;

/**
 * Transaction.java
 * Represents a single financial transaction in the CamFlow system.
 * Immutable after creation — all fields set in constructor, no setters.
 *
 * @author NGASSA DJONGA Christian Fredy
 * 
 * @version 1.0 — Freshman 2, 2026
 * @course Programming and Problem Solving 1 (Java) — Dr. Leonel Moyou
 * @institution PKFokam Institute of Excellence
 */
public class Transaction implements Serializable {

    // -------------------------------------------------------------------------
    // STATIC FIELD — auto-incrementing ID counter shared across all transactions
    // -------------------------------------------------------------------------

    /** OOP NOTE — static: one counter shared by ALL Transaction objects. */
    private static int txCounter = 0;

    // -------------------------------------------------------------------------
    // PRIVATE INSTANCE FIELDS
    // -------------------------------------------------------------------------

    /** Unique transaction ID, auto-assigned from txCounter. */
    private int transactionId;

    /** Transaction type: "DEPOSIT", "WITHDRAWAL", "TRANSFER_OUT", "TRANSFER_IN". */
    private String type;

    /** The principal amount of the transaction. */
    private double amount;

    /** Fee charged (0.0 for deposits and small withdrawals). */
    private double fee;

    /** Account balance immediately after this transaction completed. */
    private double balanceAfter;

    /** Human-readable description (e.g. "Transfer to CF-0003"). */
    private String description;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------

    /**
     * Creates a new Transaction and auto-assigns the next transaction ID.
     *
     * @param type         transaction type string
     * @param amount       principal amount in FCFA
     * @param fee          fee charged in FCFA
     * @param balanceAfter balance remaining after this transaction
     * @param description  short description string
     */
    public Transaction(String type, double amount, double fee,
                       double balanceAfter, String description) {
        // OOP NOTE — static counter incremented every time a Transaction is created
        txCounter++;
        this.transactionId = txCounter;
        this.type          = type;
        this.amount        = amount;
        this.fee           = fee;
        this.balanceAfter  = balanceAfter;
        this.description   = description;
    }

    // -------------------------------------------------------------------------
    // STATIC HELPER
    // -------------------------------------------------------------------------

    /**
     * Resets the counter (used when loading from file to restore correct state).
     * @param value counter value to restore
     */
    public static void setTxCounter(int value) { txCounter = value; }

    /** @return current global transaction counter value */
    public static int getTxCounter() { return txCounter; }

    // -------------------------------------------------------------------------
    // GETTERS — read-only access to private fields
    // -------------------------------------------------------------------------

    /** @return unique transaction ID */
    public int    getId()          { return transactionId; }

    /** @return transaction type string */
    public String getType()        { return type; }

    /** @return principal amount */
    public double getAmount()      { return amount; }

    /** @return fee charged */
    public double getFee()         { return fee; }

    /** @return balance after transaction */
    public double getBalanceAfter(){ return balanceAfter; }

    /** @return description string */
    public String getDescription() { return description; }

    // -------------------------------------------------------------------------
    // DISPLAY METHOD
    // -------------------------------------------------------------------------

    /**
     * Prints a formatted, centered, colored transaction receipt to the console.
     * Color scheme: GREEN for deposits/transfer-in, RED for withdrawals/transfer-out.
     */
    public void printReceipt() {
        // Choose color based on transaction type
        String color;
        switch (type) {
            case "DEPOSIT":      color = ConsoleUI.GREEN;   break;
            case "TRANSFER_IN":  color = ConsoleUI.GREEN;   break;
            case "WITHDRAWAL":   color = ConsoleUI.RED;     break;
            case "TRANSFER_OUT": color = ConsoleUI.MAGENTA; break;
            default:             color = ConsoleUI.WHITE;
        }

        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
        System.out.println(color + ConsoleUI.center("== Transaction #" + transactionId + " : " + type + " ==") + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thin() + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW + ConsoleUI.center("Amount      : " + ConsoleUI.formatAmount(amount)) + ConsoleUI.RESET);

        // Only show fee line if a fee was charged
        if (fee > 0) {
            System.out.println(ConsoleUI.RED + ConsoleUI.center("Fee         : " + ConsoleUI.formatAmount(fee)) + ConsoleUI.RESET);
            if (type.equals("WITHDRAWAL") || type.equals("TRANSFER_OUT")) {
                System.out.println(ConsoleUI.RED + ConsoleUI.center("Total Debited: " + ConsoleUI.formatAmount(amount + fee)) + ConsoleUI.RESET);
            }
        } else if (type.equals("WITHDRAWAL") || type.equals("TRANSFER_OUT")) {
            System.out.println(ConsoleUI.GREEN + ConsoleUI.center("Fee         : WAIVED") + ConsoleUI.RESET);
        }

        System.out.println(ConsoleUI.CYAN  + ConsoleUI.center("Balance After: " + ConsoleUI.formatAmount(balanceAfter)) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.YELLOW + ConsoleUI.center("Description : " + description) + ConsoleUI.RESET);
        System.out.println(ConsoleUI.GREEN  + ConsoleUI.center("Status      : SUCCESS") + ConsoleUI.RESET);
        System.out.println(ConsoleUI.BLUE + ConsoleUI.thick() + ConsoleUI.RESET);
    }

    /**
     * Returns a compact one-line summary for history table display.
     * @return formatted table row string
     */
    public String toTableRow() {
        // Chapter 2 — String.format for percentage formatting and alignment
        String typeShort = type.length() > 12 ? type.substring(0, 12) : type;
        return String.format("%-5d %-13s %15s %12s %18s",
                transactionId,
                typeShort,
                ConsoleUI.formatAmount(amount),
                ConsoleUI.formatAmount(fee),
                ConsoleUI.formatAmount(balanceAfter));
    }
}
