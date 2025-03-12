package controller;

import entity.User;
import java.io.IOException;

/**
 * The {@code MenuInterface} interface defines a method for displaying a menu
 * specific to a user within the system. 
 * <p>
 * Implementing classes should provide a user-specific menu that performs actions
 * based on the user's role or permissions.
 * </p>
 */
public interface MenuInterface {

    /**
     * Displays a menu for the specified user, allowing user-specific actions.
     *
     * @param user the {@link User} object representing the user for whom the menu is displayed
     * @throws IOException if an I/O error occurs during menu display or interaction
     */
    public void Menu(User user) throws IOException;
}
