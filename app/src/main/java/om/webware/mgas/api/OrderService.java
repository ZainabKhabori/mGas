package om.webware.mgas.api;

/**
 * Created by Zainab on 5/17/2019.
 */

public class OrderService {

    private String orderId;
    private String serviceId;
    private int quantity;

    public OrderService() {
        //
    }

    public OrderService(String orderId, String serviceId, int quantity) {
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.quantity = quantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
