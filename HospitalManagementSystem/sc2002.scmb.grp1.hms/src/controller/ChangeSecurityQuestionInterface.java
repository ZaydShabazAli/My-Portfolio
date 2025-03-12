package controller;

/**
 * Interface for managing the change of security questions for users.
 * Provides functionality to update a user's security question and answer.
 */
public interface ChangeSecurityQuestionInterface {
    /**
     * Changes the security question and answer for a specific user.
     *
     * @param hospitalID the ID of the hospital user whose security question is being updated.
     * @param question the new security question.
     * @param answer the answer to the new security question.
     * @return {@code true} if the security question and answer were successfully updated, {@code false} otherwise.
     */
    public boolean changeSecurityQuestion(String hospitalID, String question, String answer);
}
