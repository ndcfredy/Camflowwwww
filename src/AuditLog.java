import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * AuditLog.java
 * Represents a single audit action entry in CamFlow.
 * Tracks WHO performed an action, WHAT it was, and WHEN it happened.
 *
 * Institution : PKFokam Institute of Excellence
 * Course      : Java Lab — Mr. LELE Vaneck
 * Developer   : NGASSA DJONGA Christian Fredy
 * Version     : 2.0 — Freshman 2, 2026
 *
 * @author NGASSA DJONGA Christian Fredy
 * @version 2.0
 */
public class AuditLog implements Serializable {

    // -------------------------------------------------------------------------
    // CONSTANTS
    // -------------------------------------------------------------------------
    /** Max audit entries for Student and Staff accounts. */
    public static final int MAX_AUDIT_USER  = 5;

    /** Max audit entries for Administrator accounts. */
    public static final int MAX_AUDIT_ADMIN = 15;

    // -------------------------------------------------------------------------
    // PRIVATE FIELDS
    // -------------------------------------------------------------------------

    /** Account number or admin username who performed the action. */
    private String performedBy;

    /** Short description of the action (e.g. "LOGIN", "PIN_CHANGE"). */
    private String action;

    /** Optional extra detail (e.g. target account number). */
    private String detail;

    /** Timestamp when the action occurred. */
    private String timestamp;

    // -------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------

    /**
     * Creates a new AuditLog entry with current timestamp.
     *
     * @param performedBy account number or admin username
     * @param action      short action label
     * @param detail      optional extra info
     */
    public AuditLog(String performedBy, String action, String detail) {
        this.performedBy = performedBy;
        this.action      = action;
        this.detail      = detail;
        // Capture current date and time
        this.timestamp   = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------
    public String getPerformedBy() { return performedBy; }
    public String getAction()      { return action; }
    public String getDetail()      { return detail; }
    public String getTimestamp()   { return timestamp; }

    /**
     * Prints a single formatted audit log line.
     */
    public void print() {
        String color;
        switch (action) {
            case "LOGIN":          color = ConsoleUI.GREEN;   break;
            case "LOGOUT":         color = ConsoleUI.CYAN;    break;
            case "LOGIN_FAILED":   color = ConsoleUI.RED;     break;
            case "PIN_CHANGE":     color = ConsoleUI.YELLOW;  break;
            case "ACCOUNT_OPENED": color = ConsoleUI.GREEN;   break;
            case "ACCOUNT_CLOSED": color = ConsoleUI.RED;     break;
            case "INTEREST_APPLIED": color = ConsoleUI.MAGENTA; break;
            default:               color = ConsoleUI.WHITE;
        }
        System.out.println(color
                + String.format("  %-19s %-20s %s", timestamp, action,
                        (detail != null && !detail.isEmpty() ? "| " + detail : ""))
                + ConsoleUI.RESET);
    }
}
