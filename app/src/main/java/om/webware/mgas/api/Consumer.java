package om.webware.mgas.api;

import android.annotation.SuppressLint;
import android.database.Cursor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Consumer {

    private String userId;
    private char gender;
    private Date dateOfBirth;
    private int age;
    private String mainLocationId;
    private String mainLocationName;

    public Consumer() {
        //
    }

    @SuppressLint("SimpleDateFormat")
    public Consumer(Cursor cursor) {
        try {
            while (cursor.moveToNext()) {
                this.userId = cursor.getString(0);
                this.gender = cursor.getString(1).charAt(0);
                this.dateOfBirth = cursor.getString(2) == null ? null :
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(2));
                this.age = cursor.getInt(3);
                this.mainLocationId = cursor.getString(4);
                this.mainLocationName = cursor.getString(5);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Consumer(String userId, char gender, Date dateOfBirth, int age, String mainLocationId, String mainLocationName) {
        this.userId = userId;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.mainLocationId = mainLocationId;
        this.mainLocationName = mainLocationName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getMainLocationId() {
        return mainLocationId;
    }

    public void setMainLocationId(String mainLocationId) {
        this.mainLocationId = mainLocationId;
    }

    public String getMainLocationName() {
        return mainLocationName;
    }

    public void setMainLocationName(String mainLocationName) {
        this.mainLocationName = mainLocationName;
    }
}
