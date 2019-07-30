package om.webware.mgas.server;

public class MGasApi {

//    static String HOST = "https://01fd4f8c.ngrok.io";
    static String HOST = "https://mgas006-mgas-ws.webware.om:1995";

    public static String IISNODE_ERROR = "<p>iisnode encountered an error when processing the request.</p>";

    public static final String TOKEN_EXPIRY = HOST + "/tokenExpiry";

    public static final String USERS = HOST + "/users";
    public static final String CONSUMERS = HOST + "/consumers";
    public static final String DRIVERS = HOST + "/drivers";

    public static final String ACTIVE_LOTTERIES = HOST + "/activeLotteries";
    public static final String LOTTERY = HOST + "/lottery";
    public static final String LOTTERIES = HOST + "/lotteries";
    public static final String PROMOTION_CODES = HOST + "/promotionCodes";
    public static final String PROMOTION_CODES_USE = HOST + "/%s/promotionCodes";

    public static final String ORDERS = HOST + "/orders";
    public static final String FEEDBACKS = HOST + "/feedbacks";
    public static final String ORDER_FEEDBACK = HOST + "/%s/feedback";
    public static final String VERIFY_ORDER_CODE = HOST + "/%s/verifyOrderCode";
    public static final String DELIVERY_ISSUES = HOST + "/deliveryIssues";

    public static final String SERVICES = HOST + "/services";

    public static final String NOTIFICATIONS = HOST + "/services";
    public static final String INTERACTIVE_NOTIFICATIONS = HOST + "/interactiveNotifications";
    public static final String SUBMIT_CHOICE = HOST + "/%s/submitChoice";

    public static final String BANK_CARDS = HOST + "/%s/cards";
    public static final String DELETE_BANK_CARD = HOST + "/%s/cards/%s";

    public static final String ADD_LOCATION = HOST + "/%s/locations";
    public static final String USER_LOCATION = HOST + "/%s/locations/%s";

    public static final String GUEST_LOGIN = HOST + "/guestLogin";

    public static String makeUrl(String route, Object... params) {
        return String.format(route, params);
    }
}
