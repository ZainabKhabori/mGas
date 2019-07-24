package om.webware.mgas.api;

/**
 * Created by Zainab on 5/17/2019.
 */

public class BankCard {

    private String id;
    private String owner;
    private String cardNo;
    private int expDateMonth;
    private int expDateYear;
    private int cvv;

    public BankCard() {
        //
    }

    public BankCard(String id, String owner, String cardNo, int expDateMonth, int expDateYear, int cvv) {
        this.id = id;
        this.owner = owner;
        this.cardNo = cardNo;
        this.expDateMonth = expDateMonth;
        this.expDateYear = expDateYear;
        this.cvv = cvv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getExpDateMonth() {
        return expDateMonth;
    }

    public void setExpDateMonth(int expDateMonth) {
        this.expDateMonth = expDateMonth;
    }

    public int getExpDateYear() {
        return expDateYear;
    }

    public void setExpDateYear(int expDateYear) {
        this.expDateYear = expDateYear;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }
}
