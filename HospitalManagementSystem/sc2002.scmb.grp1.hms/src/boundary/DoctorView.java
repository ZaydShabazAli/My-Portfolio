package boundary;

import controller.*;
import entity.User;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * The DoctorView class handles the user interface for doctors.
 * It allows doctors to view and update patient medical records, manage appointments,
 * set availability, and handle appointment outcomes.
 */

public class DoctorView implements MenuInterface{
	private final Scanner scanner = new Scanner(System.in);
	private final MedicalRecordController medicalrecordcontroller = new MedicalRecordController();
	private final AvailabilityController availabilitycontroller = new AvailabilityController();
	private final AppointmentController appointmentcontroller = new AppointmentController();
	private final AppointmentOutcomeController outcomecontroller = new AppointmentOutcomeController();
	private final MedicationInventoryController medicationinventorycontroller = new MedicationInventoryController();
	
	/**
     * Displays the main menu for doctors and processes user input.
     *
     * @param user The logged-in doctor user.
     */
	public void Menu(User user) {
        while (true) {
        	System.out.println();
            System.out.println("+-----------------------------------------------+");
			System.out.println("|                   Doctor Menu                 |");
			System.out.println("+-----------------------------------------------+");
			System.out.println("| 1. View Patient Medical Records               |");
			System.out.println("| 2. Update Patient Medical Records             |");
			System.out.println("| 3. View Personal Schedule                     |");
			System.out.println("| 4. Set Availability for Appointments          |");
			System.out.println("| 5. Accept or Decline Appointment Requests     |");
			System.out.println("| 6. View Upcoming Appointments                 |");
			System.out.println("| 7. Record Appointment Outcome                 |");
			System.out.println("| 8. Set Security Questions for Recovery        |");
			System.out.println("| 9. Logout                                     |");
			System.out.println("+-----------------------------------------------+");
            System.out.println();

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 9) {
                System.out.println("Logging out...");
                break;
            }
            else if(choice == 1) {
            	try {
					medicalrecordcontroller.loadMedicalRecordsForDoctor(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            else if (choice == 2) {
            	handleMedicalRecordOptions(user);
            }
            
            else if (choice == 3) {
            	try {
					availabilitycontroller.loadAvailabilityByDoctor(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            else if (choice == 4) {
            	try {
					availabilitycontroller.createNewAvailability(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
            else if (choice == 5) {
            	handleAppointmnetRequestOptions(user);
            }
            
            else if (choice == 6) {
            	try {
					appointmentcontroller.listConfirmedAppointments(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

			else if (choice == 7) {
				try {
					boolean hasConfirmedAppointments = appointmentcontroller.listConfirmedAppointments(user.getUserId());
					if (!hasConfirmedAppointments) {
						continue;
					}
					// Proceed to create appointment outcome
					createAppointmentOutcomeForValidAppointment();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(choice == 8) {
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
     * Handles options for managing medical records.
     *
     * @param user The logged-in doctor user.
     */
	private void handleMedicalRecordOptions(User user) {
		while (true) {
			System.out.println();
			System.out.println("Medical Record Options:");
			System.out.println("1. Create New Medical Record");
			System.out.println("2. Update Existing Medical Record");
			System.out.println("3. Back to Doctor Menu");
			System.out.println();

			int choice = -1; // Default value for invalid input

			try {
				System.out.print("Enter your choice: ");
				choice = Integer.parseInt(scanner.nextLine().trim()); // Read and parse input
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number between 1 and 3.");
				continue; // Restart the loop
			}

			// Validate the choice
			if (choice == 1) {
				try {
					medicalrecordcontroller.createMedicalRecord(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (choice == 2) {
				try {
					medicalrecordcontroller.updateMedicalRecord(user.getUserId());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (choice == 3) {
				break; // Exit the loop to go back to the main doctor menu
			} else {
				System.out.println("Invalid choice. Please enter a number between 1 and 3.");
			}
		}
	}

	/**
     * Handles appointment requests for doctors to accept or decline.
     *
     * @param user The logged-in doctor user.
     */
	private void handleAppointmnetRequestOptions(User user) {
		while (true) {
			try {
				// List pending appointments and check if any exist
				if (!appointmentcontroller.listPendingAppointments(user.getUserId())) {
					System.out.println("Returning to the Doctor Menu...");
					break; // Exit the loop and return to the Doctor menu
				}
			} catch (IOException e) {
				e.printStackTrace();
				break; // Exit if an exception occurs
			}

			System.out.println();
			System.out.println("+------------------------------------------------+");
			System.out.println("|          Appointment Request Options           |");
			System.out.println("+------------------------------------------------+");
			System.out.println("| 1. Accept Appointment Request                  |");
			System.out.println("| 2. Decline Appointment Request                 |");
			System.out.println("| 3. Back to Doctor Menu                         |");
			System.out.println("+------------------------------------------------+");
			System.out.println();

			int choice = -1;
			try {
				System.out.print("Enter your choice: ");
				choice = scanner.nextInt();
				scanner.nextLine(); // Consume newline
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.");
				scanner.nextLine(); // Clear the invalid input
				break;
			}

			if (choice == 1 || choice == 2) {
				String status = (choice == 1) ? "Confirmed" : "Cancelled";

				while (true) {
					System.out.print("Enter the Appointment ID: ");
					String apptId = scanner.nextLine();

					try {
						if (appointmentcontroller.isValidAppointmentId(apptId, user.getUserId())) {
							appointmentcontroller.updateAppointmentStatus(apptId, status);
							System.out.println("Appointment status updated to " + status);
							break;
						} else {
							System.out.println("Invalid Appointment ID. Please try again.");
						}
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}
			} else if (choice == 3) {
				break; // Go back to the main doctor menu
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	/**
     * Creates an appointment outcome for a confirmed appointment.
     */
	public void createAppointmentOutcomeForValidAppointment() {
		
		boolean valid = false;
		String apptID = "";
		//String medicationName = "";
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		while (!valid) {
			System.out.print("Please enter the Appointment ID for the Appointment we wish to create an outcome: ");
			apptID = scanner.nextLine();

			try {
				boolean isValidAndConfirmed = appointmentcontroller.isAppointmentIdValidAndConfirmed(apptID);

				if (isValidAndConfirmed) {
					// Get all available medication names
					List<String> medicationNames = medicationinventorycontroller.getAllMedicationNames();
					if (medicationNames.isEmpty()) {
						System.out.println("No medications available.");
						valid = true;
						return;
					}

					// Create the appointment outcome
					outcomecontroller.createAppointmentOutcome(apptID);
					System.out.println("Appointment outcome created successfully for Appointment ID: " + apptID);
					appointmentcontroller.updateAppointmentStatus(apptID, "Completed");
					valid = true;
					PaymentController pc = new PaymentController();
					pc.recalculatePaymentsCSV(apptID);
				} else {
					System.out.println("The Appointment ID is not valid.");
					System.out.print("Would you like to re-enter the Appointment ID? (y/n): ");
					String userChoice = scanner.nextLine();

					if (userChoice.equalsIgnoreCase("n")) {
						System.out.println("Operation canceled. Returning to the previous menu.");
						valid = true;
					} else if (!userChoice.equalsIgnoreCase("y")) {
						System.out.println("Invalid choice. Please enter 'y' to retry or 'n' to go back.");
					}
				}
			} catch (IOException e) {
				System.out.println("Error while processing the Appointment Outcome: " + e.getMessage());
				valid = true;
			}
		}
		}
	
}
