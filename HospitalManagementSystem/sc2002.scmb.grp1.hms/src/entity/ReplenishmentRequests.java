package entity;

/**
 * Represents a replenishment request for medication inventory.
 */
public class ReplenishmentRequests {
    private final int requestId; // Unique identifier for the request
    private final String medicationName; // Name of the medication to replenish
    private final int quantity; // Quantity requested for replenishment
    private String status; // Status of the request (e.g., "Pending", "Approved")

    /**
     * Constructs a ReplenishmentRequests object with the specified attributes.
     *
     * @param requestId       The unique identifier for the replenishment request.
     * @param medicationName  The name of the medication for which replenishment is requested.
     * @param quantity        The quantity of medication requested.
     * @param status          The status of the replenishment request (e.g., "Pending").
     */
    public ReplenishmentRequests(int requestId, String medicationName, int quantity, String status) {
        this.requestId = requestId;
        this.medicationName = medicationName;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * Retrieves the unique identifier of the replenishment request.
     *
     * @return The request ID.
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Retrieves the name of the medication for the replenishment request.
     *
     * @return The name of the medication.
     */
    public String getMedicationName() {
        return medicationName;
    }

    /**
     * Retrieves the quantity of medication requested for replenishment.
     *
     * @return The quantity of medication requested.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Retrieves the current status of the replenishment request.
     *
     * @return The status of the request (e.g., "Pending", "Approved").
     */
    public String getStatus() {
        return status;
    }

    /**
     * Updates the status of the replenishment request.
     *
     * @param status The new status of the request (e.g., "Approved", "Rejected").
     */
    public void setStatus(String status) {
        this.status = status;
    }
}