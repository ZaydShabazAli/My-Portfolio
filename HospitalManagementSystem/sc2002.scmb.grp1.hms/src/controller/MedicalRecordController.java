package controller;

import entity.MedicalRecord;
import entity.Patient;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import repository.DoctorRepository;
import repository.MedicalRecordRepository;
import repository.PatientRepository;

/**
 * The {@code MedicalRecordController} class provides methods for managing medical records.
 * It allows viewing, creating, and updating medical records, and checking the existence
 * of patients and records. It serves both patient and doctor views.
 */
public class MedicalRecordController {
    private final MedicalRecordRepository medicalrecordrepository = new MedicalRecordRepository();
    private final DoctorRepository doctorrepository = new DoctorRepository();
    private final PatientRepository patientrepository = new PatientRepository();

    /**
     * Loads and displays all medical records for a specified patient.
     *
     * @param patientID the ID of the patient whose medical records are to be displayed
     * @throws IOException if an error occurs while accessing the repository
     */
    public void loadMedicalRecordsForPatient(String patientID) throws IOException {
        List<MedicalRecord> records = medicalrecordrepository.findRecordsByPatientId(patientID);
        Patient temp = patientrepository.findPatientById(patientID);
        
        if (records.isEmpty()) {
            // Display patient info without records
            System.out.println("+-----------------------------------------------------------------+");
            System.out.println("|                         Medical Records                         |");
            System.out.println("+-----------------------------------------------------------------+");
            System.out.printf("| %-15s: %-36s |\n", "Patient ID", temp.getUserId());
            System.out.printf("| %-15s: %-36s |\n", "Name", temp.getName());
            System.out.printf("| %-15s: %-36s |\n", "Date of Birth", temp.getDob());
            System.out.printf("| %-15s: %-36s |\n", "Gender", temp.getGender());
            System.out.printf("| %-15s: %-36s |\n", "Phone Number", temp.getPhoneNumber());
            System.out.printf("| %-15s: %-36s |\n", "Email", temp.getEmail());
            System.out.printf("| %-15s: %-36s |\n", "Blood Type", temp.getBloodtype());
            System.out.println("+-----------------------------------------------------------------+");
            System.out.println("No medical records found.");
        } else {
            // Display patient info with medical records
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.println("|                             Medical Records                           |");
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.printf("| %-15s: %-52s |\n", "Patient ID", temp.getUserId());
            System.out.printf("| %-15s: %-52s |\n", "Name", temp.getName());
            System.out.printf("| %-15s: %-52s |\n", "Date of Birth", temp.getDob());
            System.out.printf("| %-15s: %-52s |\n", "Gender", temp.getGender());
            System.out.printf("| %-15s: %-52s |\n", "Phone Number", temp.getPhoneNumber());
            System.out.printf("| %-15s: %-52s |\n", "Email", temp.getEmail());
            System.out.printf("| %-15s: %-52s |\n", "Blood Type", temp.getBloodtype());
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.println("+-----------------------------------------------------------------------+");
            System.out.println("| Doctor Name | Record ID | Diagnosis  | Treatment    | Prescription    |");
            System.out.println("+-----------------------------------------------------------------------+");

            for (MedicalRecord record : records) {
                System.out.printf("| %-12s", doctorrepository.findDoctorById(record.getDoctorId()).getName());
                System.out.printf(record.patientMRToString());
            }
            System.out.println("+-----------------------------------------------------------------------+");
        }
    }

    /**
     * Loads and displays all medical records for a specified doctor.
     *
     * @param doctorID the ID of the doctor whose medical records are to be displayed
     * @throws IOException if an error occurs while accessing the repository
     */
    public void loadMedicalRecordsForDoctor(String doctorID) throws IOException {
        List<MedicalRecord> records = medicalrecordrepository.findRecordsByDoctorId(doctorID);

        if (records.isEmpty()) {
            System.out.println("| No medical records found for Doctor ID: " + doctorID + " |");
        } else {
            System.out.println("+-----------+------------+----------------+------------------+----------------+-----------------+");
            System.out.println("| Record ID | Patient ID | Patient Name   | Diagnosis        | Treatment      | Prescription    |");
            System.out.println("+-----------+------------+----------------+------------------+----------------+-----------------+");

            for (MedicalRecord record : records) {
                Patient temp = patientrepository.findPatientById(record.getPatientId());
                System.out.printf("| %-9s | %-10s | %-14s | %-16s | %-14s | %-15s |\n",
                        record.getRecordId(),
                        temp.getUserId(),
                        temp.getName(),
                        record.getDiagnosis(),
                        record.getTreatment(),
                        record.getPrescription());
            }

            System.out.println("+-----------+------------+----------------+------------------+----------------+-----------------+");
        }
    }

    /**
     * Generates a new unique record ID for a medical record.
     *
     * @return the generated record ID
     * @throws IOException if an error occurs while accessing the repository
     */
    public String generateNextRecordId() throws IOException {
        String lastRecordId = medicalrecordrepository.getLastRecordId();
        String numberPart = lastRecordId.substring(1); // Remove the 'R' prefix
        int nextNumber = Integer.parseInt(numberPart) + 1; // Increment the number
        return "R" + String.format("%03d", nextNumber);
    }

    /**
     * Creates a new medical record for a specified doctor.
     *
     * @param doctorID the ID of the doctor creating the medical record
     * @throws IOException if an error occurs while accessing the repository
     */
    public void createMedicalRecord(String doctorID) throws IOException {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        String recordId = generateNextRecordId();

        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();

        if (!doesPatientExist(patientId)) {
            System.out.println("Invalid Patient ID. Exiting record creation.");
            return;
        }

        System.out.print("Enter Diagnosis: ");
        String diagnosis = scanner.nextLine();

        System.out.print("Enter Treatment: ");
        String treatment = scanner.nextLine();

        System.out.print("Enter Prescription: ");
        String prescription = scanner.nextLine();

        MedicalRecord newRecord = new MedicalRecord(recordId, patientId, doctorID, diagnosis, treatment, prescription);
        medicalrecordrepository.addMedicalRecord(newRecord);
        System.out.println("Medical record added successfully.");
    }

    /**
     * Updates an existing medical record for a specified doctor.
     *
     * @param doctorID the ID of the doctor updating the medical record
     * @throws IOException if an error occurs while accessing the repository
     */
    public void updateMedicalRecord(String doctorID) throws IOException {
        loadMedicalRecordsForDoctor(doctorID);
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter Record ID to update: ");
        String recordId = scanner.nextLine();

        if (!doesRecordExist(recordId)) {
            System.out.println("Record ID not found.");
            return;
        }

        System.out.print("Enter new Diagnosis: ");
        String newDiagnosis = scanner.nextLine();

        System.out.print("Enter new Treatment: ");
        String newTreatment = scanner.nextLine();

        System.out.print("Enter new Prescription: ");
        String newPrescription = scanner.nextLine();

        boolean success = medicalrecordrepository.updateMedicalRecord(recordId, newDiagnosis, newTreatment, newPrescription);

        if (success) {
            System.out.println("Medical record updated successfully.");
        } else {
            System.out.println("An error occurred. Update failed.");
        }
    }

    /**
     * Checks if a patient exists based on their ID.
     *
     * @param patientId the ID of the patient to check
     * @return {@code true} if the patient exists, {@code false} otherwise
     * @throws IOException if an error occurs while accessing the repository
     */
    public boolean doesPatientExist(String patientId) throws IOException {
        List<Patient> patients = patientrepository.loadPatients();
        return patients.stream()
                .anyMatch(patient -> patient.getUserId().equalsIgnoreCase(patientId));
    }

    /**
     * Checks if a medical record exists based on its record ID.
     *
     * @param recordId the ID of the medical record to check
     * @return {@code true} if the record exists, {@code false} otherwise
     * @throws IOException if an error occurs while accessing the repository
     */
    public boolean doesRecordExist(String recordId) throws IOException {
        List<MedicalRecord> medicalrecords = medicalrecordrepository.loadMedicalRecords();
        return medicalrecords.stream()
                .anyMatch(record -> record.getRecordId().equalsIgnoreCase(recordId));
    }
}
