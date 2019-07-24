package om.webware.mgas.server;

public class SocketResponse {

    private String msg;
    private String arabicMsg;
    private int statusCode;
    private String statusMessage;

    public SocketResponse() {
        //
    }

    public SocketResponse(String msg, String arabicMsg, int statusCode, String statusMessage) {
        this.msg = msg;
        this.arabicMsg = arabicMsg;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getArabicMsg() {
        return arabicMsg;
    }

    public void setArabicMsg(String arabicMsg) {
        this.arabicMsg = arabicMsg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
