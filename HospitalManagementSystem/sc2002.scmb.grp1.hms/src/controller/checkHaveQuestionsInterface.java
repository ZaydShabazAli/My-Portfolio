package controller;

/**
 * Interface for handling security questions related to user authentication.
 * Provides methods to check if a user has set security questions, verify answers, 
 * and retrieve the security question for a given user.
 */
public interface checkHaveQuestionsInterface {
/**
     * Checks if a specific user has set up security questions.
     *
     * @param hospitalID the ID of the hospital user.
     * @return {@code true} if the user has set security questions, {@code false} otherwise.
     */
    public boolean checkHaveQuestions(String hospitalID);
/**
     * Verifies the answer to a security question for a specific user.
     *
     * @param hospitalID the ID of the hospital user.
     * @param answer the answer provided for verification.
     * @return {@code true} if the answer is correct, {@code false} otherwise.
     */
    public boolean questionVerification(String hospitalID, String answer);
    /**
     * Retrieves the security question for a specific user.
     *
     * @param hospitalID the ID of the hospital user.
     * @return the security question set by the user.
     */
    public String returnQuestion(String hospitalID);

}
