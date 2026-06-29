package api_payloads;

public class Store {
    int order_id;
    String quantity;
    int shipDate;
    //String department[];
    String status;

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getShip_date() {
        return shipDate;
    }

    public void setShip_date(int ship_date) {
        this.shipDate = ship_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
