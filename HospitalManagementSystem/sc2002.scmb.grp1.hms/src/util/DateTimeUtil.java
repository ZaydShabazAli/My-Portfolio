package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for handling date and time operations.
 * Provides methods to parse dates and times in specific formats
 * and to validate the input date and time strings.
 */
public class DateTimeUtil {
        /**
     * Parses a date string in the format "dd-MM-yyyy" and returns a LocalDate object.
     * If the input date string is not in the valid format or is invalid, returns null.
     *
     * @param date The date string to be parsed.
     * @return A LocalDate object if the date is valid; otherwise, null.
     */
    // Method to parse date in DD-MM-YYYY format and check if it's valid
    public static LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            //System.out.println("Invalid date format. Please use DD-MM-YYYY.");
            return null;
        }
    }

    /**
     * Parses a time string in the format "HH:mm" and returns a LocalTime object.
     * If the input time string is not in the valid format or is invalid, returns null.
     *
     * @param time The time string to be parsed.
     * @return A LocalTime object if the time is valid; otherwise, null.
     */
    // Method to parse time in HH:mm format and check if it's valid
    public static LocalTime parseTime(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(time, formatter);
        } catch (DateTimeParseException e) {
            //System.out.println("Invalid time format. Please use HH:mm.");
            return null;
        }
    }

    /**
     * Checks if the given date string is valid by attempting to parse it.
     *
     * @param date The date string to validate.
     * @return true if the date is valid, false otherwise.
     */
    // Check if the date is valid (Optional)
    public static boolean isValidDate(String date) {
        return parseDate(date) != null;
    }

    /**
     * Checks if the given time string is valid by attempting to parse it.
     *
     * @param time The time string to validate.
     * @return true if the time is valid, false otherwise.
     */
    // Check if the time is valid (Optional)
    public static boolean isValidTime(String time) {
        return parseTime(time) != null;
    }
}
