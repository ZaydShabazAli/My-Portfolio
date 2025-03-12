package controller;

import entity.MedicationInventory;
import java.io.IOException;
import java.util.List;

/**
 * The {@code ReplenishmentRequestService} interface defines methods for managing
 * medication replenishment requests and checking inventory levels.
 * <p>
 * Implementing classes are expected to handle the submission of new replenishment
 * requests and provide inventory information for specific medications.
 * </p>
 */
public interface ReplenishmentRequestService {

    /**
     * Submits a replenishment request for a specified medication.
     *
     * @param medicationName the name of the medication to be replenished
     * @throws IOException if an error occurs during the request submission process
     */
    void submitReplenishmentRequest(String medicationName) throws IOException;

    /**
     * Checks the inventory for a specified medication and returns relevant inventory data.
     *
     * @param medicationName the name of the medication to check in the inventory
     * @return a list of {@link MedicationInventory} objects that match the specified medication name
     * @throws IOException if an error occurs while accessing the inventory data
     */
    List<MedicationInventory> checkInventory(String medicationName) throws IOException;
}
