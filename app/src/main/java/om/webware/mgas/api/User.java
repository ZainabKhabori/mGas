package om.webware.mgas.api;

import android.database.Cursor;
import android.util.Log;

public class User {

    private String token;
    private String id;
    private int mobileNo;
    private String email;
    private int idNo;
    private String fName;
    private String lName;
    private byte[] displayPicThumb;
    private String displayPicUrl;
    private String userType;

    public User() {
        //
    }

    public User(Cursor cursor) {
        while(cursor.moveToNext()) {
            this.token = cursor.getString(0);
            this.id = cursor.getString(1);
            this.mobileNo = cursor.getInt(2);
            this.email = cursor.getString(3);
            this.idNo = cursor.getInt(4);
            this.fName = cursor.getString(5);
            this.lName = cursor.getString(6);
            this.displayPicThumb = cursor.getBlob(7);
            this.displayPicUrl = cursor.getString(8);
            this.userType = cursor.getString(9);
        }
    }

    public User(String id, int mobileNo, String email, int idNo, String fName, String lName,
                byte[] displayPicThumb, String displayPicUrl, String userType) {
        this.id = id;
        this.mobileNo = mobileNo;
        this.email = email;
        this.idNo = idNo;
        this.fName = fName;
        this.lName = lName;
        this.displayPicThumb = displayPicThumb;
        this.displayPicUrl = displayPicUrl;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(int mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdNo() {
        return idNo;
    }

    public void setIdNo(int idNo) {
        this.idNo = idNo;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public byte[] getDisplayPicThumb() {
        return displayPicThumb;
    }

    public void setDisplayPicThumb(byte[] displayPicThumb) {
        this.displayPicThumb = displayPicThumb;
    }

    public String getDisplayPicUrl() {
        return displayPicUrl;
    }

    public void setDisplayPicUrl(String displayPicUrl) {
        this.displayPicUrl = displayPicUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
