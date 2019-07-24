package om.webware.mgas.api;

/**
 * Created by Zainab on 5/18/2019.
 */

public class Feedback {

    private String id;
    private String orderId;
    private String author;
    private String authorUserType;
    private String message;

    public Feedback() {
        //
    }

    public Feedback(String id, String orderId, String author, String authorUserType, String message) {
        this.id = id;
        this.orderId = orderId;
        this.author = author;
        this.authorUserType = authorUserType;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUserType() {
        return authorUserType;
    }

    public void setAuthorUserType(String authorUserType) {
        this.authorUserType = authorUserType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
