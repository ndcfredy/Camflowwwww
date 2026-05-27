/**
 * ConsoleUI.java
 * Utility class providing ANSI color codes, centered text helpers,
 * divider strings, and number formatters for the CamFlow console UI.
 *
 * All output is centered within a fixed 60-character wide console.
 * Uses only ASCII-safe divider characters (=, -, *) for Windows CMD compatibility.
 *
 * @author NGASSA DJONGA Christian Fredy
 * 
 * @version 1.0 — Freshman 2, 2026
 * @course Programming and Problem Solving 1 (Java) — Dr. Leonel Moyou
 * @institution PKFokam Institute of Excellence
 */
public class ConsoleUI {

    // -------------------------------------------------------------------------
    // CONSTANTS — Console width and ANSI color codes
    // -------------------------------------------------------------------------

    /** Fixed console width — all text is centered within this. */
    public static final int WIDTH = 60;

    // ANSI escape codes for terminal colors
    public static final String RESET   = "\u001B[0m";
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    // Bold variants
    public static final String BOLD_GREEN  = "\u001B[1;32m";
    public static final String BOLD_RED    = "\u001B[1;31m";
    public static final String BOLD_BLUE   = "\u001B[1;34m";
    public static final String BOLD_CYAN   = "\u001B[1;36m";
    public static final String BOLD_YELLOW = "\u001B[1;33m";
    public static final String BOLD_WHITE   = "\u001B[1;37m";
    public static final String BOLD_MAGENTA = "\u001B[1;35m";

    // -------------------------------------------------------------------------
    // DIVIDER HELPERS
    // -------------------------------------------------------------------------

    /**
     * Returns a thick divider line of '=' characters, WIDTH wide.
     * @return divider string
     */
    public static String thick() {
        return repeat('=', WIDTH);
    }

    /**
     * Returns a thin divider line of '-' characters, WIDTH wide.
     * @return divider string
     */
    public static String thin() {
        return repeat('-', WIDTH);
    }

    /**
     * Returns a star divider line of '*' characters, WIDTH wide.
     * @return divider string
     */
    public static String star() {
        return repeat('*', WIDTH);
    }

    /**
     * Repeats a character n times and returns the resulting string.
     * @param ch character to repeat
     * @param n  number of times to repeat
     * @return string of n copies of ch
     */
    public static String repeat(char ch, int n) {
        StringBuilder sb = new StringBuilder();
        // Chapter 3 — for loop
        for (int i = 0; i < n; i++) sb.append(ch);
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // CENTERING HELPER
    // -------------------------------------------------------------------------

    /**
     * Centers a string within WIDTH characters by adding leading spaces.
     * If the string is longer than WIDTH, it is returned as-is.
     *
     * @param text the text to center
     * @return centered string with leading spaces
     */
    public static String center(String text) {
        // Strip ANSI codes to get the visible length for centering calculation
        String plain = text.replaceAll("\u001B\\[[;\\d]*m", "");
        int len = plain.length();
        if (len >= WIDTH) return text;
        int padding = (WIDTH - len) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) sb.append(' ');
        sb.append(text);
        return sb.toString();
    }

    /**
     * Centers and prints a line, followed by a newline.
     * @param text text to center and print
     */
    public static void printCenter(String text) {
        System.out.println(center(text));
    }

    /**
     * Centers and prints a colored line.
     * @param color ANSI color code
     * @param text  text to center and print
     */
    public static void printCenter(String color, String text) {
        System.out.println(color + center(text) + RESET);
    }

    // -------------------------------------------------------------------------
    // NUMBER FORMATTING
    // -------------------------------------------------------------------------

    /**
     * Formats a double amount as "25,000.00 FCFA".
     * Chapter 2 — uses String.format for percentage/amount formatting.
     *
     * @param amount the amount to format
     * @return formatted string
     */
    public static String formatAmount(double amount) {
        return String.format("%,.2f FCFA", amount);
    }

    /**
     * Formats a double as a percentage string, e.g. "1.00%".
     * @param rate the rate to format
     * @return formatted percentage string
     */
    public static String formatRate(double rate) {
        return String.format("%.2f%%", rate);
    }

    // -------------------------------------------------------------------------
    // BANNER HELPERS
    // -------------------------------------------------------------------------

    /**
     * Prints a section header with thick dividers and a centered title.
     * @param title  the title text
     * @param color  ANSI color for the title
     */
    public static void printHeader(String title, String color) {
        System.out.println(BLUE + thick() + RESET);
        System.out.println(color + center(title) + RESET);
        System.out.println(BLUE + thick() + RESET);
    }

    /**
     * Prints a success message in green, centered.
     * @param msg message text
     */
    public static void success(String msg) {
        System.out.println(BOLD_GREEN + center("[SUCCESS] " + msg) + RESET);
    }

    /**
     * Prints an error message in red, centered.
     * @param msg message text
     */
    public static void error(String msg) {
        System.out.println(BOLD_RED + center("[ERROR] " + msg) + RESET);
    }

    /**
     * Prints a warning message in yellow, centered.
     * @param msg message text
     */
    public static void warning(String msg) {
        System.out.println(BOLD_YELLOW + center("[WARNING] " + msg) + RESET);
    }

    /**
     * Prints an info message in cyan, centered.
     * @param msg message text
     */
    public static void info(String msg) {
        System.out.println(CYAN + center(msg) + RESET);
    }

    /**
     * Prints a blank line.
     */
    public static void blank() {
        System.out.println();
    }

    /**
     * Prompts the user and returns input, colored with yellow label.
     * @param prompt the prompt text (label only, Scanner read is done by caller)
     */
    public static void prompt(String prompt) {
        System.out.print(YELLOW + center(prompt + ": ") + RESET);
    }
}
