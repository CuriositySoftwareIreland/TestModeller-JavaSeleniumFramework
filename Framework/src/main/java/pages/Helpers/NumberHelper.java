package pages.Helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * Utility class providing safe parsing, comparison, formatting,
 * and mathematical operations for primitive numbers and {@link BigDecimal}.
 */
public final class NumberHelper {

    private NumberHelper() {}

    // ------------------------------------------------------------
    // Parsing with defaults
    // ------------------------------------------------------------

    /**
     * Parses a string into an integer. If parsing fails, returns the default value.
     *
     * @param text         the string to parse
     * @param defaultValue the fallback value if parsing fails
     * @return parsed integer or default
     */
    public static int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text == null ? "" : text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Parses a string into a long. If parsing fails, returns the default value.
     *
     * @param text         the string to parse
     * @param defaultValue the fallback value
     * @return parsed long or default
     */
    public static long parseLong(String text, long defaultValue) {
        try {
            return Long.parseLong(text == null ? "" : text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Parses a string into a double. If parsing fails, returns the default value.
     *
     * @param text         the string to parse
     * @param defaultValue the fallback value
     * @return parsed double or default
     */
    public static double parseDouble(String text, double defaultValue) {
        try {
            return Double.parseDouble(text == null ? "" : text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Parses a string into a {@link BigDecimal}. If parsing fails, returns the default value.
     *
     * @param text         the string to parse
     * @param defaultValue the fallback value
     * @return parsed BigDecimal or default
     */
    public static BigDecimal parseBigDecimal(String text, BigDecimal defaultValue) {
        try {
            return new BigDecimal(text == null ? "" : text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ------------------------------------------------------------
    // Formatting
    // ------------------------------------------------------------

    /**
     * Formats a double using a {@link DecimalFormat} pattern.
     *
     * @param value   the number to format
     * @param pattern the formatting pattern (null uses "#.##########")
     * @return formatted string
     */
    public static String format(double value, String pattern) {
        DecimalFormat df = new DecimalFormat(pattern == null ? "#.##########" : pattern);
        return df.format(value);
    }

    /**
     * Formats a {@link BigDecimal} using a pattern.
     *
     * @param value   the number to format; null returns empty string
     * @param pattern the formatting pattern (null uses "#.##########")
     * @return formatted string
     */
    public static String format(BigDecimal value, String pattern) {
        if (value == null) return "";
        DecimalFormat df = new DecimalFormat(pattern == null ? "#.##########" : pattern);
        return df.format(value);
    }

    // ------------------------------------------------------------
    // Math (double)
    // ------------------------------------------------------------

    /** Adds two doubles. */
    public static double add(double left, double right) { return left + right; }

    /** Subtracts the second double from the first. */
    public static double subtract(double left, double right) { return left - right; }

    /** Multiplies two doubles. */
    public static double multiply(double left, double right) { return left * right; }

    /**
     * Divides two doubles.
     *
     * @param numerator   the dividend
     * @param denominator the divisor
     * @return quotient
     * @throws ArithmeticException if divisor is zero
     */
    public static double divide(double numerator, double denominator) {
        if (denominator == 0d) throw new ArithmeticException("Division by zero");
        return numerator / denominator;
    }

    /** Returns the absolute value of a double. */
    public static double abs(double value) { return Math.abs(value); }

    /** Returns the minimum of two doubles. */
    public static double min(double a, double b) { return Math.min(a, b); }

    /** Returns the maximum of two doubles. */
    public static double max(double a, double b) { return Math.max(a, b); }

    /**
     * Clamps a double to the given range.
     *
     * @param value the value to clamp
     * @param min   minimum bound
     * @param max   maximum bound
     * @return clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Rounds a double to a number of decimal places using HALF_UP.
     *
     * @param value    the value to round
     * @param decimals number of decimal places
     * @return rounded value
     */
    public static double round(double value, int decimals) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        return bd.setScale(Math.max(0, decimals), RoundingMode.HALF_UP).doubleValue();
    }

    /** Rounds upward to the given decimal places. */
    public static double ceil(double value, int decimals) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        return bd.setScale(Math.max(0, decimals), RoundingMode.CEILING).doubleValue();
    }

    /** Rounds downward to the given decimal places. */
    public static double floor(double value, int decimals) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        return bd.setScale(Math.max(0, decimals), RoundingMode.FLOOR).doubleValue();
    }

    // ------------------------------------------------------------
    // Math (BigDecimal) - precise
    // ------------------------------------------------------------

    /** Adds two BigDecimals, treating null as zero. */
    public static BigDecimal add(BigDecimal left, BigDecimal right) {
        return normalizeNull(left).add(normalizeNull(right));
    }

    /** Subtracts BigDecimal values, treating null as zero. */
    public static BigDecimal subtract(BigDecimal left, BigDecimal right) {
        return normalizeNull(left).subtract(normalizeNull(right));
    }

    /** Multiplies BigDecimals, treating null as zero. */
    public static BigDecimal multiply(BigDecimal left, BigDecimal right) {
        return normalizeNull(left).multiply(normalizeNull(right));
    }

    /**
     * Divides two BigDecimals with provided scale and rounding mode.
     *
     * @param numerator   the value to divide
     * @param denominator the divisor
     * @param scale       decimal scale (minimum 0)
     * @param mode        rounding mode; null defaults to HALF_UP
     * @return quotient
     * @throws ArithmeticException if denominator is null or zero
     */
    public static BigDecimal divide(BigDecimal numerator, BigDecimal denominator, int scale, RoundingMode mode) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0)
            throw new ArithmeticException("Division by zero");

        return normalizeNull(numerator)
                .divide(denominator, Math.max(0, scale), mode == null ? RoundingMode.HALF_UP : mode);
    }

    /**
     * Rounds a BigDecimal to a given scale with specified rounding mode.
     *
     * @param value the value to round
     * @param scale decimal places (clamped to >= 0)
     * @param mode  rounding mode (null uses HALF_UP)
     * @return rounded value
     */
    public static BigDecimal round(BigDecimal value, int scale, RoundingMode mode) {
        return normalizeNull(value)
                .setScale(Math.max(0, scale), mode == null ? RoundingMode.HALF_UP : mode);
    }

    /**
     * Compares two BigDecimals, treating null as zero.
     *
     * @param left  first value
     * @param right second value
     * @return comparison result
     */
    public static int compare(BigDecimal left, BigDecimal right) {
        return normalizeNull(left).compareTo(normalizeNull(right));
    }

    /**
     * Replaces null BigDecimal with zero.
     */
    private static BigDecimal normalizeNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    // ------------------------------------------------------------
    // Integer / long helpers
    // ------------------------------------------------------------

    /** Returns true if the integer is even. */
    public static boolean isEven(int value) { return (value & 1) == 0; }

    /** Returns true if the integer is odd. */
    public static boolean isOdd(int value) { return (value & 1) != 0; }

    /** Returns true if the long is even. */
    public static boolean isEven(long value) { return (value & 1L) == 0L; }

    /** Returns true if the long is odd. */
    public static boolean isOdd(long value) { return (value & 1L) != 0L; }

    /** Returns the minimum of two ints. */
    public static int min(int a, int b) { return Math.min(a, b); }

    /** Returns the maximum of two ints. */
    public static int max(int a, int b) { return Math.max(a, b); }

    /** Returns the minimum of two longs. */
    public static long min(long a, long b) { return Math.min(a, b); }

    /** Returns the maximum of two longs. */
    public static long max(long a, long b) { return Math.max(a, b); }

    /**
     * Clamps an int to a given range.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamps a long to a given range.
     */
    public static long clamp(long value, long min, long max) {
        return Math.max(min, Math.min(max, value));
    }

    // ------------------------------------------------------------
    // Null-safe compare for boxed types
    // ------------------------------------------------------------

    /**
     * Null-safe compare for {@link Integer}.
     *
     * @return negative if a<b, zero if equal, positive if a>b
     */
    public static int compare(Integer a, Integer b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return Integer.compare(a, b);
    }

    /**
     * Null-safe compare for {@link Long}.
     */
    public static int compare(Long a, Long b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return Long.compare(a, b);
    }

    /**
     * Null-safe compare for {@link Double}.
     */
    public static int compare(Double a, Double b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return Double.compare(a, b);
    }

    /**
     * Compares two doubles with a tolerance.
     *
     * @param a         first value
     * @param b         second value
     * @param tolerance allowed difference
     * @return true if |a - b| <= tolerance
     */
    public static boolean equals(Double a, Double b, double tolerance) {
        if (a == null || b == null) return Objects.equals(a, b);
        return Math.abs(a - b) <= Math.abs(tolerance);
    }
}
