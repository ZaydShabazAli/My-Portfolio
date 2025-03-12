package boundary;


import controller.checkHaveQuestionsInterface;
import java.util.Scanner;
/**
 * The PasswordForgetView class handles the user interface for password recovery.
 * It prompts the user with a security question and verifies the provided answer.
 */
public class PasswordForgetView {

    /**
     * Displays the password recovery menu, prompting the user to answer a security question.
     *
     * @param hospitalID The hospital ID of the user attempting to recover their password.
     * @param repository An implementation of the checkHaveQuestionsInterface that handles
     *                   retrieving the security question and verifying the answer.
     * @return true if the provided answer matches the stored answer, false otherwise.
     */

    public boolean Menu(String hospitalID, checkHaveQuestionsInterface repository) {
        String answer;
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        System.out.print("Question: ");
        System.out.println(repository.returnQuestion(hospitalID));
        System.out.println("Please enter your answer");
        answer = scanner.nextLine();
        
        return repository.questionVerification(hospitalID,answer);
        
    }
}

