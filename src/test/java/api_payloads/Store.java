package api_payloads;

/**
 * Payload for the Petstore {@code /store/order} API.
 *
 * Field names match the JSON the API expects, and the getters/setters now
 * line up with their fields (the old {@code shipDate} field had
 * {@code getShip_date}/{@code setShip_date} accessors, which broke mapping).
 */
public class Store {

    private long id;
    private int quantity;
    private String shipDate;
    private String status;
    private boolean complete;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getShipDate() {
        return shipDate;
    }

    public void setShipDate(String shipDate) {
        this.shipDate = shipDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
