package controller;

import boundary.PasswordForgetView;

/**
 * The {@code SecurityQuestionsController} class provides methods for managing security questions 
 * for different user roles. It includes operations for checking if security questions are enabled,
 * enabling them, and changing them for a specific hospital ID based on user roles.
 */
public class SecurityQuestionsController {

    /**
     * Checks if security questions are enabled for a given hospital ID.
     *
     * @param hospitalID the ID of the hospital to check
     * @return {@code true} if security questions are enabled, {@code false} otherwise
     */
    public boolean checkHaveQuestions(String hospitalID) {
        String role = extractPrefix(hospitalID);
        RepositoryController repositoryController = new RepositoryController();
        checkHaveQuestionsInterface repository = (checkHaveQuestionsInterface) repositoryController.getRepository(role);

        if (repository == null) {
            return false;
        }

        return repository.checkHaveQuestions(hospitalID);
    }

    /**
     * Enables security questions for a specified hospital ID by interacting with 
     * the {@link PasswordForgetView} menu.
     *
     * @param hospitalID the ID of the hospital to enable questions for
     * @return {@code true} if security questions were successfully enabled, {@code false} otherwise
     */
    public boolean enableQuestions(String hospitalID) {
        String role = extractPrefix(hospitalID);
        RepositoryController repositoryController = new RepositoryController();
        checkHaveQuestionsInterface repository = (checkHaveQuestionsInterface) repositoryController.getRepository(role);

        if (repository == null) {
            return false;
        }

        PasswordForgetView passwordForget = new PasswordForgetView();
        return passwordForget.Menu(hospitalID, repository);
    }

    /**
     * Extracts the prefix from the hospital ID to determine the role associated with it.
     * <p>
     * The role is determined based on the prefix of the ID:
     * <ul>
     *     <li>"PH" for Pharmacist</li>
     *     <li>"P" for Patient</li>
     *     <li>"A" for Administrator</li>
     *     <li>"D" for Doctor</li>
     * </ul>
     * </p>
     *
     * @param hospitalID the hospital ID from which to extract the prefix
     * @return a string representing the role prefix or "NULL" if the prefix does not match a known role
     */
    private static String extractPrefix(String hospitalID) {
        String prefix;
        if (hospitalID.startsWith("PH")) {
            prefix = "PH";
        } else if (hospitalID.startsWith("P")) {
            prefix = "P";
        } else if (hospitalID.startsWith("A")) {
            prefix = "A";
        } else if (hospitalID.startsWith("D")) {
            prefix = "D";
        } else {
            prefix = "NULL";
        }
        return prefix;
    }

    /**
     * Changes the security question for a user identified by hospital ID.
     *
     * @param hospitalID the ID of the hospital to update the question for
     * @param question the new security question
     * @param answer the answer to the security question
     * @return {@code true} if the security question was successfully changed, {@code false} otherwise
     */
    public boolean changeSecurityQuestionControl(String hospitalID, String question, String answer) {
        String role = extractPrefix(hospitalID);
        RepositoryController rc = new RepositoryController();
        ChangeSecurityQuestionInterface repository = (ChangeSecurityQuestionInterface) rc.getRepository(role);

        if (repository == null) {
            System.out.println("| Invalid ID or role. Cannot change questions.   |");
            System.out.println("+------------------------------------------------+\n");
            return false;
        }

        boolean isChanged = repository.changeSecurityQuestion(hospitalID, question, answer);

        if (isChanged) {
            System.out.println("| Security question changed successfully.        |");
        } else {
            System.out.println("| Failed to change security question.            |");
        }
        System.out.println("+------------------------------------------------+\n");
        return isChanged;
    }
}
