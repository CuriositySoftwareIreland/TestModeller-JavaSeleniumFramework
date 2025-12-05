package pages.Helpers;

/**
 * Utility class for common Boolean operations, parsing, formatting, and safe handling of boxed Booleans.
 */
public final class BooleanHelper {

    private BooleanHelper() {}

    // -------------------------------------------------------------------------
    // Logic operations
    // -------------------------------------------------------------------------

    /**
     * Returns the logical negation of the input.
     *
     * @param value the boolean value
     * @return true if input is false, false if input is true
     */
    public static boolean not(boolean value) { return !value; }

    /**
     * Returns the logical AND of two boolean values.
     *
     * @param a first boolean
     * @param b second boolean
     * @return true if both a and b are true; otherwise false
     */
    public static boolean and(boolean a, boolean b) { return a && b; }

    /**
     * Returns the logical OR of two boolean values.
     *
     * @param a first boolean
     * @param b second boolean
     * @return true if either a or b is true; otherwise false
     */
    public static boolean or(boolean a, boolean b) { return a || b; }

    /**
     * Returns the logical XOR (exclusive OR) of two boolean values.
     *
     * @param a first boolean
     * @param b second boolean
     * @return true if exactly one of a or b is true; otherwise false
     */
    public static boolean xor(boolean a, boolean b) { return a ^ b; }

    /**
     * Returns the logical NAND (NOT AND) of two boolean values.
     *
     * @param a first boolean
     * @param b second boolean
     * @return true if NOT (a AND b); otherwise false
     */
    public static boolean nand(boolean a, boolean b) { return !(a && b); }

    /**
     * Returns the logical NOR (NOT OR) of two boolean values.
     *
     * @param a first boolean
     * @param b second boolean
     * @return true if NOT (a OR b); otherwise false
     */
    public static boolean nor(boolean a, boolean b) { return !(a || b); }

    // -------------------------------------------------------------------------
    // Parsing / formatting
    // -------------------------------------------------------------------------

    /**
     * Parses a string into a boolean. Recognized true values: "true", "t", "yes", "y", "1", "on", "enabled".
     * Recognized false values: "false", "f", "no", "n", "0", "off", "disabled".
     * If unrecognized, returns false.
     *
     * @param s the string to parse; may be null
     * @return the parsed boolean
     */
    public static boolean parse(String s) {
        return parse(s, false);
    }

    /**
     * Parses a string into a boolean with a default value if the string is unrecognized or null.
     *
     * @param s the string to parse; may be null
     * @param defaultValue the value to return if s is null or unrecognized
     * @return the parsed boolean or the default value
     */
    public static boolean parse(String s, boolean defaultValue) {
        if (s == null) return defaultValue;
        String v = s.trim().toLowerCase();
        switch (v) {
            case "true":
            case "t":
            case "yes":
            case "y":
            case "1":
            case "on":
            case "enabled":
                return true;
            case "false":
            case "f":
            case "no":
            case "n":
            case "0":
            case "off":
            case "disabled":
                return false;
            default:
                return defaultValue;
        }
    }

    /**
     * Converts a boolean value to an integer (1 for true, 0 for false).
     *
     * @param b the boolean value
     * @return 1 if b is true, 0 if b is false
     */
    public static int toInt(boolean b) { return b ? 1 : 0; }

    /**
     * Converts a boolean value to a string with custom true/false representations.
     *
     * @param b the boolean value
     * @param trueString the string to use if b is true; defaults to "true" if null
     * @param falseString the string to use if b is false; defaults to "false" if null
     * @return the corresponding string representation
     */
    public static String toString(boolean b, String trueString, String falseString) {
        return b ? (trueString == null ? "true" : trueString) : (falseString == null ? "false" : falseString);
    }

    // -------------------------------------------------------------------------
    // Boxed Boolean helpers
    // -------------------------------------------------------------------------

    /**
     * Safely compares two boxed Boolean values for equality, handling nulls.
     *
     * @param a first Boolean; may be null
     * @param b second Boolean; may be null
     * @return true if both are equal or both null, false otherwise
     */
    public static boolean equals(Boolean a, Boolean b) {
        if (a == null || b == null) return a == b;
        return a.equals(b);
    }

    /**
     * Returns the Boolean value if not null, otherwise returns the provided default value.
     *
     * @param value the Boolean value; may be null
     * @param defaultValue the default primitive boolean to return if value is null
     * @return value if not null, otherwise defaultValue
     */
    public static boolean coalesce(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }
}
