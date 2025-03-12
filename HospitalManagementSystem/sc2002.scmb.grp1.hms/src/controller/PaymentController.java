package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The {@code PaymentController} class handles payment-related operations for medical appointments.
 * It manages payment processing, calculates outstanding balances, and updates payment records in CSV files.
 */
public class PaymentController {
    private static final String FILE_PATH_APPOINTMENT = "sc2002.scmb.grp1.hms/resource/Appointment.csv";
    private static final String FILE_PATH_PAYMENT = "sc2002.scmb.grp1.hms/resource/Payment.csv";

    /**
     * Processes a payment for a specified hospital ID. The method prompts the user to enter credit card details,
     * verifies them, and updates the payment records accordingly.
     *
     * @param hospitalID the hospital ID of the patient making the payment
     * @return {@code true} if the payment was processed successfully, {@code false} otherwise
     */
    public boolean pay(String hospitalID) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter your credit card number:");
        String creditCardNumber = scanner.nextLine().trim();

        if (creditCardNumber.length() < 12 || creditCardNumber.length() > 16) {
            System.out.println("Invalid credit card number. Payment failed.");
            return false;
        }

        System.out.println("Please enter the name on the credit card:");
        String creditCardName = scanner.nextLine().trim();

        if (creditCardName.isEmpty()) {
            System.out.println("Invalid credit card name. Payment failed.");
            return false;
        }

        List<String[]> allRecords = new ArrayList<>();
        boolean paymentProcessed = false;

        // Step 1: Read and update the payment record
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PAYMENT))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID)) {
                    int numberOfUnpaid = Integer.parseInt(parts[1]);
                    int numberOfPaid = Integer.parseInt(parts[2]);

                    if (numberOfUnpaid > 0) {
                        numberOfPaid += numberOfUnpaid;
                        parts[1] = "0";
                        parts[2] = String.valueOf(numberOfPaid);
                        paymentProcessed = true;
                    } else {
                        System.out.println("No unpaid medical records to process.");
                        return false;
                    }
                }
                allRecords.add(parts);
            }
        } catch (IOException e) {
            System.err.println("Error reading Payment.csv: " + e.getMessage());
            return false;
        }

        // Step 2: Write the updated payment records to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PAYMENT))) {
            writer.write("PatientID,numberOfUnpaid,numberOfPaid");
            writer.newLine();
            for (String[] record : allRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Payment.csv: " + e.getMessage());
            return false;
        }

        if (paymentProcessed) {
            System.out.println("Payment processed successfully for hospital ID: " + hospitalID);
            System.out.println("Thank you for your payment, " + creditCardName + "!");
            return true;
        }

        System.out.println("Payment could not be processed.");
        return false;
    }

    /**
     * Calculates the total amount due for a specific hospital ID based on the number of unpaid medical records.
     *
     * @param hospitalID the hospital ID to check
     * @return the total amount due based on the unpaid records, or {@code -1} if an error occurs
     */
    public int calculate(String hospitalID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PAYMENT))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID)) {
                    int numberOfUnpaid = Integer.parseInt(parts[1]);
                    int totalAmount = numberOfUnpaid * 70; // Assuming a fixed rate per unpaid record
                    return totalAmount;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Payment.csv: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid data format in Payment.csv: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Updates the unpaid count for a specified appointment ID by increasing the unpaid count by 1.
     * If the hospital ID does not exist in the payment records, a new record is created.
     *
     * @param appID the appointment ID associated with the payment
     */
    public void recalculatePaymentsCSV(String appID) {
        String hospitalID = null;

        // Step 1: Retrieve the hospital ID associated with the appointment
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_APPOINTMENT))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(appID)) {
                    hospitalID = parts[1];
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading Appointment.csv: " + e.getMessage());
        }

        List<String[]> allRecords = new ArrayList<>();
        boolean hospitalIDFound = false;

        // Step 2: Update the payment record for the hospital ID, or create a new entry if not found
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH_PAYMENT))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(hospitalID)) {
                    int unpaidCount = Integer.parseInt(parts[1]) + 1;
                    parts[1] = String.valueOf(unpaidCount);
                    hospitalIDFound = true;
                }
                allRecords.add(parts);
            }
        } catch (IOException e) {
            System.err.println("Error reading Payment.csv: " + e.getMessage());
            return;
        }

        // Add a new record if the hospital ID was not found
        if (!hospitalIDFound && hospitalID != null) {
            allRecords.add(new String[] { hospitalID, "1", "0" });
        }

        // Step 3: Write the updated records back to the Payment.csv file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH_PAYMENT))) {
            writer.write("PatientID,numberOfUnpaid,numberOfPaid");
            writer.newLine();
            for (String[] record : allRecords) {
                writer.write(String.join(",", record));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to Payment.csv: " + e.getMessage());
        }
    }
}
