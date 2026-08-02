package com.saucedemo.utils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * RandomDataGenerator produces randomized test data (names, emails, postal codes, numbers)
 * so tests avoid brittle hardcoded literals (e.g. always submitting "John Doe / 90210").
 *
 * <p>Thread-safe: uses {@link ThreadLocalRandom}, matching the framework's ThreadLocal
 * parallel-execution model ({@link com.saucedemo.driver.DriverFactory}).
 */
public class RandomDataGenerator {

    private static final String[] FIRST_NAMES = {
            "John", "Jane", "Alice", "Bob", "Charlie", "Diana", "Ethan", "Fiona", "George", "Hannah"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Wilson", "Taylor"
    };

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private RandomDataGenerator() {
        // Prevent instantiation
    }

    /** Returns a random first name from a curated pool. */
    public static String randomFirstName() {
        return FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
    }

    /** Returns a random last name from a curated pool. */
    public static String randomLastName() {
        return LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
    }

    /** Returns a random 5-digit US-style postal code as a string. */
    public static String randomPostalCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 100000));
    }

    /** Returns a random alphanumeric string of the given length. */
    public static String randomAlphanumeric(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERIC.charAt(ThreadLocalRandom.current().nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    /** Returns a random, guaranteed-unique email address for test-data isolation. */
    public static String randomEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    /** Returns a random integer between min (inclusive) and max (exclusive). */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    /** Returns a random 10-digit US-style phone number as a string. */
    public static String randomPhoneNumber() {
        return String.format("%03d-%03d-%04d",
                ThreadLocalRandom.current().nextInt(200, 999),
                ThreadLocalRandom.current().nextInt(200, 999),
                ThreadLocalRandom.current().nextInt(0, 9999));
    }
}
