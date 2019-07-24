package om.webware.mgas.api;

import android.database.Cursor;

public class Driver {

    private String userId;
    private String plateCode;
    private int plateNumber;
    private String bankName;
    private String bankBranch;
    private String bankAccountName;
    private String bankAccountNo;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String province;
    private String governorate;
    private String country;

    public Driver() {
        //
    }

    public Driver(Cursor cursor) {
        while (cursor.moveToNext()) {
            this.userId = cursor.getString(0);
            this.plateCode = cursor.getString(1);
            this.plateNumber = cursor.getInt(2);
            this.bankName = cursor.getString(3);
            this.bankBranch = cursor.getString(4);
            this.bankAccountName = cursor.getString(5);
            this.bankAccountNo = cursor.getString(6);
            this.addressLine1 = cursor.getString(7);
            this.addressLine2 = cursor.getString(8);
            this.city = cursor.getString(9);
            this.province = cursor.getString(10);
            this.governorate = cursor.getString(11);
            this.country = cursor.getString(12);
        }
    }

    public Driver(String userId, String plateCode, int plateNumber, String bankName, String bankBranch,
                  String bankAccountName, String bankAccountNo, String addressLine1, String addressLine2,
                  String city, String province, String governorate, String country) {
        this.userId = userId;
        this.plateCode = plateCode;
        this.plateNumber = plateNumber;
        this.bankName = bankName;
        this.bankBranch = bankBranch;
        this.bankAccountName = bankAccountName;
        this.bankAccountNo = bankAccountNo;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.province = province;
        this.governorate = governorate;
        this.country = country;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String plateCode) {
        this.plateCode = plateCode;
    }

    public int getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(int plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountNo() {
        return bankAccountNo;
    }

    public void setBankAccountNo(String bankAccountNo) {
        this.bankAccountNo = bankAccountNo;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getGovernorate() {
        return governorate;
    }

    public void setGovernorate(String governorate) {
        this.governorate = governorate;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
