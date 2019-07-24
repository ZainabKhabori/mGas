package om.webware.mgas.api;

public class InteractiveNotifChoice {

    private String id;
    private String interactiveNotifId;
    private String title;
    private String arabicTitle;
    private String description;
    private String arabicDescription;
    private String image;
    private int selectionsNo;

    public InteractiveNotifChoice() {
        //
    }

    public InteractiveNotifChoice(String id, String interactiveNotifId, String title, String arabicTitle,
                                  String description, String arabicDescription, String image, int selectionsNo) {
        this.id = id;
        this.interactiveNotifId = interactiveNotifId;
        this.title = title;
        this.arabicTitle = arabicTitle;
        this.description = description;
        this.arabicDescription = arabicDescription;
        this.image = image;
        this.selectionsNo = selectionsNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInteractiveNotifId() {
        return interactiveNotifId;
    }

    public void setInteractiveNotifId(String interactiveNotifId) {
        this.interactiveNotifId = interactiveNotifId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArabicDescription() {
        return arabicDescription;
    }

    public void setArabicDescription(String arabicDescription) {
        this.arabicDescription = arabicDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getSelectionsNo() {
        return selectionsNo;
    }

    public void setSelectionsNo(int selectionsNo) {
        this.selectionsNo = selectionsNo;
    }
}
