package om.webware.mgas.api;

import java.util.Date;

public class Service {

    private String id;
    private String type;
    private String arabicType;
    private String cylinderSize;
    private String arabicCylinderSize;
    private double charge;
    private Date dateModified;

    public Service() {
        //
    }

    public Service(String id, String type, String arabicType, String cylinderSize, String arabicCylinderSize,
                   double charge, Date dateModified) {
        this.id = id;
        this.type = type;
        this.arabicType = arabicType;
        this.cylinderSize = cylinderSize;
        this.arabicCylinderSize = arabicCylinderSize;
        this.charge = charge;
        this.dateModified = dateModified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getArabicType() {
        return arabicType;
    }

    public void setArabicType(String arabicType) {
        this.arabicType = arabicType;
    }

    public String getCylinderSize() {
        return cylinderSize;
    }

    public void setCylinderSize(String cylinderSize) {
        this.cylinderSize = cylinderSize;
    }

    public String getArabicCylinderSize() {
        return arabicCylinderSize;
    }

    public void setArabicCylinderSize(String arabicCylinderSize) {
        this.arabicCylinderSize = arabicCylinderSize;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
