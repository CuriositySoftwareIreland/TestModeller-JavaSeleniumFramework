package pages.Helpers;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class providing convenience helpers for working with dates and times.
 * Includes parsing, formatting, arithmetic, comparisons, and zone conversions.
 */
public final class DateHelper {

    private DateHelper() {}

    /**
     * Returns the provided zone or the system default if null.
     */
    private static ZoneId nz(ZoneId zone) {
        return zone == null ? ZoneId.systemDefault() : zone;
    }

    // -------------------------------------------------------------------------
    // Now / Today
    // -------------------------------------------------------------------------

    /**
     * Returns today's date using the system default time zone.
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Returns today's date using the specified zone.
     *
     * @param zone the time zone to use; if null, the system default is used
     */
    public static LocalDate today(ZoneId zone) {
        return LocalDate.now(nz(zone));
    }

    /**
     * Returns the current local date-time using the system default time zone.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Returns the current local date-time using the specified zone.
     *
     * @param zone the time zone to use; if null, the system default is used
     */
    public static LocalDateTime now(ZoneId zone) {
        return LocalDateTime.now(nz(zone));
    }

    /**
     * Returns the current date-time in the specified time zone.
     *
     * @param zone the time zone to use; if null, the system default is used
     */
    public static ZonedDateTime nowInZone(ZoneId zone) {
        return ZonedDateTime.now(nz(zone));
    }

    // -------------------------------------------------------------------------
    // Parse
    // -------------------------------------------------------------------------

    /**
     * Parses a date string using the given pattern.
     *
     * @param text the text to parse; may be null
     * @param pattern the date format pattern
     * @return the parsed LocalDate, or null if text is null
     */
    public static LocalDate parseLocalDate(String text, String pattern) {
        if (text == null) return null;
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(text, f);
    }

    /**
     * Parses a date-time string using the given pattern.
     *
     * @param text the text to parse; may be null
     * @param pattern the date-time format pattern
     * @return the parsed LocalDateTime, or null if text is null
     */
    public static LocalDateTime parseLocalDateTime(String text, String pattern) {
        if (text == null) return null;
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(text, f);
    }

    /**
     * Parses a zoned date-time using the given pattern. If ZonedDateTime parsing
     * fails, falls back to parsing as LocalDateTime and applying the provided zone.
     *
     * @param text the text to parse; may be null
     * @param pattern the date-time pattern
     * @param zoneHint the fallback zone if the pattern does not include zone info
     * @return the parsed ZonedDateTime, or null if text is null
     */
    public static ZonedDateTime parseZonedDateTime(String text, String pattern, ZoneId zoneHint) {
        if (text == null) return null;
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern).withZone(nz(zoneHint));
        try {
            return ZonedDateTime.parse(text, f);
        } catch (Exception ignored) {
            LocalDateTime ldt = LocalDateTime.parse(text, f);
            return ldt.atZone(nz(zoneHint));
        }
    }

    // -------------------------------------------------------------------------
    // Format
    // -------------------------------------------------------------------------

    /**
     * Formats a LocalDate using the specified pattern.
     *
     * @param date the date to format; may be null
     * @param pattern the pattern to apply
     * @return the formatted string, or empty string if date is null
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null) return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return date.format(f);
    }

    /**
     * Formats a LocalDateTime using the specified pattern.
     *
     * @param dateTime the date-time to format; may be null
     * @param pattern the pattern to apply
     * @return the formatted string, or empty string if dateTime is null
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(f);
    }

    /**
     * Formats a ZonedDateTime using the specified pattern.
     *
     * @param dateTime the zoned date-time to format; may be null
     * @param pattern the format pattern
     * @return the formatted string, or empty string if dateTime is null
     */
    public static String format(ZonedDateTime dateTime, String pattern) {
        if (dateTime == null) return "";
        DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(f);
    }

    // -------------------------------------------------------------------------
    // Arithmetic
    // -------------------------------------------------------------------------

    /**
     * Adds days to a date.
     *
     * @param d the date; may be null
     * @param days number of days to add
     * @return the resulting date, or null if input is null
     */
    public static LocalDate plusDays(LocalDate d, long days) { return d == null ? null : d.plusDays(days); }

    public static LocalDate plusMonths(LocalDate d, long months) { return d == null ? null : d.plusMonths(months); }

    public static LocalDate plusYears(LocalDate d, long years) { return d == null ? null : d.plusYears(years); }

    public static LocalDateTime plusHours(LocalDateTime dt, long hours) { return dt == null ? null : dt.plusHours(hours); }

    public static LocalDateTime plusMinutes(LocalDateTime dt, long minutes) { return dt == null ? null : dt.plusMinutes(minutes); }

    public static LocalDateTime plusSeconds(LocalDateTime dt, long seconds) { return dt == null ? null : dt.plusSeconds(seconds); }

    // -------------------------------------------------------------------------
    // Start/end of day
    // -------------------------------------------------------------------------

    /**
     * Returns the start of the given date at midnight (00:00).
     */
    public static LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    /**
     * Returns the end of the given date at 23:59:59.999999999.
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        if (date == null) return null;
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Returns the start of the given date in the specified zone.
     */
    public static ZonedDateTime startOfDay(LocalDate date, ZoneId zone) {
        return date == null ? null : date.atStartOfDay(nz(zone));
    }

    /**
     * Returns the end of the given date in the specified zone.
     */
    public static ZonedDateTime endOfDay(LocalDate date, ZoneId zone) {
        if (date == null) return null;
        return date.atTime(LocalTime.MAX).atZone(nz(zone));
    }

    // -------------------------------------------------------------------------
    // Between
    // -------------------------------------------------------------------------

    /**
     * Returns the number of days between two dates.
     */
    public static long betweenDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Returns the number of hours between two date-times.
     */
    public static long betweenHours(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.HOURS.between(start, end);
    }

    /**
     * Returns the number of minutes between two date-times.
     */
    public static long betweenMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.MINUTES.between(start, end);
    }

    /**
     * Returns the number of milliseconds between two instants.
     */
    public static long betweenMillis(Instant start, Instant end) {
        if (start == null || end == null) return 0L;
        return ChronoUnit.MILLIS.between(start, end);
    }

    // -------------------------------------------------------------------------
    // Truncation
    // -------------------------------------------------------------------------

    /**
     * Truncates a date-time to the nearest hour.
     */
    public static LocalDateTime truncateToHour(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.truncatedTo(ChronoUnit.HOURS);
    }

    /**
     * Truncates a date-time to the nearest minute.
     */
    public static LocalDateTime truncateToMinute(LocalDateTime dt) {
        if (dt == null) return null;
        return dt.truncatedTo(ChronoUnit.MINUTES);
    }

    // -------------------------------------------------------------------------
    // Zone conversions
    // -------------------------------------------------------------------------

    /**
     * Applies a time zone to a LocalDateTime.
     */
    public static ZonedDateTime atZone(LocalDateTime dt, ZoneId zone) {
        if (dt == null) return null;
        return dt.atZone(nz(zone));
    }

    /**
     * Converts a ZonedDateTime to another time zone while preserving the instant.
     */
    public static ZonedDateTime convertZone(ZonedDateTime zdt, ZoneId newZone) {
        if (zdt == null) return null;
        return zdt.withZoneSameInstant(nz(newZone));
    }

    // -------------------------------------------------------------------------
    // Epoch conversions
    // -------------------------------------------------------------------------

    /**
     * Converts a ZonedDateTime to epoch milliseconds.
     */
    public static long toEpochMillis(ZonedDateTime zdt) {
        if (zdt == null) return 0L;
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * Creates a ZonedDateTime from epoch milliseconds.
     */
    public static ZonedDateTime fromEpochMillis(long epochMillis, ZoneId zone) {
        return Instant.ofEpochMilli(epochMillis).atZone(nz(zone));
    }

    // -------------------------------------------------------------------------
    // Month helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the first day of the month for the given date.
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.withDayOfMonth(1);
    }

    /**
     * Returns the last day of the month for the given date.
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        if (date == null) return null;
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    // -------------------------------------------------------------------------
    // Comparisons
    // -------------------------------------------------------------------------

    /**
     * Returns true if date a is strictly before date b.
     */
    public static boolean isBefore(LocalDate a, LocalDate b) {
        if (a == null || b == null) return false;
        return a.isBefore(b);
    }

    /**
     * Returns true if date a is strictly after date b.
     */
    public static boolean isAfter(LocalDate a, LocalDate b) {
        if (a == null || b == null) return false;
        return a.isAfter(b);
    }

    /**
     * Returns true if date-time a is strictly before b.
     */
    public static boolean isBefore(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) return false;
        return a.isBefore(b);
    }

    /**
     * Returns true if date-time a is strictly after b.
     */
    public static boolean isAfter(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) return false;
        return a.isAfter(b);
    }
}
