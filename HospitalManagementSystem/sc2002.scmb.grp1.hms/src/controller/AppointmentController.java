package controller;

import entity.Appointment;
import entity.Availability;
import entity.Doctor;
import entity.Patient;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import repository.AppointmentRepository;
import repository.AvailabilityRepository;
import repository.DoctorRepository;
import repository.PatientRepository;
/**
 * Controller class for managing appointments, including creation, cancellation, rescheduling, 
 * and listing of appointments.
 */
public class AppointmentController {
    private AppointmentRepository appointmentRepository = new AppointmentRepository();
    private AvailabilityRepository availabilityRepository = new AvailabilityRepository();
    private PatientRepository patientrepository = new PatientRepository();
    private DoctorRepository doctorrepository = new DoctorRepository();
    private AvailabilityController availabilitycontroller = new AvailabilityController();
/**
     * Creates a new appointment for a given patient.
     *
     * @param PatientID the ID of the patient scheduling the appointment.
     * @throws IOException if an I/O error occurs.
     */
    public void createAppointment(String PatientID) throws IOException {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Please enter the Availability ID for the slot you wish to select ('0' to exit): ");
            String AvailID = scanner.nextLine();

            if (AvailID.equals("0")) { // for user to exit back to menu
                System.out.println("Exiting appointment scheduling...");
                return;
            }

            // sanity check
            if (AvailID.isEmpty()) {
                continue;
            }

            Availability availslot = availabilityRepository.getAvailabilityById(AvailID);
            if (availslot == null) {
                System.out.println("The Availability ID you entered cannot be found. Please try again.");
                continue;
            }

            String ApptId = generateNextApptId();

            String patientId = PatientID;

            String doctorId = availslot.getDoctorId();

            String date = availslot.getDate();

            String starttime = availslot.getStartTime();

            String endtime = availslot.getEndTime();

            String Status = "Pending";

            Appointment newAppointment = new Appointment(ApptId, patientId, doctorId, date, starttime, endtime, Status);

            appointmentRepository.createNewAppointment(newAppointment);

            availabilityRepository.deleteAvailabilityById(AvailID);

            System.out.println("Appointment Pending Approver.");

            break;
        }
    }
/**
     * Generates the next appointment ID in sequence.
     *
     * @return the next appointment ID.
     * @throws IOException if an I/O error occurs.
     */
    // generate new appointmentid
    public String generateNextApptId() throws IOException {
        String lastApptId = appointmentRepository.getLastApptId();
        if (lastApptId == "AP000") {
            return "AP000";
        }
        String numberPart = lastApptId.substring(2);
        int nextNumber = Integer.parseInt(numberPart) + 1;
        return "AP" + String.format("%03d", nextNumber);
    }
    /**
     * Lists all pending appointments for a specific doctor.
     *
     * @param doctorId the ID of the doctor.
     * @return true if there are pending appointments, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean listPendingAppointments(String doctorId) throws IOException {
        List<Appointment> pendingAppointments = appointmentRepository.getPendingAppointmentsByDoctorId(doctorId);
    
        if (pendingAppointments.isEmpty()) {
            System.out.println("+---------------------------------------------------+");
            System.out.println("|               Pending Appointments                |");
            System.out.println("+---------------------------------------------------+");
            System.out.println("| No pending appointments found for Doctor ID: " + doctorId + " |");
            System.out.println("+---------------------------------------------------+");
            return false; // No pending appointments
        }
    
        // Header with Doctor ID
        System.out.println("+-------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                     Pending Appointments                                                   |");
        System.out.println("+-------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-12s: %-50s |\n", "Doctor ID", doctorId);
        System.out.println("+-------------------------------------------------------------------------------------------------------------+");
    
        // Table Headers
        System.out.printf("| %-15s | %-20s | %-12s | %-12s | %-10s | %-10s |\n",
                          "Appointment ID", "Patient Name", "Date", "Start Time", "End Time", "Status");
        System.out.println("+-------------------------------------------------------------------------------------------------------------+");
    
        // Table Rows
        for (Appointment appointment : pendingAppointments) {
            Patient temp = patientrepository.findPatientById(appointment.getPatientId());
            System.out.printf("| %-15s | %-20s | %-12s | %-12s | %-10s | %-10s |\n",
                              appointment.getAppointmentId(),
                              temp.getName(),
                              appointment.getAppointmentDate(),
                              appointment.getStartTime(),
                              appointment.getEndTime(),
                              appointment.getStatus());
        }
    
        System.out.println("+-------------------------------------------------------------------------------------------------------------+");
        return true; // Pending appointments exist
    }
    
    

 /**
     * Updates the status of an appointment.
     *
     * @param Appt the ID of the appointment to update.
     * @param newstatus the new status for the appointment.
     * @throws IOException if an I/O error occurs.
     */
    // Method to update the status of an appointment
    public void updateAppointmentStatus(String Appt, String newstatus) throws IOException {
        Appointment appointment = appointmentRepository.getAppointmentById(Appt);
        appointment.setStatus(newstatus);
        appointmentRepository.updateAppointment(appointment);
    }
/**
     * Checks if an appointment ID is valid for a given doctor.
     *
     * @param appointmentId the ID of the appointment to check.
     * @param doctorId the ID of the doctor.
     * @return true if the appointment ID is valid, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    // check if the appointment is valid
    public boolean isValidAppointmentId(String appointmentId, String doctorId) throws IOException {
        List<Appointment> pendingAppointments = appointmentRepository.getPendingAppointmentsByDoctorId(doctorId);
        for (Appointment appointment : pendingAppointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return true;
            }
        }
        return false;
    }
 /**
     * Checks if an appointment ID is valid for rescheduling by a patient.
     *
     * @param appointmentId the ID of the appointment to check.
     * @param patientId the ID of the patient.
     * @return true if the appointment ID is valid for rescheduling, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    // check if the appointment is valid
    public boolean isValidRescheduleAppointmentId(String appointmentId, String patientId) throws IOException {
        List<Appointment> pendingAppointments = appointmentRepository
                .getConfirmedOrPendingAppointmentsByDoctorId(patientId);
        for (Appointment appointment : pendingAppointments) {
            if (appointment.getAppointmentId().equals(appointmentId)) {
                return true;
            }
        }
        return false;
    }
/**
     * Lists all confirmed appointments for a specific doctor.
     *
     * @param doctorId the ID of the doctor.
     * @return true if there are confirmed appointments, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean listConfirmedAppointments(String doctorId) throws IOException {
        List<Appointment> confirmedAppointments = appointmentRepository.getConfirmedAppointmentsByDoctorId(doctorId);
    
        if (confirmedAppointments.isEmpty()) {
            System.out.println("+-----------------------------------------------------------------------------------------------------------+");
            System.out.println("|                                        Confirmed Appointments                                             |");
            System.out.println("+-----------------------------------------------------------------------------------------------------------+");
            System.out.printf("| Doctor ID: %-100s |\n", doctorId);
            System.out.println("+-----------------------------------------------------------------------------------------------------------+");
            System.out.println("| No confirmed appointments found.                                                                          |");
            System.out.println("+-----------------------------------------------------------------------------------------------------------+");
            return false; // Indicate no confirmed appointments
        }
    
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                                       Confirmed Appointments                                                           |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.printf("| Doctor ID: %-122s |\n", doctorId);
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------+");
        System.out.println("| Appointment ID | Patient Name          | Gender   | Age | Phone Number  | Email                | Blood Type | Date       | Time Slot   |");
        System.out.println("+----------------------------------------------------------------------------------------------------------------------------------------+");
    
        for (Appointment appointment : confirmedAppointments) {
            Patient temp = patientrepository.findPatientById(appointment.getPatientId());
            System.out.printf("| %-14s | %-20s | %-8s | %-3s | %-13s | %-20s | %-10s | %-10s | %-10s |\n",
                              appointment.getAppointmentId(),
                              temp.getName(),
                              temp.getGender(),
                              temp.getAge(),
                              temp.getPhoneNumber(),
                              temp.getEmail(),
                              temp.getBloodtype(),
                              appointment.getAppointmentDate(),
                              appointment.getStartTime() + " - " + appointment.getEndTime());
        }
    
        System.out.println("+-----------------------------------------------------------------------------------------------------------------------------------------+");
        return true; // Indicate that confirmed appointments were found
    }
    

/**
     * Lists all scheduled appointments for a specific patient.
     *
     * @param patientId the ID of the patient.
     * @throws IOException if an I/O error occurs.
     */
    public void listofScheduledAppointments(String patientId) throws IOException {
        List<Appointment> scheduleAppointments = appointmentRepository
                .getConfirmedOrPendingAppointmentsByDoctorId(patientId);

        if (scheduleAppointments.isEmpty()) {
            System.out.println("No Schediuled Appointments");
        } else {
            System.out.println("+------------------------------------------------------------------------------------+");
            System.out.println("|                                Scheduled Appointments                              |");
            System.out.println("+------------------------------------------------------------------------------------+");
            System.out.printf("| %-15s | %-12s | %-10s | %-10s | %-8s | %-12s | \n",
                    "Appointment ID", "Doctor Name", "Date", "Start Time", "End Time", "Status");
            System.out.println("+------------------------------------------------------------------------------------+");
            for (Appointment appointment : scheduleAppointments) {
                Doctor temp = doctorrepository.findDoctorById(appointment.getDoctorId());
                System.out.printf("| %-15s | %-12s | %-10s | %-10s | %-8s | %-12s | \n",
                    appointment.getAppointmentId(),
                    temp.getName(),
                    appointment.getAppointmentDate(),
                    appointment.getStartTime(),
                    appointment.getEndTime(),
                    appointment.getStatus());   
                    
            }
            System.out.println("+------------------------------------------------------------------------------------+");            
        }
    }
/**
     * Reschedules an appointment by replacing the old appointment with a new one.
     *
     * @param oldAppointmentID the ID of the old appointment.
     * @param availID the ID of the new availability slot.
     * @param patientId the ID of the patient.
     * @throws IOException if an I/O error occurs.
     */
    // reshedule appointment
    public void ScheduleAppointment(String oldAppointmentID, String availID, String patientId) throws IOException {
        Appointment oldappt = appointmentRepository.getAppointmentById(oldAppointmentID);
        availabilitycontroller.createNewRescheduleAvailability(oldappt);
        appointmentRepository.removeAppointmentById(oldappt.getAppointmentId());
        createRescheduleAppointment(patientId, availID);

    }
  /**
     * Cancels an appointment and makes the slot available for rescheduling.
     *
     * @param oldAppointmentID the ID of the appointment to cancel.
     * @throws IOException if an I/O error occurs.
     */
    // cancel appointment
    public void CancelAppointment(String oldAppointmentID) throws IOException {
        Appointment oldappt = appointmentRepository.getAppointmentById(oldAppointmentID);
        availabilitycontroller.createNewRescheduleAvailability(oldappt);
        appointmentRepository.removeAppointmentById(oldappt.getAppointmentId());

    }
/**
     * Creates a new rescheduled appointment for a patient.
     *
     * @param PatientID the ID of the patient.
     * @param AvailID the ID of the new availability slot.
     * @throws IOException if an I/O error occurs.
     */
    // create new appointment base on user new choice
    public void createRescheduleAppointment(String PatientID, String AvailID) throws IOException {

        Availability availslot = availabilityRepository.getAvailabilityById(AvailID);

        String ApptId = generateNextApptId();

        String patientId = PatientID;

        String doctorId = availslot.getDoctorId();

        String date = availslot.getDate();

        String starttime = availslot.getStartTime();

        String endtime = availslot.getEndTime();

        String Status = "Pending";

        Appointment newAppointment = new Appointment(ApptId, patientId, doctorId, date, starttime, endtime, Status);

        appointmentRepository.createNewAppointment(newAppointment);

        availabilityRepository.deleteAvailabilityById(AvailID);

        System.out.println("Appointment Pending Approval.");
    }
 /**
     * Checks if an appointment ID is valid and confirmed.
     *
     * @param appointmentId the ID of the appointment.
     * @return true if the appointment is valid and confirmed, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    public boolean isAppointmentIdValidAndConfirmed(String appointmentId) throws IOException {
        List<Appointment> allAppointments = appointmentRepository.loadAllAppointments();

        for (Appointment appointment : allAppointments) {
            if (appointment.getAppointmentId().equals(appointmentId) && appointment.getStatus().equals("Confirmed")) {
                return true;
            }
        }

        // Return false if no matching appointment is found or status is not confirmed
        return false;
    }
/**
     * Retrieves all appointments from the repository.
     *
     * @return a list of all {@link Appointment} objects.
     * @throws IOException if an I/O error occurs.
     */
    public List<Appointment> viewAppointments() throws IOException
    {
        List<Appointment> allAppointments = appointmentRepository.loadAllAppointments();
        return allAppointments;
    }
}
