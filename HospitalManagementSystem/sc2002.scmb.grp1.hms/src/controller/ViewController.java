package controller;

import boundary.*;

/**
 * The {@code ViewController} class provides an interface for accessing 
 * different view objects based on a specified role. This allows a unified 
 * method of retrieving views for doctors, patients, pharmacists, or administrators.
 */
public class ViewController {
    // Instances of different views for various roles
    private final DoctorView doctorView = new DoctorView();
    private final PatientView patientView = new PatientView();
    private final PharmacistView pharmacistView = new PharmacistView();
    private final AdministratorView administratorView = new AdministratorView();

    /**
     * Returns the appropriate view object based on the specified role.
     * 
     * Role codes:
     * <ul>
     *     <li>"D" - returns a {@link DoctorView}</li>
     *     <li>"P" - returns a {@link PatientView}</li>
     *     <li>"PH" - returns a {@link PharmacistView}</li>
     *     <li>"A" - returns an {@link AdministratorView}</li>
     * </ul>
     * 
     *
     * @param role a {@code String} representing the role code for the view
     * @return the corresponding view object, or {@code null} if the role code is unrecognized
     */
    public Object getView(String role) {
        return switch (role) {
            case "D" -> doctorView;
            case "P" -> patientView;
            case "PH" -> pharmacistView;
            case "A" -> administratorView;
            default -> null;
        };
    }
}
