package boundary;

import controller.AuthenticationController;


/**
 * The main entry point for the Hospital Management System (HMS).
 * This class initializes the system and starts the authentication process.
 */

public class HMSmain {
	/**
     * The main method that serves as the entry point of the application.
     * It initializes the AuthenticationController and begins the sign-in process.
     *
     * @param args Command-line arguments.
     */

	public static void main(String[] args) {
		AuthenticationController authenticationController = new AuthenticationController();
		authenticationController.startSignIn(); 
    }

}