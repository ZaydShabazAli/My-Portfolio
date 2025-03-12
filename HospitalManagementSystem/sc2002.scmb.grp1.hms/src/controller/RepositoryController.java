package controller;

import repository.*;

/**
 * The {@code RepositoryController} class provides a centralized way to access various 
 * repository objects based on user roles. This allows other components to retrieve
 * the appropriate repository (e.g., Doctor, Patient, Pharmacist, Administrator) 
 * based on a specified role code.
 */
public class RepositoryController {
    private final DoctorRepository doctorRepository = new DoctorRepository();
    private final PatientRepository patientRepository = new PatientRepository();
    private final PharmacistRepository pharmacistRepository = new PharmacistRepository();
    private final AdministratorRepository administratorRepository = new AdministratorRepository();

    /**
     * Returns the repository associated with a specified user role.
     * 
     * Role codes:
     * <ul>
     *     <li>"D" - returns a {@link DoctorRepository}</li>
     *     <li>"P" - returns a {@link PatientRepository}</li>
     *     <li>"PH" - returns a {@link PharmacistRepository}</li>
     *     <li>"A" - returns an {@link AdministratorRepository}</li>
     * </ul>
     * 
     *
     * @param role a {@code String} representing the role code for the desired repository
     * @return the corresponding repository object, or {@code null} if the role code is unrecognized
     */
    public Object getRepository(String role) {
        return switch (role) {
            case "D" -> doctorRepository;
            case "P" -> patientRepository;
            case "PH" -> pharmacistRepository;
            case "A" -> administratorRepository;
            default -> null;
        };
    }
}
