package boundary;

import java.io.IOException;
import java.util.Scanner;

import controller.*;
import entity.User;

/**
 * The PatientView class handles the user interface for patients.
 * It allows patients to view and manage medical records, schedule appointments,
 * update personal information, and handle payments.
 */
public class PatientView implements MenuInterface {
    private final Scanner scanner = new Scanner(System.in);
    private final MedicalRecordController medicalrecordcontroller = new MedicalRecordController();
    private final AvailabilityController availabilitycontroller = new AvailabilityController();
    private final AppointmentController appointmentcontroller = new AppointmentController();
    private final AppointmentOutcomeController outcomecontroller = new AppointmentOutcomeController();
    private final PatientController patientcontroller = new PatientController();

    /**
     * Displays the Patient Menu and processes user input for various patient-related actions.
     *
     * @param user The logged-in patient user.
     */
    public void Menu(User user) {
        while (true) {
            System.out.println();
            System.out.println("+-------------------------------------------+");
            System.out.println("|                Patient Menu               |");
            System.out.println("+-------------------------------------------+");
            System.out.println("| 1. View Medical Record                    |");
            System.out.println("| 2. Update Personal Information            |");
            System.out.println("| 3. View Available Appointment Slots       |");
            System.out.println("| 4. Schedule an Appointment                |");
            System.out.println("| 5. Reschedule an Appointment              |");
            System.out.println("| 6. Cancel an Appointment                  |");
            System.out.println("| 7. View Scheduled Appointments            |");
            System.out.println("| 8. View Past Appointment Outcome Records  |");
            System.out.println("| 9. Make Outstanding Payments              |");
            System.out.println("| 10. Set Security Questions for Recovery   |");
            System.out.println("| 11. Logout                                |");
            System.out.println("+-------------------------------------------+");
            System.out.print("Select an option: ");
            System.out.println();

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 11) {
                System.out.println("Logging out...");
                break;
            } else if (choice == 1) {
                try {
                    medicalrecordcontroller.loadMedicalRecordsForPatient(user.getUserId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if(choice == 2){
                try{
                    patientcontroller.updatePatientInfo(user.getUserId());
                } catch (IOException e) {
					e.printStackTrace();
				}
            }

            else if (choice == 3) {
                try {
                    availabilitycontroller.viewAvailableAppointmentSlotsForPatient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (choice == 4) {
                try {
                    availabilitycontroller.viewAvailableAppointmentSlotsForPatient();
                    appointmentcontroller.createAppointment(user.getUserId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (choice == 5) {
                try {
                    handleRescheduleAppointments(user);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            else if (choice == 6) {
                try {
                    handleCancelAppointments(user);
                } catch (IOException e) {
                    
                    e.printStackTrace();
                }
            }

            else if (choice == 7) {
                try {
                    appointmentcontroller.listofScheduledAppointments(user.getUserId());
                } catch (IOException e) {
                    
                    e.printStackTrace();
                }
            }

            else if (choice == 8) {
                try {
                    outcomecontroller.getAllAppointmentOutcomesForPatient(user.getUserId());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if(choice == 9){
                PaymentController pc = new PaymentController();
                System.out.print("Your outstanding bill is $");
                System.out.println(pc.calculate(user.getUserId()));
                if(pc.calculate(user.getUserId())!= 0){
                    pc.pay(user.getUserId());
                }
            }
            else if(choice == 10) {
                SecurityQuestionsController sqc = new SecurityQuestionsController();
                System.out.println("Please enter a security question");
                String question = scanner.nextLine();
                System.out.println("Please enter the answer");
                String answer = scanner.nextLine();
                if(!sqc.changeSecurityQuestionControl(user.getUserId(), question, answer)){
                    System.out.println("Sorry your security questions were not able to be set, contact an administrator");
                }
                else{
                    System.out.println("Security Questions successfully set");
                }
            
        }
        }
    }

    /**
     * Handles rescheduling of appointments for a patient.
     *
     * @param user The logged-in patient user.
     * @throws IOException if an error occurs during I/O operations.
     */
    private void handleRescheduleAppointments(User user) throws IOException {
        //List confirmed or pending appointments for the user
        try {
            appointmentcontroller.listofScheduledAppointments(user.getUserId());
        } catch (IOException e) {
            System.out.println("Error listing appointments: " + e.getMessage());
            return;
        }

        //Validate the Appointment ID
        boolean validapptid = false;
        String Apptid = null;
        while (!validapptid) {
            System.out.println();
            System.out.println("Enter the Appointment ID of the Appointment you wish to Reschedule ('0' to exit)");
            Apptid = scanner.nextLine();

            if (Apptid.equals("0")){ // for user to exit back to menu
                System.out.println("Exiting appointment rescheduling...");
                return;
            }

            // Check if the appointment is valid for rescheduling
            if (appointmentcontroller.isValidRescheduleAppointmentId(Apptid, user.getUserId())) {
                validapptid = true; // Valid appointment ID, exit the loop
            } else {
                System.out.println("Invalid Appointment ID or the appointment cannot be rescheduled.");
            }
        }

        //View available appointment slots for the patient
        try {
            availabilitycontroller.viewAvailableAppointmentSlotsForPatient();
        } catch (IOException e) {
            System.out.println("Error viewing available slots: " + e.getMessage());
            return;
        }

        //Validate the Availability ID
        boolean validavailid = false;
        String Availid = null;
        while (!validavailid) {
            System.out.println();
            System.out.println("Enter the Availability ID of the Appointment you wish to book: ");
            Availid = scanner.nextLine();

            // Check if the availability ID exists
            if (availabilitycontroller.isAvailabilityIdExist(Availid)) {
                validavailid = true; // Valid availability ID, exit the loop
            } else {
                System.out.println("Invalid Availability ID. Please try again.");
            }
        }

        //Reschedule the appointment
        try {
            appointmentcontroller.ScheduleAppointment(Apptid, Availid, user.getUserId());
            System.out.println("You have successfully rescheduled your appointment!");
        } catch (IOException e) {
            System.out.println("Error scheduling the appointment: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Handles cancellation of appointments for a patient.
     *
     * @param user The logged-in patient user.
     * @throws IOException if an error occurs during I/O operations.
     */
    private void handleCancelAppointments(User user) throws IOException {
        // List confirmed or pending appointments for the user
        try {
            appointmentcontroller.listofScheduledAppointments(user.getUserId());
        } catch (IOException e) {
            System.out.println("Error listing appointments: " + e.getMessage());
            return;
        }
        // Validate the Appointment ID
        boolean validapptid = false;
        String Apptid = null;
        while (!validapptid) {
            System.out.println();
            System.out.println("Enter the Appointment ID of the Appointment you wish to Cancel ('0' to exit)");
            Apptid = scanner.nextLine();

            if (Apptid.equals("0")){ // for user to exit back to menu
                System.out.println("Exiting appointment rescheduling...");
                return;
            }
            
            if (appointmentcontroller.isValidRescheduleAppointmentId(Apptid, user.getUserId())) {
                validapptid = true;
            } else {
                System.out.println("Invalid Appointment ID");
            }
        }
        // cancel the appointment
        try {
            System.out.println("You have successfully cancelled your appointment!");
            appointmentcontroller.CancelAppointment(Apptid);
        } catch (IOException e) {
            System.out.println("Error cancelling the appointment: " + e.getMessage());
        }
        System.out.println();
    }
}
