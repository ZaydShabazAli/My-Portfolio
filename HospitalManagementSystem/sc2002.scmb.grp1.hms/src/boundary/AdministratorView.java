package boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import controller.AdministratorController;
import controller.AppointmentController;
import controller.AppointmentOutcomeController;
import controller.MenuInterface;
import controller.SecurityQuestionsController;
import entity.User;
import entity.MedicationInventory;
import entity.ReplenishmentRequests;
import entity.Appointment;

/**
 * The AdministratorView class provides the user interface for administrators
 * in the Hospital Management System. It allows administrators to manage
 * hospital staff, appointments, medication inventory, and security questions.
 * This class implements the MenuInterface.
 */

public class AdministratorView implements MenuInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final AdministratorController adminControl = new AdministratorController();
    private final AppointmentController appControl = new AppointmentController();
    private final AppointmentOutcomeController outcomeControl = new AppointmentOutcomeController();

    /**
     * Displays the main menu for administrators and handles user input.
     * Administrators can view/manage staff, appointments, inventory, and set security questions.
     * 
     * @param user The User object representing the logged-in administrator.
     * @throws IOException if there is an error in I/O operations.
     */

    public void Menu(User user) throws IOException {
        while (true) {
            System.out.println();
            System.out.println();
            System.out.println("+------------------------------------------------+");
            System.out.println("|               Administrator Menu               |");
            System.out.println("+------------------------------------------------+");
            System.out.println("| 1. View and Manage Hospital Staff              |");
            System.out.println("| 2. View Appointment Details                    |");
            System.out.println("| 3. View and Manage Inventory                   |");
            System.out.println("| 4. Set Security Question for Recovery          |");
            System.out.println("| 5. Logout                                      |");
            System.out.println("+------------------------------------------------+");
            System.out.println();

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println();
                    System.out.println("+------------------------------------------------+");
                    System.out.println("|         View and Manage Hospital Staff         |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println("| 1. View Staff                                  |");
                    System.out.println("| 2. Add Staff                                   |");
                    System.out.println("| 3. Remove Staff                                |");
                    System.out.println("| 4. Update Staff                                |");
                    System.out.println("| 5. Return to Admin Menu                        |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println();

                    int staffChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    /**
                     * Processes staff management options including viewing, adding,
                     * removing, and updating staff members.
                     */
                    while (staffChoice != 5) {
                        switch (staffChoice) {
                            case 1:
                                System.out.println();
                                System.out.println("+------------------------------------------------+");
                                System.out.println("|                   View Staff                   |");
                                System.out.println("+------------------------------------------------+");
                                System.out.println("| 1. All Staff                                   |");
                                System.out.println("| 2. By Role                                     |");
                                System.out.println("| 3. By Gender                                   |");
                                System.out.println("| 4. By Age                                      |");
                                System.out.println("| 5. Return to view menu                         |");
                                System.out.println("+------------------------------------------------+");
                                System.out.println();

                                int viewChoice = scanner.nextInt();
                                scanner.nextLine();

                                while (viewChoice != 5) {
                                    List<User> view = new ArrayList<>();
                                    switch (viewChoice) {
                                        case 1:
                                            view = adminControl.viewStaff("All");
                                            adminControl.displayStaffList(view);
                                            break;
                                        case 2:
                                            System.out.println(
                                                    "Please choose staff type to view (Admin/Doctor/Pharmacist) ");
                                            String role = scanner.next();
                                            view = adminControl.viewStaff(role);
                                            adminControl.displayStaffList(view);
                                            break;
                                        case 3:
                                            System.out.println("Please choose staff gender to view (Male/Female) ");
                                            String gender = scanner.next();
                                            view = adminControl.viewStaff(gender);
                                            adminControl.displayStaffList(view);
                                            break;
                                        case 4:
                                            System.out.println(
                                                    "Please choose staff age range to view (20/30/40) ");
                                            String age = scanner.next();
                                            view = adminControl.viewStaff(age);
                                            adminControl.displayStaffList(view);
                                            break;
                                        case 5:
                                            System.out.println("Returning to view menu...");
                                            break;
                                        default:
                                            System.out.println("Invalid option. Please try again.");
                                            break;

                                    }
                                    System.out.println();
                                    System.out.println("+------------------------------------------------+");
                                    System.out.println("|                   View Staff                   |");
                                    System.out.println("+------------------------------------------------+");
                                    System.out.println("| 1. All Staff                                   |");
                                    System.out.println("| 2. By Role                                     |");
                                    System.out.println("| 3. By Gender                                   |");
                                    System.out.println("| 4. By Age                                      |");
                                    System.out.println("| 5. Return to view menu                         |");
                                    System.out.println("+------------------------------------------------+");
                                    System.out.println();

                                    viewChoice = scanner.nextInt();
                                    scanner.nextLine();
                                }
                                break;
                            case 2:
                                System.out.println("Choose staff role (Admin/Doctor/Pharmacist)");
                                String roleChoice = scanner.next();
                                scanner.nextLine(); // Consume newline

                                if (roleChoice.equals("Admin")) {
                                    System.out.println("Please enter user ID: ");
                                    String userID = scanner.next();
                                    System.out.println("Please enter user name: ");
                                    String userName = scanner.next();
                                    System.out.println("Please enter user gender: ");
                                    String userGender = scanner.next();
                                    System.out.println("Please enter user age: ");
                                    String userAge = scanner.next();
                                    System.out.println("Please enter user email: ");
                                    String userEmail = scanner.next();
                                    System.out.println("Please enter user contact: ");
                                    String userContact = scanner.next();

                                    adminControl.addAdmin(userID, userName, roleChoice, userName, userGender, userAge,
                                            userEmail, userContact);
                                } else if (roleChoice.equals("Doctor")) {
                                    System.out.println("Please enter user ID: ");
                                    String userID = scanner.next();
                                    System.out.println("Please enter user name: ");
                                    String userName = scanner.next();
                                    System.out.println("Please enter user gender: ");
                                    String userGender = scanner.next();
                                    System.out.println("Please enter user age: ");
                                    String userAge = scanner.next();
                                    System.out.println("Please enter user specialization: ");
                                    String specialization = scanner.next();
                                    System.out.println("Please enter user email: ");
                                    String userEmail = scanner.next();
                                    System.out.println("Please enter user contact: ");
                                    String userContact = scanner.next();

                                    adminControl.addDoctor(userID, userName, roleChoice, userName, userGender, userAge,
                                            specialization, userEmail, userContact);
                                } else if (roleChoice.equals("Pharmacist")) {
                                    System.out.println("Please enter user ID: ");
                                    String userID = scanner.next();
                                    System.out.println("Please enter user name: ");
                                    String userName = scanner.next();
                                    System.out.println("Please enter user gender: ");
                                    String userGender = scanner.next();
                                    System.out.println("Please enter user age: ");
                                    String userAge = scanner.next();
                                    System.out.println("Please enter user email: ");
                                    String userEmail = scanner.next();
                                    System.out.println("Please enter user contact: ");
                                    String userContact = scanner.next();

                                    adminControl.addPharmacist(userID, userName, roleChoice, userName, userGender,
                                            userAge, userEmail, userContact);
                                } else {
                                    System.out.println(
                                            "Invalid role choice. Please enter a valid role (Admin/Doctor/Pharmacist).");
                                }
                                break;
                            case 3:
                                System.out.println("Choose staff role (Admin/Doctor/Pharmacist)");
                                roleChoice = scanner.next();
                                scanner.nextLine(); // Consume newline

                                if (roleChoice.equals("Admin")) {
                                    System.out.println("Please enter the user ID of the Admin to remove: ");
                                    String userID = scanner.next();
                                    adminControl.removeAdmin(userID);
                                } else if (roleChoice.equals("Doctor")) {
                                    System.out.println("Please enter the user ID of the Doctor to remove: ");
                                    String userID = scanner.next();
                                    adminControl.removeDoctor(userID);
                                } else if (roleChoice.equals("Pharmacist")) {
                                    System.out.println("Please enter the user ID of the Pharmacist to remove: ");
                                    String userID = scanner.next();
                                    adminControl.removePharmacist(userID);
                                } else {
                                    System.out.println(
                                            "Invalid role choice. Please enter a valid role (Admin/Doctor/Pharmacist).");
                                }
                                break;
                            case 4:
                                System.out.println("Choose staff role to update (Admin/Doctor/Pharmacist): ");
                                String updateRole = scanner.nextLine();

                                System.out.println("Enter the User ID of the staff member to update: ");
                                String updateStaffId = scanner.nextLine();

                                adminControl.updateStaffInfo(updateRole, updateStaffId);

                                break;
                            case 5:
                                System.out.println("Returning...");
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }

                        System.out.println();
                        System.out.println("+------------------------------------------------+");
                        System.out.println("|         View and Manage Hospital Staff         |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println("| 1. View Staff                                  |");
                        System.out.println("| 2. Add Staff                                   |");
                        System.out.println("| 3. Remove Staff                                |");
                        System.out.println("| 4. Update Staff                                |");
                        System.out.println("| 5. Return to Admin Menu                        |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println();

                        staffChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                    }
                    break;
                case 2:

                    System.out.println();
                    System.out.println("+------------------------------------------------+");
                    System.out.println("|            View Appointment Details            |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println("| 1. View Appointments                           |");
                    System.out.println("| 2. View Confirmed Appointments                 |");
                    System.out.println("| 3. View Appointment Outcome Records            |");
                    System.out.println("| 4. Return to Admin Menu                        |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println();

                    int appointmentChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    while (appointmentChoice != 4) {
                        switch (appointmentChoice) {
                            case 1:
                                System.out.println("Viewing appointments...");
                                List<Appointment> appointments = adminControl.appointmentList();

                                System.out.println(
                                        "+---------------+-------------+-----------+------------+------------+-----------+-----------+----------+");
                                System.out.printf("| %-13s | %-11s | %-9s | %-10s | %-10s | %-9s | %-8s |\n",
                                        "Appointment ID", "Patient ID", "Doctor ID", "Date", "Start Time", "End Time",
                                        "Status");
                                System.out.println(
                                        "+---------------+-------------+-----------+------------+------------+-----------+-----------+----------+");

                                for (Appointment appointment : appointments) {
                                    System.out.printf("| %-13s | %-11s | %-9s | %-10s | %-10s | %-9s | %-8s |\n",
                                            appointment.getAppointmentId(),
                                            appointment.getPatientId(),
                                            appointment.getDoctorId(),
                                            appointment.getAppointmentDate(),
                                            appointment.getStartTime(),
                                            appointment.getEndTime(),
                                            appointment.getStatus());
                                }

                                System.out.println(
                                        "+---------------+-------------+-----------+------------+------------+-----------+-----------+----------+");

                                break;
                            case 2:
                                System.out.println("Enter doctor id: ");
                                String doctorID = scanner.next();
                                scanner.nextLine(); // Consume newline

                                System.out.println("Viewing confirmed appointments for doctor...");
                                appControl.listConfirmedAppointments(doctorID);

                                break;
                            case 3:
                                System.out.println("Viewing appointment outcome records...");
                                outcomeControl.viewAppointmentOutcomes();
                                break;
                            case 4:
                                System.out.println("Returning to admin menu...");
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }

                        System.out.println();
                        System.out.println("+------------------------------------------------+");
                        System.out.println("|            View Appointment Details            |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println("| 1. View Appointments                           |");
                        System.out.println("| 2. View Confirmed Appointments                 |");
                        System.out.println("| 3. View Appointment Outcome Records            |");
                        System.out.println("| 4. Return to Admin Menu                        |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println();

                        appointmentChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    }

                    break;
                case 3:

                    System.out.println();
                    System.out.println("+------------------------------------------------+");
                    System.out.println("|           View and Manage Inventory            |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println("| 1. View Inventory                              |");
                    System.out.println("| 2. View Replenish Requests                     |");
                    System.out.println("| 3. Approve Replenish Request                   |");
                    System.out.println("| 4. Return to Admin Menu                        |");
                    System.out.println("+------------------------------------------------+");
                    System.out.println();

                    int inventoryChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    while (inventoryChoice != 4) {
                        switch (inventoryChoice) {
                            case 1:
                                System.out.println("Viewing medication inventory...");
                                List<MedicationInventory> inventory = adminControl.viewInventory();

                                System.out.println("+-----------------------+-------------+-------------+");
                                System.out.printf("| %-21s | %-11s | %-11s |\n", "Medication Name", "Stock Level",
                                        "Alert Level");
                                System.out.println("+-----------------------+-------------+-------------+");

                                for (MedicationInventory medication : inventory) {
                                    System.out.printf("| %-21s | %-11d | %-11d |\n",
                                            medication.getMedicationName(),
                                            medication.getStockLevel(),
                                            medication.getStockAlertLevel());
                                }

                                System.out.println("+-----------------------+-------------+-------------+");
                                break;
                            case 2:
                                System.out.println("Viewing pending replenish requests...");

                                List<ReplenishmentRequests> pendingRequests = adminControl.viewRequests();

                                System.out.println("+-----------------------+------------+-----------+");
                                System.out.printf("| %-21s | %-10s | %-9s |\n", "Medication Name", "Request ID",
                                        "Status");
                                System.out.println("+-----------------------+------------+-----------+");

                                for (ReplenishmentRequests request : pendingRequests) {
                                    System.out.printf("| %-21s | %-10d | %-9s |\n",
                                            request.getMedicationName(),
                                            request.getRequestId(),
                                            request.getStatus());
                                }

                                System.out.println("+-----------------------+------------+-----------+");
                                break;
                            case 3:
                                System.out.println("Request ID to be approved: ");
                                int requestID = scanner.nextInt();

                                adminControl.replenishStock(requestID);
                                break;

                            case 4:
                                System.out.println("Returning to admin menu...");
                                break;
                            default:
                                System.out.println("Invalid option. Please try again.");
                                break;
                        }

                        System.out.println();
                        System.out.println("+------------------------------------------------+");
                        System.out.println("|           View and Manage Inventory            |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println("| 1. View Inventory                              |");
                        System.out.println("| 2. View Replenish Requests                     |");
                        System.out.println("| 3. Approve Replenish Request                   |");
                        System.out.println("| 4. Return to Admin Menu                        |");
                        System.out.println("+------------------------------------------------+");
                        System.out.println();

                        inventoryChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                    }

                    break;
                case 4:
                    SecurityQuestionsController sqc = new SecurityQuestionsController();
                    System.out.println("Please enter a security question");
                    String question = scanner.nextLine();
                    System.out.println("Please enter the answer");
                    String answer = scanner.nextLine();
                    if (!sqc.changeSecurityQuestionControl(user.getUserId(), question, answer)) {
                        System.out.println(
                                "Sorry your security questions were not able to be set, contact an administrator");
                    } else {
                        System.out.println("Security Questions successfully set");
                    }
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }
}
