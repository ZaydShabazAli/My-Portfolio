package controller;

import entity.Doctor;
import repository.DoctorRepository;
import java.io.IOException;


/**
 * Manages doctor-related operations.
 */
public class DoctorController {
    
	private DoctorRepository doctorRepository = new DoctorRepository();
    
     /**
     * Retrieves a doctor by their unique ID.
     *
     * @param doctorId the unique ID of the doctor to retrieve.
     * @return the {@link Doctor} object if found, or {@code null} if no doctor matches the ID.
     * @throws IOException if an error occurs while accessing the repository.
     */
 // Get a doctor by DoctorID
    public Doctor getDoctorById(String doctorId) throws IOException {
        return doctorRepository.findDoctorById(doctorId);
    }
     /**
     * Prints the details of a doctor based on their unique ID.
     *
     * If the doctor exists, their details are printed to the console.
     * If no doctor is found, an appropriate message is displayed.
     *
     * @param doctorId the unique ID of the doctor whose information is to be printed.
     */
    // Example: Print the doctor's information
    public void printDoctorInfo(String doctorId) {
        try {
            Doctor doctor = getDoctorById(doctorId);
            if (doctor != null) {
                System.out.println("Doctor Details:\n" + doctor);
            } else {
                System.out.println("No doctor found with ID: " + doctorId);
            }
        } catch (IOException e) {
            System.out.println("Error retrieving doctor information: " + e.getMessage());
        }
    }
}

