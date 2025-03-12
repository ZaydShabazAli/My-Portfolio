package repository;

import entity.AppointmentOutcome;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import util.CSVUtil;

/**
 * The AppointmentOutcomeRepository class provides methods to manage
 * appointment outcomes within the Hospital Management System.
 * It reads from and writes to a CSV file for data persistence.
 */
public class AppointmentOutcomeRepository {
    private static final String FILE_PATH_APPOINTMENT_OUTCOME = "sc2002.scmb.grp1.hms//resource//AppointmentOutcome.csv";
    // private static final CSVUtil csvutil = new CSVUtil();


    /**
     * Loads all appointment outcomes from the CSV file.
     *
     * @return A list of AppointmentOutcome objects.
     * @throws IOException if an error occurs while reading the file.
     */
    public List<AppointmentOutcome> loadAllAppointmentOutcomes() throws IOException {
        List<AppointmentOutcome> appointmentOutcomes = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(FILE_PATH_APPOINTMENT_OUTCOME));
            String line;

            // Skip the header line if there's one
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                // Use regex to properly split the CSV line considering potential commas inside quotes
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length == 7) {
                    String outcomeId = data[0];
                    String appointmentId = data[1];
                    String date = data[2];
                    String serviceType = data[3];
                    String prescribedMedication = data[4].replace("\"", ""); // Remove surrounding quotes if any
                    String medicationStatus = data[5];
                    String consultationNotes = data[6];

                    // Create an AppointmentOutcome object
                    AppointmentOutcome appointmentOutcome = new AppointmentOutcome(outcomeId, appointmentId, date,
                            serviceType, prescribedMedication, medicationStatus, consultationNotes);

                    // Add it to the list
                    appointmentOutcomes.add(appointmentOutcome);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading appointment outcome data: " + e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return appointmentOutcomes;
    }

    /**
     * Creates a new appointment outcome and appends it to the CSV file.
     *
     * @param appointmentOutcome The AppointmentOutcome object to be added.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void createNewAppointmentOutcome(AppointmentOutcome appointmentOutcome) throws IOException {
        File file = new File(FILE_PATH_APPOINTMENT_OUTCOME);


        // Open the file in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // If the file is not empty, write a newline first
            if (file.length() > 0) {
                writer.newLine();
            }

            // Format the appointment outcome data as CSV
            String appointmentOutcomeData = String.join(",",
                    appointmentOutcome.getOutcomeId(),
                    appointmentOutcome.getAppointmentId(),
                    appointmentOutcome.getDate(),
                    appointmentOutcome.getServiceType(),
                    appointmentOutcome.getPrescribedMedication(),
                    appointmentOutcome.getMedicationStatus(),
                    appointmentOutcome.getConsultationNotes()
            );


            // Write the new appointment outcome data to the file
            writer.write(appointmentOutcomeData);
            writer.flush();
        }

        // Clean up empty rows from the CSV file (if necessary)
        CSVUtil.removeEmptyRows(FILE_PATH_APPOINTMENT_OUTCOME);
    }

    /**
     * Retrieves an appointment outcome by its ID.
     *
     * @param appointmentOutcomeId The ID of the appointment outcome to find.
     * @return The AppointmentOutcome object, or null if not found.
     * @throws IOException if an error occurs while reading the file.
     */
    public AppointmentOutcome getAppointmentOutcomeById(String appointmentOutcomeId) throws IOException {
        List<AppointmentOutcome> allAppointmentOutcomes = loadAllAppointmentOutcomes();

        for (AppointmentOutcome appointmentOutcome : allAppointmentOutcomes) {
            if (appointmentOutcome.getOutcomeId().equals(appointmentOutcomeId)) {
                return appointmentOutcome; // Return the appointment outcome that matches the appointmentOutcomeId
            }
        }

        return null; // If no match found, return null
    }

    /**
     * Retrieves the ID of the last appointment outcome.
     *
     * @return The last available appointment outcome ID, or "AO000" if none exist.
     * @throws IOException if an error occurs while reading the file.
     */
    public String getLastAppointmentOutcomeId() throws IOException {
        List<AppointmentOutcome> appointmentOutcomes = loadAllAppointmentOutcomes();
        if (appointmentOutcomes.isEmpty()) {
            return "AO000"; // Return a default ID if no appointment outcomes exist
        }
        String lastAvailableId = appointmentOutcomes.get(appointmentOutcomes.size() - 1).getOutcomeId();
        return lastAvailableId;
    }

    /**
     * Updates an existing appointment outcome in the CSV file.
     *
     * @param updatedOutcome The updated AppointmentOutcome object.
     * @throws IOException if an error occurs while reading or writing the file.
     */
    // Method to update an appointment outcome in the CSV file
    public void updateAppointmentOutcome(AppointmentOutcome updatedOutcome) throws IOException {
        List<AppointmentOutcome> allOutcomes = loadAllAppointmentOutcomes();
        boolean updated = false;

        // Update the appointment outcome
        for (int i = 0; i < allOutcomes.size(); i++) {
            AppointmentOutcome outcome = allOutcomes.get(i);
            if (outcome.getOutcomeId().equals(updatedOutcome.getOutcomeId())) {
                allOutcomes.set(i, updatedOutcome); // Replace with updated outcome
                updated = true;
                break;
            }
        }

        // If outcome was updated, write changes back to the CSV
        if (updated) {
            writeAppointmentOutcomesToFile(allOutcomes);
        }
    }

    /**
     * Writes the list of appointment outcomes back to the CSV file.
     *
     * @param allOutcomes The list of AppointmentOutcome objects.
     * @throws IOException if an error occurs while writing to the file.
     */
    private void writeAppointmentOutcomesToFile(List<AppointmentOutcome> allOutcomes) throws IOException {
        BufferedWriter writer = null;
    
        try {
            writer = new BufferedWriter(new FileWriter(FILE_PATH_APPOINTMENT_OUTCOME));
            writer.write("OutcomeID,AppointmentID,Date,ServiceType,PrescribedMedication,MedicationStatus,ConsultationNotes\n");
    
            for (AppointmentOutcome outcome : allOutcomes) {
                String prescribedMedication = outcome.getPrescribedMedication();
    
                // Ensure medications are quoted correctly
                if (prescribedMedication.contains(",") && !prescribedMedication.startsWith("\"")) {
                    prescribedMedication = "\"" + prescribedMedication + "\"";
                }
    
                writer.write(outcome.getOutcomeId() + "," +
                        outcome.getAppointmentId() + "," +
                        outcome.getDate() + "," +
                        outcome.getServiceType() + "," +
                        prescribedMedication + "," +
                        outcome.getMedicationStatus() + "," +
                        outcome.getConsultationNotes() + "\n");
            }
        } catch (IOException e) {
            throw new IOException("Error writing appointment outcomes: " + e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

