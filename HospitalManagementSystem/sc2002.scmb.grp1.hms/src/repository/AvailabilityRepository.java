package repository;

import java.io.*;
import java.util.*;
import entity.Availability;
import util.CSVUtil;

/**
 * The AvailabilityRepository class manages the availability data of doctors.
 * It provides methods to load, filter, add, and delete availabilities from a CSV file.
 */
public class AvailabilityRepository {
    private static final String FILE_PATH_AVAILABILITY = "sc2002.scmb.grp1.hms//resource//Availability.csv";
    // private static final CSVUtil csvutil = new CSVUtil(); 
    
    /**
     * Loads all availability records from the CSV file.
     *
     * @return A list of all Availability objects.
     * @throws IOException if an error occurs while reading the file.
     */
 // Method to load availability data from the CSV file
    public List<Availability> loadAllAvailabilities() throws IOException {
        List<Availability> availabilities = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(FILE_PATH_AVAILABILITY));
            String line;
            // Skip the header line
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                if (fields.length == 5) {
                    String availabilityId = fields[0];
                    String doctorId = fields[1];
                    String date = fields[2];
                    String startTime = fields[3];
                    String endTime = fields[4];

                    Availability availability = new Availability(availabilityId, doctorId, date, startTime, endTime);
                    availabilities.add(availability);
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading availability data: " + e.getMessage());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return availabilities;
    }
    
    /**
     * Retrieves availabilities for a specific doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return A list of Availability objects for the specified doctor.
     * @throws IOException if an error occurs while reading the file.
     */
    // Method to filter availability by doctorId
    public List<Availability> getAvailabilityByDoctorId(String doctorId) throws IOException {
        List<Availability> allAvailability = loadAllAvailabilities();
        List<Availability> filteredAvailability = new ArrayList<>();

        for (Availability availability : allAvailability) {
            if (availability.getDoctorId().equals(doctorId)) {
                filteredAvailability.add(availability);
            }
        }

        return filteredAvailability;
    }
    

    /**
     * Creates a new availability record and appends it to the CSV file.
     *
     * @param availability The Availability object to add.
     * @throws IOException if an error occurs while writing to the file.
     */

    public void createNewAvailability(Availability availability) throws IOException {
        File file = new File(FILE_PATH_AVAILABILITY);

        // Open the CSV file in append mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            // If the file is not empty, write a newline first
            if (file.length() > 0) {
                writer.newLine();
            }

            // Format the availability data as CSV
            String availabilityData = String.join(",",
                    availability.getAvailabilityId(),
                    availability.getDoctorId(),
                    availability.getDate(),
                    availability.getStartTime(),
                    availability.getEndTime());

            // Write the new availability data to the file
            writer.write(availabilityData);
            writer.flush();
        }
        CSVUtil.removeEmptyRows(FILE_PATH_AVAILABILITY);
    }


    
    
    /**
     * Retrieves the ID of the last availability record.
     *
     * @return The last available ID, or "AV000" if no records exist.
     * @throws IOException if an error occurs while reading the file.
     */
 // Get the last availabilityId from the existing records
    public String getLastAvailId() throws IOException {
        List<Availability> available = loadAllAvailabilities();
        if (available.isEmpty()) {
            return "AV000";  // Return the base value if no records exist
        }
        String lastAvailableId = available.get(available.size() - 1).getAvailabilityId();
        return lastAvailableId;
    }
    

    /**
     * Retrieves an availability by its ID.
     *
     * @param availabilityId The ID of the availability to retrieve.
     * @return The Availability object, or null if not found.
     * @throws IOException if an error occurs while reading the file.
     */
 // Method to get availability by availabilityId
    public Availability getAvailabilityById(String availabilityId) throws IOException {
        List<Availability> allAvailabilities = loadAllAvailabilities();

        for (Availability availability : allAvailabilities) {
            if (availability.getAvailabilityId().equals(availabilityId)) {
                return availability; // Return the availability record that matches the availabilityId
            }
        }

        return null; // If no match found, return null
    }
    
     /**
     * Deletes an availability by its ID.
     *
     * @param availabilityId The ID of the availability to delete.
     * @throws IOException if an error occurs while writing to the file.
     */
 // Method to delete availability by availabilityId
    public void deleteAvailabilityById(String availabilityId) throws IOException {
        File file = new File(FILE_PATH_AVAILABILITY);
        List<Availability> allAvailabilities = loadAllAvailabilities();
        boolean found = false;

        // Filter out the availability with the matching availabilityId
        List<Availability> updatedAvailabilities = new ArrayList<>();
        for (Availability availability : allAvailabilities) {
            if (!availability.getAvailabilityId().equals(availabilityId)) {
                updatedAvailabilities.add(availability);
            } else {
                found = true;
            }
        }

        if (!found) {
            System.out.println("Availability with ID " + availabilityId + " not found.");
            return;
        }

        // Write the updated availability list back to the CSV file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            // Write the header
            writer.write("availabilityId,doctorId,date,startTime,endTime");
            writer.newLine();

            // Write each remaining availability record
            for (Availability availability : updatedAvailabilities) {
                String availabilityData = String.join(",",
                        availability.getAvailabilityId(),
                        availability.getDoctorId(),
                        availability.getDate(),
                        availability.getStartTime(),
                        availability.getEndTime());
                writer.write(availabilityData);
                writer.newLine();
            }
        }
        
        CSVUtil.removeEmptyRows(FILE_PATH_AVAILABILITY);
    }

}
