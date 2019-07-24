package om.webware.mgas.api;

import java.util.Date;

public class InteractiveNotification {

    private String id;
    private String title;
    private String arabicTitle;
    private String text;
    private String arabicText;
    private String image;
    private Date scheduledTime;
    private String correctChoice;
    private InteractiveNotifChoices choices;

    public InteractiveNotification() {
        //
    }

    public InteractiveNotification(String id, String title, String arabicTitle, String text,
                                   String arabicText, String image, Date scheduledTime,
                                   String correctChoice, InteractiveNotifChoices choices) {
        this.id = id;
        this.title = title;
        this.arabicTitle = arabicTitle;
        this.text = text;
        this.arabicText = arabicText;
        this.image = image;
        this.scheduledTime = scheduledTime;
        this.correctChoice = correctChoice;
        this.choices = choices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArabicTitle() {
        return arabicTitle;
    }

    public void setArabicTitle(String arabicTitle) {
        this.arabicTitle = arabicTitle;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getArabicText() {
        return arabicText;
    }

    public void setArabicText(String arabicText) {
        this.arabicText = arabicText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getCorrectChoice() {
        return correctChoice;
    }

    public void setCorrectChoice(String correctChoice) {
        this.correctChoice = correctChoice;
    }

    public InteractiveNotifChoices getChoices() {
        return choices;
    }

    public void setChoices(InteractiveNotifChoices choices) {
        this.choices = choices;
    }
}
