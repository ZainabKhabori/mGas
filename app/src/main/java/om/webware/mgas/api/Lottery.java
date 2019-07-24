package om.webware.mgas.api;

import java.util.Date;

public class Lottery {

    private String id;
    private Date startDate;
    private Date endDate;
    private String winner;
    private String winningCode;

    public Lottery() {
        //
    }

    public Lottery(String id, Date startDate, Date endDate, String winner, String winningCode) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.winner = winner;
        this.winningCode = winningCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinningCode() {
        return winningCode;
    }

    public void setWinningCode(String winningCode) {
        this.winningCode = winningCode;
    }
}
