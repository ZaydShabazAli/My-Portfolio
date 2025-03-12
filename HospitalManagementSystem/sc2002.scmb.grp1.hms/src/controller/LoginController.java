package controller;

import entity.User;
import java.io.IOException;
import java.util.Scanner;
/**
 * Controller class for managing user login functionality.
 * Validates user credentials, handles password changes for new users, 
 * and directs authenticated users to their respective menus.
 */
public class LoginController {
 /**
     * Logs in a user based on their hospital ID and password.
     *
     * The method validates the user's credentials using the appropriate repository,
     * prompts new users with default passwords to change them, and navigates them to their role-specific menu.
     *
     * @param hospitalID the unique ID of the hospital user attempting to log in.
     * @param password the password associated with the user's hospital ID.
     * @return {@code true} if the login is successful, {@code false} otherwise.
     */
    public boolean login(String hospitalID, String password){
        String role = extractPrefix(hospitalID);
        RepositoryController repositoryController = new RepositoryController();
        ValidationInterface repository = (ValidationInterface) repositoryController.getRepository(role);
        if(repository == null){
            System.out.println("Invalid ID or password. Returning to main menu.");
            System.out.println();
            return false;
        }
        User user = repository.validateCredentials(hospitalID, password);
        if(user == null){
            System.out.println("Invalid ID or password. Returning to main menu.");
            System.out.println();
            return false;
        }
        else if (user != null && password.equals("Password")){
            System.out.println("You are a new user with the default password, please change it");
            PasswordController pc = new PasswordController();
            System.out.println("Please enter a new password");
            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(System.in);
            String newPassword;
            do {
                newPassword = scanner.nextLine();
                if (newPassword.equals("Password")) {
                    System.out.println("The password cannot be the default 'Password'. Please enter a new password:");
                }
            } while (newPassword.equals("Password"));
            if(!pc.changePassword(hospitalID, newPassword)){
                System.out.println("Something went wrong, please contact an administrator");
            }
            System.out.println("Your password has been successfully changed, proceeding to login...");
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            try{
                view.Menu(user);
            } catch(IOException e){
                System.out.println("Menu did not work ");
            }
            return true;
            

        }
        else{
            ViewController viewController = new ViewController();
            MenuInterface view = (MenuInterface) viewController.getView(role);
            try{
                view.Menu(user);
            } catch(IOException e){
                System.out.println("Menu did not work ");
            }
            return true;
        }









//        switch(role){
//            case "D":
//                System.out.println("Welcome, Doctor " + user.getName());
//                doctorView.doctorMenu(user);
//                return true;
//
//            case "P":
//                System.out.println("Welcome, Patient " + user.getName());
//                patientView.patientMenu(user);
//                return true;
//
//            case "Ph":
//                System.out.println("Welcome, Pharmacist " + user.getName());
//                pharmacistView.pharmacistMenu(user);
//                return true;
//
//            case "A":
//                System.out.println("Welcome, Administrator " + user.getName());
//                administratorView.administratorMenu(user);
//                return true;
//
//            default:
//                return false;
//
//        }


    }




 /**
     * Extracts the prefix from the hospital ID to determine the user's role.
     *
     * Prefixes are mapped as follows:
     * - "PH" for Pharmacists.
     * - "P" for Patients.
     * - "A" for Administrators.
     * - "D" for Doctors.
     *
     * @param hospitalId the hospital ID from which the prefix is extracted.
     * @return the extracted prefix or {@code "NULL"} if no valid prefix is found.
     */
    private static String extractPrefix(String hospitalId) {
        if (hospitalId.startsWith("PH")) {
            return "PH"; // Return "Ph" if the ID starts with "Ph"
        } else if (hospitalId.startsWith("P")) {
            return "P"; // Return "P" if the ID starts with "P"
        } else if (hospitalId.startsWith("A")) {
            return "A";
        } else if (hospitalId.startsWith("D")) {
            return "D";
        }
        return "NULL"; // Return an empty string if there's no matching prefix
    }
//
//    private boolean signInDoctor() {
//        User user = signIn("Doctor", doctorRepository);
//        if (user == null) {
//            System.out.println("Invalid ID or password. Returning to main menu.");
//            System.out.println();
//            return false;
//        }
//        System.out.println("Welcome, Doctor " + user.getName());
//        doctorView.doctorMenu(user);
//        return true;
//    }
//
//    private boolean signInPatient() {
//        User user = signIn("Patient", patientRepository);
//        if (user == null) {
//            System.out.println("Invalid ID or password. Returning to main menu.");
//            System.out.println();
//            return false;
//        }
//        System.out.println("Welcome, Patient " + user.getName());
//        patientView.patientMenu(user);
//        return true;
//    }
//
//    private boolean signInPharmacist() {
//        User user = signIn("Pharmacist", pharmacistRepository);
//        if (user == null) {
//            System.out.println("Invalid ID or password. Returning to main menu.");
//            System.out.println();
//            return false;
//        }
//        System.out.println("Welcome, Pharmacist " + user.getName());
//        pharmacistView.pharmacistMenu(user);
//        return true;
//    }
//
//    private boolean signInAdministrator() {
//        User user = signIn("Administrator", administratorRepository);
//        if (user == null) {
//            System.out.println("Invalid ID or password. Returning to main menu.");
//            System.out.println();
//            return false;
//        }
//        System.out.println("Welcome, Administrator " + user.getName());
//        administratorView.administratorMenu(user);
//        return true;
//    }

//    private User signIn(String role, Object repository) {
//        System.out.print("Enter " + role + " ID: ");
//        String id = scanner.nextLine();
//        System.out.print("Enter Password: ");
//        String password = scanner.nextLine();
//
//        if (repository instanceof DoctorRepository doctorRepo) {
//            return doctorRepo.validateDoctorCredentials(id, password);
//        } else if (repository instanceof PatientRepository patientRepo) {
//            return patientRepo.validatePatientCredentials(id, password);
//        } else if (repository instanceof PharmacistRepository pharmacistRepo) {
//            return pharmacistRepo.validatePharmacistCredentials(id, password);
//        } else if (repository instanceof AdministratorRepository adminRepo) {
//            return adminRepo.validateAdministratorCredentials(id, password);
//        } else {
//            System.out.println("Invalid repository type.");
//            return null;
//        }
//    }


}
