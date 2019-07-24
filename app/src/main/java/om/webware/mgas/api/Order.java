package om.webware.mgas.api;

import java.util.Date;

/**
 * Created by Zainab on 5/17/2019.
 */

public class Order {

    private String id;
    private String consumerId;
    private String driverId;
    private String locationId;
    private Date orderDate;
    private String deliveryOptionId;
    private boolean climbStairs;
    private double totalCost;
    private String status;
    private String arabicStatus;
    private OrderServices services;
    private Feedbacks feedbacks;

    public Order() {
        //
    }

    public Order(String id, String consumerId, String driverId, String locationId, Date orderDate,
                 String deliveryOptionId, boolean climbStairs, double totalCost, String status,
                 String arabicStatus, OrderServices services, Feedbacks feedbacks) {
        this.id = id;
        this.consumerId = consumerId;
        this.driverId = driverId;
        this.locationId = locationId;
        this.orderDate = orderDate;
        this.deliveryOptionId = deliveryOptionId;
        this.climbStairs = climbStairs;
        this.totalCost = totalCost;
        this.status = status;
        this.arabicStatus = arabicStatus;
        this.services = services;
        this.feedbacks = feedbacks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryOptionId() {
        return deliveryOptionId;
    }

    public void setDeliveryOptionId(String deliveryOptionId) {
        this.deliveryOptionId = deliveryOptionId;
    }

    public boolean isClimbStairs() {
        return climbStairs;
    }

    public void setClimbStairs(boolean climbStairs) {
        this.climbStairs = climbStairs;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArabicStatus() {
        return arabicStatus;
    }

    public void setArabicStatus(String arabicStatus) {
        this.arabicStatus = arabicStatus;
    }

    public OrderServices getServices() {
        return services;
    }

    public void setServices(OrderServices services) {
        this.services = services;
    }

    public Feedbacks getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Feedbacks feedbacks) {
        this.feedbacks = feedbacks;
    }
}
