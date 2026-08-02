package com.saucedemo.utils;

import com.saucedemo.constants.FrameworkConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtility provides reusable date/time formatting and arithmetic helpers, replacing
 * ad hoc {@link java.text.SimpleDateFormat} usage scattered across the framework
 * (e.g. previously duplicated inline in {@link ScreenshotUtility}).
 */
public class DateUtility {

    private DateUtility() {
        // Prevent instantiation
    }

    /** Returns the current date/time formatted for screenshot file names. */
    public static String getScreenshotTimestamp() {
        return format(LocalDateTime.now(), FrameworkConstants.SCREENSHOT_TIMESTAMP_FORMAT);
    }

    /** Returns the current date/time formatted for human-readable report entries. */
    public static String getReportTimestamp() {
        return format(LocalDateTime.now(), FrameworkConstants.REPORT_TIMESTAMP_FORMAT);
    }

    /** Returns the current date/time in ISO-8601 format. */
    public static String getIsoTimestamp() {
        return format(LocalDateTime.now(), FrameworkConstants.ISO_DATETIME_FORMAT);
    }

    /** Formats a {@link LocalDateTime} using the given pattern. */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /** Formats a {@link LocalDate} using the given pattern. */
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /** Returns today's date plus the given number of days, formatted with the given pattern. */
    public static String getFutureDate(int daysFromToday, String pattern) {
        return format(LocalDate.now().plusDays(daysFromToday), pattern);
    }

    /** Returns today's date minus the given number of days, formatted with the given pattern. */
    public static String getPastDate(int daysBeforeToday, String pattern) {
        return format(LocalDate.now().minusDays(daysBeforeToday), pattern);
    }
}
