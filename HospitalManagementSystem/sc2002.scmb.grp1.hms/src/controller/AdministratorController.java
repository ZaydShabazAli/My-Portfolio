package controller;

import java.io.IOException;
import java.util.*;

import entity.User;
import entity.Administrator;
import entity.Pharmacist;
import entity.Doctor;
import entity.MedicationInventory;
import entity.Patient;
import entity.Appointment;
import entity.ReplenishmentRequests;
import controller.MedicationInventoryController;
import controller.AppointmentController;
import repository.AdministratorRepository;
import repository.ReplenishmentRequestRepository;
import repository.DoctorRepository;
import repository.PharmacistRepository;
/**
 * Controller class for managing administrator-related operations.
 * Handles inventory, staff, and replenishment request functionalities.
 */

@SuppressWarnings("unused")
public class AdministratorController {
    private final Scanner scanner = new Scanner(System.in);
    private AdministratorRepository administratorRepository = new AdministratorRepository();
    private DoctorRepository doctorRepository = new DoctorRepository();
    private PharmacistRepository pharmacistRepository = new PharmacistRepository();
    private ReplenishmentRequestRepository requestRepository = new ReplenishmentRequestRepository();
    private MedicationInventoryController inventoryController = new MedicationInventoryController();
    private AppointmentController appointmentController = new AppointmentController();
    /**
     * Adds stock for a specific medicine.
     *
     * @param medicine the name of the medicine.
     * @param amount the quantity to add.
     * @param level the alert level for the medicine.
     * @throws IOException if an I/O error occurs.
     */
    public void addStock(String medicine, int amount, int level) throws IOException {
        inventoryController.addMedicine(medicine, amount, level);
    }
    /**
     * Removes a specific medicine from stock.
     *
     * @param medicine the name of the medicine to remove.
     * @throws IOException if an I/O error occurs.
     */

    public void removeStock(String medicine) throws IOException {
        inventoryController.removeMedicine(medicine);
    }
     /**
     * Approves a replenishment request by updating the inventory and marking the request as completed.
     *
     * @param requestID the ID of the replenishment request.
     * @throws IOException if an I/O error occurs.
     */

    public void replenishStock(int requestID) throws IOException {
        ReplenishmentRequests request = requestRepository.getRequestById(requestID);
        if (request == null){
            System.out.println("Invalid request ID");
            return;
        }
        inventoryController.replenishInventory(request.getMedicationName(), request.getQuantity());
        requestRepository.updateRequestStatus(requestID, "Completed");
        System.out.println("Approving replenishment request...");
    }
    /**
     * Changes the alert level for a specific medicine.
     *
     * @param medicine the name of the medicine.
     * @param amount the new alert level.
     * @throws IOException if an I/O error occurs.
     */
    public void changeAlert(String medicine, int amount) throws IOException {
        inventoryController.updateAlert(medicine, amount);
    }
     /**
     * Retrieves a list of all medication inventory.
     *
     * @return a list of {@link MedicationInventory}.
     * @throws IOException if an I/O error occurs.
     */
    public List<MedicationInventory> viewInventory() throws IOException {
        return inventoryController.inventory();
    }

    /**
     * Retrieves a list of pending replenishment requests.
     *
     * @return a list of {@link ReplenishmentRequests}.
     * @throws IOException if an I/O error occurs.
     */
    public List<ReplenishmentRequests> viewRequests() throws IOException {
        return requestRepository.pendingRequests();
    }

    /**
     * Retrieves a list of all appointments.
     *
     * @return a list of {@link Appointment}.
     * @throws IOException if an I/O error occurs.
     */
    public List<Appointment> appointmentList() throws IOException {
        return appointmentController.viewAppointments();
    }
    /**
     * Displays a formatted table of staff information.
     *
     * @param staffList the list of staff members to display.
     */
    public void displayStaffList(List<User> staffList) {
        if (staffList.isEmpty()) {
            System.out.println("No staff members found for the selected filter.");
            return;
        }
    
        // Define column widths for each field
        int idWidth = 6;
        int nameWidth = 10;
        int roleWidth = 15;
        int genderWidth = 8;
        int ageWidth = 4;
        int emailWidth = 25;
        int contactWidth = 12;
        int specializationWidth = 15;
    
        // Print table header
        System.out.printf("%-" + idWidth + "s %-"+ nameWidth +"s %-"+ roleWidth +"s %-"+ genderWidth +"s %-"+ ageWidth +"s %-"+ emailWidth +"s %-"+ contactWidth +"s %-"+ specializationWidth +"s%n",
                "ID", "Name", "Role", "Gender", "Age", "Email", "Contact", "Specialization");
        System.out.println("---------------------------------------------------------------------------------------------------------------");
    
        // Print each user's information in the table format
        for (User user : staffList) {
            String specialization = "-"; // Default for non-doctors
    
            // Check if the user is an Administrator, Doctor, or Pharmacist, and get appropriate fields
            if (user instanceof Administrator) {
                Administrator admin = (Administrator) user;
                System.out.printf("%-" + idWidth + "s %-"+ nameWidth +"s %-"+ roleWidth +"s %-"+ genderWidth +"s %-"+ ageWidth +"s %-"+ emailWidth +"s %-"+ contactWidth +"s %-"+ specializationWidth +"s%n",
                        admin.getUserId(), admin.getName(), admin.getRole(), admin.getGender(), admin.getAge(),
                        admin.getStaffEmail(), admin.getStaffContact(), specialization);
            } else if (user instanceof Doctor) {
                Doctor doctor = (Doctor) user;
                specialization = doctor.getSpecialization(); // Get specialization for doctors
                System.out.printf("%-" + idWidth + "s %-"+ nameWidth +"s %-"+ roleWidth +"s %-"+ genderWidth +"s %-"+ ageWidth +"s %-"+ emailWidth +"s %-"+ contactWidth +"s %-"+ specializationWidth +"s%n",
                        doctor.getUserId(), doctor.getName(), doctor.getRole(), doctor.getGender(), doctor.getAge(),
                        doctor.getStaffEmail(), doctor.getStaffContact(), specialization);
            } else if (user instanceof Pharmacist) {
                Pharmacist pharmacist = (Pharmacist) user;
                System.out.printf("%-" + idWidth + "s %-"+ nameWidth +"s %-"+ roleWidth +"s %-"+ genderWidth +"s %-"+ ageWidth +"s %-"+ emailWidth +"s %-"+ contactWidth +"s %-"+ specializationWidth +"s%n",
                        pharmacist.getUserId(), pharmacist.getName(), pharmacist.getRole(), pharmacist.getGender(), pharmacist.getAge(),
                        pharmacist.getStaffEmail(), pharmacist.getStaffContact(), specialization);
            }
        }
    }
    
    /**
 * Retrieves a list of staff based on a given filter.
 *
 * @param filter the criteria to filter staff members (e.g., "All", "Doctor", "Pharmacist", "Admin", "Male", "Female", "20", "30", "40").
 * @return a filtered list of {@link User}.
 * @throws IOException if an I/O error occurs.
 */
    public List<User> viewStaff(String filter) throws IOException {
        List<Administrator> administrators = administratorRepository.loadAdministrators();
        List<Doctor> doctors = doctorRepository.loadDoctors();
        List<Pharmacist> pharmacists = pharmacistRepository.loadPharmacists();

        List<User> combined = new ArrayList<>();
        List<User> filtered = new ArrayList<>();

        combined.addAll(administrators);
        combined.addAll(doctors);
        combined.addAll(pharmacists);

        switch (filter) {
            case "All":
                return combined;
            case "Doctor":
                for (User user : combined) {
                    if (user instanceof Doctor) {
                        filtered.add(user);
                    }
                }
                break;
            case "Pharmacist":
                for (User user : combined) {
                    if (user instanceof Pharmacist) {
                        filtered.add(user);
                    }
                }
                break;
            case "Admin":
                for (User user : combined) {
                    if (user instanceof Administrator) {
                        filtered.add(user);
                    }
                }
                break;
            case "Male":
                for (User user : combined) {
                    if ("Male".equalsIgnoreCase(user.getGender())) {
                        filtered.add(user);
                    }
                }
                break;
            case "Female":
                for (User user : combined) {
                    if ("Female".equalsIgnoreCase(user.getGender())) {
                        filtered.add(user);
                    }
                }
                break;
            case "20":
                for (User user : combined) {
                    String ageString = user.getAge().trim(); // Remove any leading/trailing whitespace

                    // Check if age is numeric before parsing
                    if (ageString.matches("\\d+")) { // Ensures the age string is fully numeric
                        int age = Integer.parseInt(ageString);
                        if (age >= 20 && age < 30) {
                            filtered.add(user);
                        }
                    }
                    // If age is not numeric, skip to the next user
                }
                break;

            case "30":
                for (User user : combined) {
                    String ageString = user.getAge().trim();

                    if (ageString.matches("\\d+")) {
                        int age = Integer.parseInt(ageString);
                        if (age >= 30 && age < 40) {
                            filtered.add(user);
                        }
                    }
                    // Skip if age is not numeric
                }
                break;

            case "40":
                for (User user : combined) {
                    String ageString = user.getAge().trim();

                    if (ageString.matches("\\d+")) {
                        int age = Integer.parseInt(ageString);
                        if (age >= 40 && age <= 50) {
                            filtered.add(user);
                        }
                    }
                    // Skip if age is not numeric
                }
                break;
            default:
                System.err.println("Unrecognized filter: " + filter + ". Returning all staff.");
                return combined; // Default to returning the full list
        }
        return filtered;

    }
    /**
 * Adds a new administrator to the system.
 *
 * @param userid the unique ID of the administrator.
 * @param name the name of the administrator.
 * @param role the role of the administrator.
 * @param password the password for the administrator.
 * @param gender the gender of the administrator.
 * @param age the age of the administrator.
 * @param staffemail the email of the administrator.
 * @param staffcontact the contact number of the administrator.
 * @throws IOException if an I/O error occurs.
 */
    public void addAdmin(String userid,
            String name,
            String role,
            String password,
            String gender,
            String age,
            String staffemail,
            String staffcontact) throws IOException {
        Administrator newAdmin = new Administrator(userid,
                name,
                role,
                password,
                gender,
                age,
                staffemail,
                staffcontact);

        administratorRepository.writeAdmin(newAdmin);
    }
/**
 * Removes an administrator from the system by their user ID.
 *
 * @param userID the ID of the administrator to remove.
 * @throws IOException if an I/O error occurs.
 */
    public void removeAdmin(String userID) throws IOException {
        if (administratorRepository.hasAdministrator(userID)) {
            administratorRepository.removeAdministratorById(userID);
            System.out.println("Administrator with ID " + userID + " removed successfully.");
        } else {
            System.out.println("Administrator with ID " + userID + " does not exist.");
        }
    }
/**
 * Adds a new doctor to the system.
 *
 * @param userid the unique ID of the doctor.
 * @param name the name of the doctor.
 * @param role the role of the doctor.
 * @param password the password for the doctor.
 * @param gender the gender of the doctor.
 * @param age the age of the doctor.
 * @param specialization the specialization of the doctor.
 * @param staffemail the email of the doctor.
 * @param staffcontact the contact number of the doctor.
 * @throws IOException if an I/O error occurs.
 */
    public void addDoctor(String userid,
            String name,
            String role,
            String password,
            String gender,
            String age,
            String specialization,
            String staffemail,
            String staffcontact) throws IOException {

        Doctor newDoctor = new Doctor(userid,
                name,
                role,
                password,
                gender,
                age,
                specialization,
                staffemail,
                staffcontact);
        doctorRepository.writeDoctor(newDoctor);
        return;
    }
    /**
 * Removes a doctor from the system by their user ID.
 *
 * @param userID the ID of the doctor to remove.
 * @throws IOException if an I/O error occurs.
 */

    public void removeDoctor(String userID) throws IOException {
        if (doctorRepository.hasDoctor(userID)) {
            doctorRepository.removeDoctorById(userID);
            System.out.println("Doctor with ID " + userID + " removed successfully.");
        } else {
            System.out.println("Doctor with ID " + userID + " does not exist.");
        }
    }
    /**
 * Removes a pharmacist from the system by their user ID.
 *
 * @param userID the ID of the pharmacist to remove.
 * @throws IOException if an I/O error occurs.
 */

    public void removePharmacist(String userID) throws IOException {
        if (pharmacistRepository.hasPharmacist(userID)) {
            pharmacistRepository.removePharmacistById(userID);
            System.out.println("Pharmacist with ID " + userID + " removed successfully.");
        } else {
            System.out.println("Pharmacist with ID " + userID + " does not exist.");
        }
    }
    /**
 * Adds a new pharmacist to the system.
 *
 * @param userid the unique ID of the pharmacist.
 * @param name the name of the pharmacist.
 * @param role the role of the pharmacist.
 * @param password the password for the pharmacist.
 * @param gender the gender of the pharmacist.
 * @param age the age of the pharmacist.
 * @param staffemail the email of the pharmacist.
 * @param staffcontact the contact number of the pharmacist.
 * @throws IOException if an I/O error occurs.
 */
    public void addPharmacist(String userid,
            String name,
            String role,
            String password,
            String gender,
            String age,
            String staffemail,
            String staffcontact) throws IOException {
        Pharmacist newPharmacist = new Pharmacist(userid,
                name,
                role,
                password,
                gender,
                age,
                staffemail,
                staffcontact);
        pharmacistRepository.writePharmacist(newPharmacist);
        return;
    }
/**
 * Updates the contact information of a staff member based on their role and ID.
 *
 * @param role the role of the staff member (e.g., "Admin", "Doctor", "Pharmacist").
 * @param staffId the ID of the staff member.
 * @throws IOException if an I/O error occurs.
 */
    public void updateStaffInfo(String role, String staffId) throws IOException {
        User staff = null;

        // Retrieve the appropriate user based on their role
        if (role.equalsIgnoreCase("Admin")) {
            staff = administratorRepository.findAdminById(staffId);
        } else if (role.equalsIgnoreCase("Doctor")) {
            staff = doctorRepository.findDoctorById(staffId);
        } else if (role.equalsIgnoreCase("Pharmacist")) {
            staff = pharmacistRepository.findPharmacistById(staffId);
        }

        if (staff == null) {
            System.out.println("Staff member not found!");
            return;
        }

        // Display current contact information based on the type of user
        if (staff instanceof Administrator) {
            System.out.println("Current Email: " + ((Administrator) staff).getStaffEmail());
            System.out.println("Current Phone Number: " + ((Administrator) staff).getStaffContact());
        } else if (staff instanceof Doctor) {
            System.out.println("Current Email: " + ((Doctor) staff).getStaffEmail());
            System.out.println("Current Phone Number: " + ((Doctor) staff).getStaffContact());
        } else if (staff instanceof Pharmacist) {
            System.out.println("Current Email: " + ((Pharmacist) staff).getStaffEmail());
            System.out.println("Current Phone Number: " + ((Pharmacist) staff).getStaffContact());
        }

        System.out.print("Enter new email (leave blank to keep current): ");
        String newEmail = scanner.nextLine();

        if (!newEmail.isEmpty()) {
            if (staff instanceof Administrator) {
                ((Administrator) staff).setStaffEmail(newEmail);
            } else if (staff instanceof Doctor) {
                ((Doctor) staff).setStaffEmail(newEmail);
            } else if (staff instanceof Pharmacist) {
                ((Pharmacist) staff).setStaffEmail(newEmail);
            }
        }

        System.out.print("Enter new phone number (leave blank to keep current): ");
        String newPhoneNumber = scanner.nextLine();

        if (!newPhoneNumber.isEmpty()) {
            if (staff instanceof Administrator) {
                ((Administrator) staff).setStaffContact(newPhoneNumber);
            } else if (staff instanceof Doctor) {
                ((Doctor) staff).setStaffContact(newPhoneNumber);
            } else if (staff instanceof Pharmacist) {
                ((Pharmacist) staff).setStaffContact(newPhoneNumber);
            }
        }

        // Save the updated information
        boolean success = false;
        if (role.equalsIgnoreCase("Admin")) {
            success = administratorRepository.updateAdministrator((Administrator) staff);
        } else if (role.equalsIgnoreCase("Doctor")) {
            success = doctorRepository.updateDoctor((Doctor) staff);
        } else if (role.equalsIgnoreCase("Pharmacist")) {
            success = pharmacistRepository.updatePharmacist((Pharmacist) staff);
        }

        if (success) {
            System.out.println("Contact information updated successfully.");
        } else {
            System.out.println("Failed to update contact information.");
        }
    }

}
