package om.webware.mgas.api;

import java.util.Date;

public class DeliveryIssue {

    private String id;
    private String driverId;
    private String orderId;
    private Date dateReported;
    private String issue;

    public DeliveryIssue() {
        //
    }

    public DeliveryIssue(String id, String driverId, String orderId, Date dateReported, String issue) {
        this.id = id;
        this.driverId = driverId;
        this.orderId = orderId;
        this.dateReported = dateReported;
        this.issue = issue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
