package pages.Helpers;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class providing safe null-handling and common string operations.
 */
public final class StringHelper {

    private StringHelper() {}

    /**
     * Normalizes a string by converting {@code null} to an empty string.
     */
    private static String normalizeNull(String value) {
        return value == null ? "" : value;
    }

    // ------------------------------------------------------------
    // Basic checks
    // ------------------------------------------------------------

    /**
     * Returns whether the given string is {@code null} or empty ("").
     *
     * @param value the string to check
     * @return true if null or empty
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Returns whether the given string is {@code null} or blank (empty or whitespace only).
     *
     * @param value the string to check
     * @return true if null or blank
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Converts {@code null} to an empty string; otherwise returns the value.
     *
     * @param value the string to convert
     * @return empty string if null, else the original string
     */
    public static String nullToEmpty(String value) {
        return normalizeNull(value);
    }

    /**
     * Returns the length of the string, or 0 if the string is null.
     *
     * @param value the string whose length is requested
     * @return length or 0
     */
    public static int length(String value) {
        return value == null ? 0 : value.length();
    }

    /**
     * Concatenates two strings, treating null values as empty strings.
     *
     * @param first  the first string
     * @param second the second string
     * @return concatenated result
     */
    public static String concat(String first, String second) {
        return normalizeNull(first) + normalizeNull(second);
    }

    // ------------------------------------------------------------
    // Equality / compare
    // ------------------------------------------------------------

    /**
     * Safely compares two strings for exact equality.
     *
     * @param a first string
     * @param b second string
     * @return true if equal
     */
    public static boolean equals(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * Safely compares two strings for case-insensitive equality.
     *
     * @param a first string
     * @param b second string
     * @return true if equal ignoring case
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null || b == null) return a == b;
        return a.equalsIgnoreCase(b);
    }

    /**
     * Compares two strings lexicographically, treating null as empty.
     *
     * @param a first string
     * @param b second string
     * @return comparison result
     */
    public static int compare(String a, String b) {
        return normalizeNull(a).compareTo(normalizeNull(b));
    }

    /**
     * Compares two strings lexicographically, case-insensitive, treating null as empty.
     *
     * @param a first string
     * @param b second string
     * @return comparison result
     */
    public static int compareIgnoreCase(String a, String b) {
        return normalizeNull(a).compareToIgnoreCase(normalizeNull(b));
    }

    // ------------------------------------------------------------
    // Casing / trimming
    // ------------------------------------------------------------

    /**
     * Returns a lowercase version of the string. Null becomes empty.
     *
     * @param value input string
     * @return lowercase string
     */
    public static String toLower(String value) {
        return normalizeNull(value).toLowerCase();
    }

    /**
     * Returns an uppercase version of the string. Null becomes empty.
     *
     * @param value input string
     * @return uppercase string
     */
    public static String toUpper(String value) {
        return normalizeNull(value).toUpperCase();
    }

    /**
     * Returns a trimmed version of the string. Null becomes empty string.
     *
     * @param value input string
     * @return trimmed string
     */
    public static String trim(String value) {
        return normalizeNull(value).trim();
    }

    // ------------------------------------------------------------
    // Search
    // ------------------------------------------------------------

    /**
     * Checks whether a string contains a search substring (case-sensitive).
     *
     * @param value  the source string
     * @param search the substring to search for
     * @return true if found
     */
    public static boolean contains(String value, String search) {
        if (value == null || search == null) return false;
        return value.contains(search);
    }

    /**
     * Case-insensitive contains check.
     *
     * @param value  the source string
     * @param search the substring to search for
     * @return true if found ignoring case
     */
    public static boolean containsIgnoreCase(String value, String search) {
        if (value == null || search == null) return false;
        return value.toLowerCase().contains(search.toLowerCase());
    }

    /**
     * Case-sensitive prefix check.
     *
     * @param value  source string
     * @param prefix prefix to check
     * @return true if starts with prefix
     */
    public static boolean startsWith(String value, String prefix) {
        if (value == null || prefix == null) return false;
        return value.startsWith(prefix);
    }

    /**
     * Case-sensitive suffix check.
     *
     * @param value  source string
     * @param suffix suffix to check
     * @return true if ends with suffix
     */
    public static boolean endsWith(String value, String suffix) {
        if (value == null || suffix == null) return false;
        return value.endsWith(suffix);
    }

    /**
     * Returns the first index of a substring, or -1 if not found.
     *
     * @param value  source string
     * @param search substring to find
     * @return index or -1
     */
    public static int indexOf(String value, String search) {
        if (value == null || search == null) return -1;
        return value.indexOf(search);
    }

    /**
     * Returns the last index of a substring, or -1 if not found.
     *
     * @param value  source string
     * @param search substring to find
     * @return index or -1
     */
    public static int lastIndexOf(String value, String search) {
        if (value == null || search == null) return -1;
        return value.lastIndexOf(search);
    }

    // ------------------------------------------------------------
    // Replace
    // ------------------------------------------------------------

    /**
     * Replaces occurrences of a target string with a replacement string.
     * Null inputs are treated as empty.
     *
     * @param value       source string
     * @param target      substring to replace
     * @param replacement replacement value
     * @return modified string
     */
    public static String replace(String value, String target, String replacement) {
        if (value == null) return "";
        return value.replace(normalizeNull(target), normalizeNull(replacement));
    }

    /**
     * Replaces the first substring matching a regex.
     *
     * @param value       source string
     * @param regex       regex pattern
     * @param replacement replacement text
     * @return modified string
     */
    public static String replaceFirstRegex(String value, String regex, String replacement) {
        if (value == null) return "";
        return value.replaceFirst(regex, normalizeNull(replacement));
    }

    /**
     * Replaces all substrings matching a regex.
     *
     * @param value       source string
     * @param regex       regex pattern
     * @param replacement replacement text
     * @return modified string
     */
    public static String replaceAllRegex(String value, String regex, String replacement) {
        if (value == null) return "";
        return value.replaceAll(regex, normalizeNull(replacement));
    }

    // ------------------------------------------------------------
    // Substrings / slices
    // ------------------------------------------------------------

    /**
     * Returns a substring starting at a given index. Out-of-range indices
     * are clamped; null returns null.
     *
     * @param value      source string
     * @param beginIndex starting index (clamped)
     * @return substring or null
     */
    public static String substring(String value, int beginIndex) {
        if (value == null) return null;
        int len = value.length();
        int start = Math.max(0, Math.min(beginIndex, len));
        return value.substring(start);
    }

    /**
     * Returns a substring between two clamped indices. Null returns null.
     *
     * @param value      source string
     * @param beginIndex start index
     * @param endIndex   end index
     * @return substring or null
     */
    public static String substring(String value, int beginIndex, int endIndex) {
        if (value == null) return null;
        int len = value.length();
        int start = Math.max(0, Math.min(beginIndex, len));
        int end = Math.max(start, Math.min(endIndex, len));
        return value.substring(start, end);
    }

    /**
     * Returns the leftmost {@code length} characters. Null returns null.
     *
     * @param value  source string
     * @param length number of characters
     * @return substring or null
     */
    public static String left(String value, int length) {
        if (value == null) return null;
        int len = Math.max(0, Math.min(length, value.length()));
        return value.substring(0, len);
    }

    /**
     * Returns the rightmost {@code length} characters. Null returns null.
     *
     * @param value  source string
     * @param length number of characters
     * @return substring or null
     */
    public static String right(String value, int length) {
        if (value == null) return null;
        int len = Math.max(0, Math.min(length, value.length()));
        return value.substring(value.length() - len);
    }

    /**
     * Returns the substring before the first occurrence of a delimiter.
     * If delimiter not found, returns the original string.
     *
     * @param value     source string
     * @param delimiter delimiter to search
     * @return substring before delimiter
     */
    public static String substringBefore(String value, String delimiter) {
        if (value == null || delimiter == null) return value;
        int idx = value.indexOf(delimiter);
        return idx < 0 ? value : value.substring(0, idx);
    }

    /**
     * Returns the substring after the first occurrence of a delimiter.
     * If not found, returns empty string.
     *
     * @param value     source string
     * @param delimiter delimiter to search
     * @return substring after delimiter
     */
    public static String substringAfter(String value, String delimiter) {
        if (value == null || delimiter == null) return "";
        int idx = value.indexOf(delimiter);
        return idx < 0 ? "" : value.substring(idx + delimiter.length());
    }

    // ------------------------------------------------------------
    // Split / Join
    // ------------------------------------------------------------

    /**
     * Splits a string by a literal delimiter. Null returns an empty array.
     *
     * @param value     source string
     * @param delimiter literal delimiter string
     * @return array of parts
     */
    public static String[] split(String value, String delimiter) {
        if (value == null) return new String[0];
        if (delimiter == null || delimiter.isEmpty()) return new String[] { value };
        return value.split(Pattern.quote(delimiter), -1);
    }

    /**
     * Joins an iterable collection of strings using a delimiter.
     * Null parts are treated as empty.
     *
     * @param delimiter delimiter between parts
     * @param parts     strings to join
     * @return joined string
     */
    public static String join(String delimiter, Iterable<String> parts) {
        if (parts == null) return "";
        String d = normalizeNull(delimiter);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (!first) sb.append(d);
            sb.append(normalizeNull(part));
            first = false;
        }
        return sb.toString();
    }

    // ------------------------------------------------------------
    // Padding / repeat / reverse
    // ------------------------------------------------------------

    /**
     * Repeats a string a given number of times. Null becomes empty.
     *
     * @param value the string to repeat
     * @param times number of repetitions
     * @return repeated string
     */
    public static String repeat(String value, int times) {
        if (times <= 0) return "";
        String src = normalizeNull(value);
        return src.repeat(times);
    }

    /**
     * Pads a string on the left to reach a desired total length.
     *
     * @param value       the original string
     * @param totalLength final desired length
     * @param padUnit     the padding unit to repeat
     * @return padded string
     */
    public static String padLeft(String value, int totalLength, String padUnit) {
        String src = normalizeNull(value);
        int pad = Math.max(0, totalLength - src.length());
        if (pad == 0) return src;
        return padUnit.repeat(pad) + src;
    }

    /**
     * Pads a string on the right to reach a desired total length.
     *
     * @param value       the original string
     * @param totalLength final desired length
     * @param padUnit     the padding unit to repeat
     * @return padded string
     */
    public static String padRight(String value, int totalLength, String padUnit) {
        String src = normalizeNull(value);
        int pad = Math.max(0, totalLength - src.length());
        if (pad == 0) return src;
        return src + padUnit.repeat(pad);
    }

    /**
     * Reverses a string. Null becomes empty.
     *
     * @param value string to reverse
     * @return reversed string
     */
    public static String reverse(String value) {
        String src = normalizeNull(value);
        return new StringBuilder(src).reverse().toString();
    }

    /**
     * Removes all whitespace characters using a regex. Null becomes empty string.
     *
     * @param value input string
     * @return string without whitespace
     */
    public static String removeWhitespace(String value) {
        if (value == null) return "";
        return value.replaceAll("\\s+", "");
    }
}
