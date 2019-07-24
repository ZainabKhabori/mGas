package om.webware.mgas.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import om.webware.mgas.api.BankCard;
import om.webware.mgas.api.BankCards;
import om.webware.mgas.api.Consumer;
import om.webware.mgas.api.DeliveryIssue;
import om.webware.mgas.api.DeliveryIssues;
import om.webware.mgas.api.Driver;
import om.webware.mgas.api.Feedback;
import om.webware.mgas.api.Feedbacks;
import om.webware.mgas.api.Location;
import om.webware.mgas.api.Locations;
import om.webware.mgas.api.Order;
import om.webware.mgas.api.OrderService;
import om.webware.mgas.api.OrderServices;
import om.webware.mgas.api.Orders;
import om.webware.mgas.api.Service;
import om.webware.mgas.api.Services;
import om.webware.mgas.api.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;

    public enum Tables {
        SERVICES,
        USERS,
        CONSUMERS,
        DRIVERS,
        BANK_CARDS,
        LOCATIONS,
        ORDERS,
        ORDER_SERVICES,
        FEEDBACK,
        DELIVERY_ISSUES
    }

    public DatabaseHelper(Context context) {
        super(context, "mGas", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createServicesTable = "create table " + DatabaseKeys.TABLE_SERVICES + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_TYPE + " text, " +
                DatabaseKeys.FIELD_ARABIC_TYPE + " text, " +
                DatabaseKeys.FIELD_CYLINDER_SIZE + " text, " +
                DatabaseKeys.FIELD_ARABIC_CYLINDER_SIZE + " text, " +
                DatabaseKeys.FIELD_CHARGE + " float, " +
                DatabaseKeys.FIELD_DATE_MODIFIED + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        String createUsersTable = "create table " + DatabaseKeys.TABLE_USERS + " ( " +
                DatabaseKeys.FIELD_TOKEN + " text, " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_MOBILE_NO + " int, " +
                DatabaseKeys.FIELD_EMAIL + " text, " +
                DatabaseKeys.FIELD_ID_NO + " int, " +
                DatabaseKeys.FIELD_F_NAME + " text, " +
                DatabaseKeys.FIELD_L_NAME + " text, " +
                DatabaseKeys.FIELD_DISPLAY_PIC_THUMB + " blob, " +
                DatabaseKeys.FIELD_DISPLAY_PIC_URL + " text, " +
                DatabaseKeys.FIELD_USER_TYPE + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + "), " +
                "unique(" + DatabaseKeys.FIELD_MOBILE_NO + ") " +
                ");";

        String createConsumersTable = "create table " + DatabaseKeys.TABLE_CONSUMERS + " ( " +
                DatabaseKeys.FIELD_USER_ID + " text, " +
                DatabaseKeys.FIELD_GENDER + " text, " +
                DatabaseKeys.FIELD_DATE_OF_BIRTH + " text, " +
                DatabaseKeys.FIELD_AGE + " int, " +
                DatabaseKeys.FIELD_MAIN_LOCATION_ID + " text, " +
                DatabaseKeys.FIELD_MAIN_LOCATION_NAME + " text, " +
                "primary key(" + DatabaseKeys.FIELD_USER_ID + ") " +
                ");";

        String createDriversTable = "create table " + DatabaseKeys.TABLE_DRIVERS + " ( " +
                DatabaseKeys.FIELD_USER_ID + " text, " +
                DatabaseKeys.FIELD_PLATE_CODE + " text, " +
                DatabaseKeys.FIELD_PLATE_NUMBER + " int, " +
                DatabaseKeys.FIELD_BANK_NAME + " text, " +
                DatabaseKeys.FIELD_BANK_BRANCH + " text, " +
                DatabaseKeys.FIELD_BANK_ACCOUNT_NAME + " text, " +
                DatabaseKeys.FIELD_BANK_ACCOUNT_NO + " text, " +
                DatabaseKeys.FIELD_ADDRESS_LINE_1 + " text, " +
                DatabaseKeys.FIELD_ADDRESS_LINE_2 + " text, " +
                DatabaseKeys.FIELD_CITY + " text, " +
                DatabaseKeys.FIELD_PROVINCE + " text, " +
                DatabaseKeys.FIELD_GOVERNORATE + " text, " +
                DatabaseKeys.FIELD_COUNTRY + " text, " +
                "primary key(" + DatabaseKeys.FIELD_USER_ID + ") " +
                ");";

        String createCardsTable = "create table " + DatabaseKeys.TABLE_BANK_CARDS + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_OWNER + " text, " +
                DatabaseKeys.FIELD_CARD_NO + " text, " +
                DatabaseKeys.FIELD_EXP_DATE_MONTH + " int, " +
                DatabaseKeys.FIELD_EXP_DATE_YEAR + " int, " +
                DatabaseKeys.FIELD_CVV + " int, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        String createLocationsTable = "create table " + DatabaseKeys.TABLE_LOCATIONS + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_LONGITUDE + " float, " +
                DatabaseKeys.FIELD_LATITUDE + " float, " +
                DatabaseKeys.FIELD_ADDRESS_LINE_1 + " text, " +
                DatabaseKeys.FIELD_ADDRESS_LINE_2 + " text, " +
                DatabaseKeys.FIELD_CITY + " text, " +
                DatabaseKeys.FIELD_PROVINCE + " text, " +
                DatabaseKeys.FIELD_GOVERNORATE + " text, " +
                DatabaseKeys.FIELD_COUNTRY + " text, " +
                DatabaseKeys.FIELD_LOCATION_NAME + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        String createOrdersTable = "create table " + DatabaseKeys.TABLE_ORDERS + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_CONSUMER_ID + " text, " +
                DatabaseKeys.FIELD_DRIVER_ID + " text, " +
                DatabaseKeys.FIELD_LOCATION_ID + " text, " +
                DatabaseKeys.FIELD_ORDER_DATE + " text, " +
                DatabaseKeys.FIELD_DELIVERY_OPTION_ID + " text, " +
                DatabaseKeys.FIELD_CLIMB_STAIRS + " boolean, " +
                DatabaseKeys.FIELD_TOTAL_COST + " float, " +
                DatabaseKeys.FIELD_STATUS + " text, " +
                DatabaseKeys.FIELD_ARABIC_STATUS + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        String createOrderServicesTable = "create table " + DatabaseKeys.TABLE_ORDER_SERVICES + " ( " +
                DatabaseKeys.FIELD_ORDER_ID + " text, " +
                DatabaseKeys.FIELD_SERVICE_ID + " text, " +
                DatabaseKeys.FIELD_QUANTITY + " int, " +
                "primary key(" + DatabaseKeys.FIELD_ORDER_ID + ", " + DatabaseKeys.FIELD_SERVICE_ID + ") " +
                ");";

        String createFeedbackTable = "create table " + DatabaseKeys.TABLE_FEEDBACK + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_ORDER_ID + " text, " +
                DatabaseKeys.FIELD_AUTHOR + " text, " +
                DatabaseKeys.FIELD_AUTHOR_USER_TYPE + " text, " +
                DatabaseKeys.FIELD_MESSAGE + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        String createDeliveryIssuesTable = "create table " + DatabaseKeys.TABLE_DELIVERY_ISSUES + " ( " +
                DatabaseKeys.FIELD_ID + " text, " +
                DatabaseKeys.FIELD_DRIVER_ID + " text, " +
                DatabaseKeys.FIELD_ORDER_ID + " text, " +
                DatabaseKeys.FIELD_DATE_REPORTED + " text, " +
                DatabaseKeys.FIELD_ISSUE + " text, " +
                "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                ");";

        db.execSQL(createServicesTable);
        db.execSQL(createUsersTable);
        db.execSQL(createConsumersTable);
        db.execSQL(createDriversTable);
        db.execSQL(createCardsTable);
        db.execSQL(createLocationsTable);
        db.execSQL(createOrdersTable);
        db.execSQL(createOrderServicesTable);
        db.execSQL(createFeedbackTable);
        db.execSQL(createDeliveryIssuesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String> tables = new ArrayList<>();

        while(cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }

        cursor.close();

        for(String table : tables) {
            String drop = "DROP TABLE IF EXISTS " + table;
            db.execSQL(drop);
        }

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void insert(Tables table, Object data) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        switch (table) {
            case SERVICES:
                Service service = (Service)data;

                values.put(DatabaseKeys.FIELD_ID, service.getId());
                values.put(DatabaseKeys.FIELD_TYPE, service.getType());
                values.put(DatabaseKeys.FIELD_ARABIC_TYPE, service.getArabicType());
                values.put(DatabaseKeys.FIELD_CYLINDER_SIZE, service.getCylinderSize());
                values.put(DatabaseKeys.FIELD_ARABIC_CYLINDER_SIZE, service.getArabicCylinderSize());
                values.put(DatabaseKeys.FIELD_CHARGE, service.getCharge());
                values.put(DatabaseKeys.FIELD_DATE_MODIFIED, dateFormat.format(service.getDateModified()));

                database.insert(DatabaseKeys.TABLE_SERVICES, null, values);
                values.clear();
                break;
            case USERS:
                User user = (User)data;

                values.put(DatabaseKeys.FIELD_TOKEN, user.getToken());
                values.put(DatabaseKeys.FIELD_ID, user.getId());
                values.put(DatabaseKeys.FIELD_MOBILE_NO, user.getMobileNo());
                values.put(DatabaseKeys.FIELD_EMAIL, user.getEmail());
                values.put(DatabaseKeys.FIELD_ID_NO, user.getIdNo());
                values.put(DatabaseKeys.FIELD_F_NAME, user.getfName());
                values.put(DatabaseKeys.FIELD_L_NAME, user.getlName());
                values.put(DatabaseKeys.FIELD_DISPLAY_PIC_THUMB, user.getDisplayPicThumb());
                values.put(DatabaseKeys.FIELD_DISPLAY_PIC_URL, user.getDisplayPicUrl());
                values.put(DatabaseKeys.FIELD_USER_TYPE, user.getUserType());

                database.insertOrThrow(DatabaseKeys.TABLE_USERS, null, values);
                values.clear();
                break;
            case CONSUMERS:
                Consumer consumer = (Consumer)data;

                String date = consumer.getDateOfBirth() == null ? null : dateFormat.format(consumer.getDateOfBirth());

                values.put(DatabaseKeys.FIELD_USER_ID, consumer.getUserId());
                values.put(DatabaseKeys.FIELD_GENDER, new String(new char[] {consumer.getGender()}));
                values.put(DatabaseKeys.FIELD_DATE_OF_BIRTH, date);
                values.put(DatabaseKeys.FIELD_AGE, consumer.getAge());
                values.put(DatabaseKeys.FIELD_MAIN_LOCATION_ID, consumer.getMainLocationId());
                values.put(DatabaseKeys.FIELD_MAIN_LOCATION_NAME, consumer.getMainLocationName());

                database.insert(DatabaseKeys.TABLE_CONSUMERS, null, values);
                values.clear();
                break;
            case DRIVERS:
                Driver driver = (Driver)data;

                values.put(DatabaseKeys.FIELD_USER_ID, driver.getUserId());
                values.put(DatabaseKeys.FIELD_PLATE_CODE, driver.getPlateCode());
                values.put(DatabaseKeys.FIELD_PLATE_NUMBER, driver.getPlateNumber());
                values.put(DatabaseKeys.FIELD_BANK_NAME, driver.getBankName());
                values.put(DatabaseKeys.FIELD_BANK_BRANCH, driver.getBankBranch());
                values.put(DatabaseKeys.FIELD_BANK_ACCOUNT_NAME, driver.getBankAccountName());
                values.put(DatabaseKeys.FIELD_BANK_ACCOUNT_NO, driver.getBankAccountNo());
                values.put(DatabaseKeys.FIELD_ADDRESS_LINE_1, driver.getAddressLine1());
                values.put(DatabaseKeys.FIELD_ADDRESS_LINE_2, driver.getAddressLine2());
                values.put(DatabaseKeys.FIELD_CITY, driver.getCity());
                values.put(DatabaseKeys.FIELD_PROVINCE, driver.getProvince());
                values.put(DatabaseKeys.FIELD_GOVERNORATE, driver.getGovernorate());
                values.put(DatabaseKeys.FIELD_COUNTRY, driver.getCountry());

                database.insert(DatabaseKeys.TABLE_DRIVERS, null, values);
                values.clear();
                break;
            case BANK_CARDS:
                BankCard bankCard = (BankCard)data;

                values.put(DatabaseKeys.FIELD_ID, bankCard.getId());
                values.put(DatabaseKeys.FIELD_OWNER, bankCard.getOwner());
                values.put(DatabaseKeys.FIELD_CARD_NO, bankCard.getCardNo());
                values.put(DatabaseKeys.FIELD_EXP_DATE_MONTH, bankCard.getExpDateMonth());
                values.put(DatabaseKeys.FIELD_EXP_DATE_YEAR, bankCard.getExpDateYear());
                values.put(DatabaseKeys.FIELD_CVV, bankCard.getCvv());

                database.insert(DatabaseKeys.TABLE_BANK_CARDS, null, values);
                values.clear();
                break;
            case LOCATIONS:
                Location location = (Location)data;

                values.put(DatabaseKeys.FIELD_ID, location.getId());
                values.put(DatabaseKeys.FIELD_LONGITUDE, location.getLongitude());
                values.put(DatabaseKeys.FIELD_LATITUDE, location.getLatitude());
                values.put(DatabaseKeys.FIELD_ADDRESS_LINE_1, location.getAddressLine1());
                values.put(DatabaseKeys.FIELD_ADDRESS_LINE_2, location.getAddressLine2());
                values.put(DatabaseKeys.FIELD_CITY, location.getCity());
                values.put(DatabaseKeys.FIELD_PROVINCE, location.getProvince());
                values.put(DatabaseKeys.FIELD_GOVERNORATE, location.getGovernorate());
                values.put(DatabaseKeys.FIELD_COUNTRY, location.getCountry());
                values.put(DatabaseKeys.FIELD_LOCATION_NAME, location.getLocationName());

                database.insert(DatabaseKeys.TABLE_LOCATIONS, null, values);
                values.clear();
                break;
            case ORDERS:
                Order order = (Order)data;

                String orderDate = order.getOrderDate() == null ? null : dateFormat.format(order.getOrderDate());

                values.put(DatabaseKeys.FIELD_ID, order.getId());
                values.put(DatabaseKeys.FIELD_CONSUMER_ID, order.getConsumerId());
                values.put(DatabaseKeys.FIELD_DRIVER_ID, order.getDriverId());
                values.put(DatabaseKeys.FIELD_LOCATION_ID, order.getLocationId());
                values.put(DatabaseKeys.FIELD_ORDER_DATE, orderDate);
                values.put(DatabaseKeys.FIELD_DELIVERY_OPTION_ID, order.getDeliveryOptionId());
                values.put(DatabaseKeys.FIELD_CLIMB_STAIRS, order.isClimbStairs());
                values.put(DatabaseKeys.FIELD_TOTAL_COST, order.getTotalCost());
                values.put(DatabaseKeys.FIELD_STATUS, order.getStatus());
                values.put(DatabaseKeys.FIELD_ARABIC_STATUS, order.getArabicStatus());

                database.insert(DatabaseKeys.TABLE_ORDERS, null, values);
                values.clear();
                break;
            case ORDER_SERVICES:
                OrderService orderService = (OrderService)data;

                values.put(DatabaseKeys.FIELD_ORDER_ID, orderService.getOrderId());
                values.put(DatabaseKeys.FIELD_SERVICE_ID, orderService.getServiceId());
                values.put(DatabaseKeys.FIELD_QUANTITY, orderService.getQuantity());

                database.insert(DatabaseKeys.TABLE_ORDER_SERVICES, null, values);
                values.clear();
                break;
            case FEEDBACK:
                Feedback feedback = (Feedback)data;

                values.put(DatabaseKeys.FIELD_ID, feedback.getId());
                values.put(DatabaseKeys.FIELD_ORDER_ID, feedback.getOrderId());
                values.put(DatabaseKeys.FIELD_AUTHOR, feedback.getAuthor());
                values.put(DatabaseKeys.FIELD_AUTHOR_USER_TYPE, feedback.getAuthorUserType());
                values.put(DatabaseKeys.FIELD_MESSAGE, feedback.getMessage());

                database.insert(DatabaseKeys.TABLE_FEEDBACK, null, values);
                values.clear();
                break;
            case DELIVERY_ISSUES:
                DeliveryIssue deliveryIssue = (DeliveryIssue)data;

                String reported = deliveryIssue.getDateReported() == null ? null :
                        dateFormat.format(deliveryIssue.getDateReported());

                values.put(DatabaseKeys.FIELD_ID, deliveryIssue.getId());
                values.put(DatabaseKeys.FIELD_ORDER_ID, deliveryIssue.getOrderId());
                values.put(DatabaseKeys.FIELD_DRIVER_ID, deliveryIssue.getDriverId());
                values.put(DatabaseKeys.FIELD_DATE_REPORTED, dateFormat.format(deliveryIssue.getDateReported()));
                values.put(DatabaseKeys.FIELD_ISSUE, deliveryIssue.getIssue());

                database.insert(DatabaseKeys.TABLE_DELIVERY_ISSUES, null, values);
                values.clear();
                break;
        }
    }

    public Object select(Tables table, String where) {
        SQLiteDatabase database = this.getWritableDatabase();

        String query = "select * from ";
        Cursor cursor;

        switch (table) {
            case SERVICES:
                query += DatabaseKeys.TABLE_SERVICES;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Services(cursor);
            case USERS:
                query += DatabaseKeys.TABLE_USERS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);

                return new User(cursor);
            case CONSUMERS:
                query += DatabaseKeys.TABLE_CONSUMERS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Consumer(cursor);
            case DRIVERS:
                query += DatabaseKeys.TABLE_DRIVERS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Driver(cursor);
            case BANK_CARDS:
                query += DatabaseKeys.TABLE_BANK_CARDS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new BankCards(cursor);
            case LOCATIONS:
                query += DatabaseKeys.TABLE_LOCATIONS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Locations(cursor);
            case ORDERS:
                query += DatabaseKeys.TABLE_ORDERS;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Orders(cursor);
            case ORDER_SERVICES:
                query += DatabaseKeys.TABLE_ORDER_SERVICES;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new OrderServices(cursor);
            case FEEDBACK:
                query += DatabaseKeys.TABLE_FEEDBACK;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new Feedbacks(cursor);
            case DELIVERY_ISSUES:
                query += DatabaseKeys.TABLE_DELIVERY_ISSUES;

                if(where != null) {
                    query += " " + where;
                }

                query += ";";

                cursor = database.rawQuery(query, null);
                return new DeliveryIssues(cursor);
            default:
                    return null;
        }
    }

    public void update(Tables table, HashMap<String, Object> params, String[] whereFields, String[] whereValues) {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            for(String field : params.keySet()) {
                Object object = params.get(field);

                if(object instanceof Date) {
                    values.put(field, dateFormat.format(object));
                } else if(object instanceof String) {
                    values.put(field, (String)object);
                } else if(object instanceof Integer) {
                    values.put(field, (int)object);
                } else if(object instanceof Double) {
                    values.put(field, (double)object);
                } else if(object instanceof Character) {
                    values.put(field, new String(new char[] {(char)object}));
                } else if(object instanceof Boolean) {
                    values.put(field, (boolean)object);
                } else if(object instanceof Byte[]) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(object);
                    objectOutputStream.flush();
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    objectOutputStream.close();
                    values.put(field, bytes);
                }
            }

            for(int i = 0; i < whereFields.length; i++) {
                String whereStr = whereFields[i];
                whereStr += "=?";
                whereFields[i] = whereStr;
            }

            String where = TextUtils.join(", ", whereFields);

            switch (table) {
                case SERVICES:
                    database.update(DatabaseKeys.TABLE_SERVICES, values, where, whereValues);
                    values.clear();
                    break;
                case USERS:
                    database.update(DatabaseKeys.TABLE_USERS, values, where, whereValues);
                    values.clear();
                    break;
                case CONSUMERS:
                    database.update(DatabaseKeys.TABLE_CONSUMERS, values, where, whereValues);
                    values.clear();
                    break;
                case DRIVERS:
                    database.update(DatabaseKeys.TABLE_DRIVERS, values, where, whereValues);
                    values.clear();
                    break;
                case BANK_CARDS:
                    database.update(DatabaseKeys.TABLE_BANK_CARDS, values, where, whereValues);
                    values.clear();
                    break;
                case LOCATIONS:
                    database.update(DatabaseKeys.TABLE_LOCATIONS, values, where, whereValues);
                    values.clear();
                    break;
                case ORDERS:
                    database.update(DatabaseKeys.TABLE_ORDERS, values, where, whereValues);
                    values.clear();
                    break;
                case ORDER_SERVICES:
                    database.update(DatabaseKeys.TABLE_ORDER_SERVICES, values, where, whereValues);
                    values.clear();
                    break;
                case FEEDBACK:
                    database.update(DatabaseKeys.TABLE_FEEDBACK, values, where, whereValues);
                    values.clear();
                    break;
                case DELIVERY_ISSUES:
                    database.update(DatabaseKeys.TABLE_DELIVERY_ISSUES, values, where, whereValues);
                    values.clear();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(Tables table, String[] whereFields, String[] whereValues) {
        SQLiteDatabase database = this.getWritableDatabase();

        for(int i = 0; i < whereFields.length; i++) {
            String whereStr = whereFields[i];
            whereStr += "=?";
            whereFields[i] = whereStr;
        }

        String where = TextUtils.join(", ", whereFields);

        switch (table) {
            case SERVICES:
                database.delete(DatabaseKeys.TABLE_SERVICES, where, whereValues);
                break;
            case USERS:
                database.delete(DatabaseKeys.TABLE_USERS, where, whereValues);
                break;
            case CONSUMERS:
                database.delete(DatabaseKeys.TABLE_CONSUMERS, where, whereValues);
                break;
            case DRIVERS:
                database.delete(DatabaseKeys.TABLE_DRIVERS, where, whereValues);
                break;
            case BANK_CARDS:
                database.delete(DatabaseKeys.TABLE_BANK_CARDS, where, whereValues);
                break;
            case LOCATIONS:
                database.delete(DatabaseKeys.TABLE_LOCATIONS, where, whereValues);
                break;
            case ORDERS:
                database.delete(DatabaseKeys.TABLE_ORDERS, where, whereValues);
                break;
            case ORDER_SERVICES:
                database.delete(DatabaseKeys.TABLE_ORDER_SERVICES, where, whereValues);
                break;
            case FEEDBACK:
                database.delete(DatabaseKeys.TABLE_FEEDBACK, where, whereValues);
                break;
            case DELIVERY_ISSUES:
                database.delete(DatabaseKeys.TABLE_DELIVERY_ISSUES, where, whereValues);
                break;
        }
    }

    public void emptyTable(Tables table) {
        SQLiteDatabase database = this.getWritableDatabase();

        String dropQuery = "drop table ";
        String createQuery = "";

        switch (table) {
            case SERVICES:
                dropQuery += DatabaseKeys.TABLE_SERVICES + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_SERVICES + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_TYPE + " text, " +
                        DatabaseKeys.FIELD_ARABIC_TYPE + " text, " +
                        DatabaseKeys.FIELD_CYLINDER_SIZE + " text, " +
                        DatabaseKeys.FIELD_ARABIC_CYLINDER_SIZE + " text, " +
                        DatabaseKeys.FIELD_CHARGE + " float, " +
                        DatabaseKeys.FIELD_DATE_MODIFIED + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
            case USERS:
                dropQuery += DatabaseKeys.TABLE_USERS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_USERS + " ( " +
                        DatabaseKeys.FIELD_TOKEN + " text, " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_MOBILE_NO + " int, " +
                        DatabaseKeys.FIELD_EMAIL + " text, " +
                        DatabaseKeys.FIELD_ID_NO + " int, " +
                        DatabaseKeys.FIELD_F_NAME + " text, " +
                        DatabaseKeys.FIELD_L_NAME + " text, " +
                        DatabaseKeys.FIELD_DISPLAY_PIC_THUMB + " blob, " +
                        DatabaseKeys.FIELD_DISPLAY_PIC_URL + " text, " +
                        DatabaseKeys.FIELD_USER_TYPE + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + "), " +
                        "unique(" + DatabaseKeys.FIELD_MOBILE_NO + ") " +
                        ");";
                break;
            case CONSUMERS:
                dropQuery += DatabaseKeys.TABLE_CONSUMERS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_CONSUMERS + " ( " +
                        DatabaseKeys.FIELD_USER_ID + " text, " +
                        DatabaseKeys.FIELD_GENDER + " text, " +
                        DatabaseKeys.FIELD_DATE_OF_BIRTH + " text, " +
                        DatabaseKeys.FIELD_AGE + " int, " +
                        DatabaseKeys.FIELD_MAIN_LOCATION_ID + " text, " +
                        DatabaseKeys.FIELD_MAIN_LOCATION_NAME + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_USER_ID + ") " +
                        ");";
                break;
            case DRIVERS:
                dropQuery += DatabaseKeys.TABLE_DRIVERS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_DRIVERS + " ( " +
                        DatabaseKeys.FIELD_USER_ID + " text, " +
                        DatabaseKeys.FIELD_PLATE_CODE + " text, " +
                        DatabaseKeys.FIELD_PLATE_NUMBER + " int, " +
                        DatabaseKeys.FIELD_BANK_NAME + " text, " +
                        DatabaseKeys.FIELD_BANK_BRANCH + " text, " +
                        DatabaseKeys.FIELD_BANK_ACCOUNT_NAME + " text, " +
                        DatabaseKeys.FIELD_BANK_ACCOUNT_NO + " text, " +
                        DatabaseKeys.FIELD_ADDRESS_LINE_1 + " text, " +
                        DatabaseKeys.FIELD_ADDRESS_LINE_2 + " text, " +
                        DatabaseKeys.FIELD_CITY + " text, " +
                        DatabaseKeys.FIELD_PROVINCE + " text, " +
                        DatabaseKeys.FIELD_GOVERNORATE + " text, " +
                        DatabaseKeys.FIELD_COUNTRY + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_USER_ID + ") " +
                        ");";
                break;
            case BANK_CARDS:
                dropQuery += DatabaseKeys.TABLE_BANK_CARDS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_BANK_CARDS + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_OWNER + " text, " +
                        DatabaseKeys.FIELD_CARD_NO + " text, " +
                        DatabaseKeys.FIELD_EXP_DATE_MONTH + " int, " +
                        DatabaseKeys.FIELD_EXP_DATE_YEAR + " int, " +
                        DatabaseKeys.FIELD_CVV + " int, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
            case LOCATIONS:
                dropQuery += DatabaseKeys.TABLE_LOCATIONS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_LOCATIONS + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_LONGITUDE + " float, " +
                        DatabaseKeys.FIELD_LATITUDE + " float, " +
                        DatabaseKeys.FIELD_ADDRESS_LINE_1 + " text, " +
                        DatabaseKeys.FIELD_ADDRESS_LINE_2 + " text, " +
                        DatabaseKeys.FIELD_CITY + " text, " +
                        DatabaseKeys.FIELD_PROVINCE + " text, " +
                        DatabaseKeys.FIELD_GOVERNORATE + " text, " +
                        DatabaseKeys.FIELD_COUNTRY + " text, " +
                        DatabaseKeys.FIELD_LOCATION_NAME + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
            case ORDERS:
                dropQuery += DatabaseKeys.TABLE_ORDERS + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_ORDERS + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_CONSUMER_ID + " text, " +
                        DatabaseKeys.FIELD_DRIVER_ID + " text, " +
                        DatabaseKeys.FIELD_LOCATION_ID + " text, " +
                        DatabaseKeys.FIELD_ORDER_DATE + " text, " +
                        DatabaseKeys.FIELD_DELIVERY_OPTION_ID + " text, " +
                        DatabaseKeys.FIELD_CLIMB_STAIRS + " boolean, " +
                        DatabaseKeys.FIELD_TOTAL_COST + " float, " +
                        DatabaseKeys.FIELD_STATUS + " text, " +
                        DatabaseKeys.FIELD_ARABIC_STATUS + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
            case ORDER_SERVICES:
                dropQuery += DatabaseKeys.TABLE_ORDER_SERVICES + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_ORDER_SERVICES + " ( " +
                        DatabaseKeys.FIELD_ORDER_ID + " text, " +
                        DatabaseKeys.FIELD_SERVICE_ID + " text, " +
                        DatabaseKeys.FIELD_QUANTITY + " int, " +
                        "primary key(" + DatabaseKeys.FIELD_ORDER_ID + ", " + DatabaseKeys.FIELD_SERVICE_ID + ") " +
                        ");";
                break;
            case FEEDBACK:
                dropQuery += DatabaseKeys.TABLE_FEEDBACK + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_FEEDBACK + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_ORDER_ID + " text, " +
                        DatabaseKeys.FIELD_AUTHOR + " text, " +
                        DatabaseKeys.FIELD_AUTHOR_USER_TYPE + " text, " +
                        DatabaseKeys.FIELD_MESSAGE + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
            case DELIVERY_ISSUES:
                dropQuery += DatabaseKeys.TABLE_DELIVERY_ISSUES + ";";
                createQuery = "create table " + DatabaseKeys.TABLE_DELIVERY_ISSUES + " ( " +
                        DatabaseKeys.FIELD_ID + " text, " +
                        DatabaseKeys.FIELD_DRIVER_ID + " text, " +
                        DatabaseKeys.FIELD_ORDER_ID + " text, " +
                        DatabaseKeys.FIELD_DATE_REPORTED + " text, " +
                        DatabaseKeys.FIELD_ISSUE + " text, " +
                        "primary key(" + DatabaseKeys.FIELD_ID + ") " +
                        ");";
                break;
        }

        database.execSQL(dropQuery);
        database.execSQL(createQuery);
    }

    public void dropDatabase() {
        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        ArrayList<String> tables = new ArrayList<>();

        while(cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }

        cursor.close();

        for(String table : tables) {
            String drop = "DROP TABLE IF EXISTS " + table;
            database.execSQL(drop);
        }

        onCreate(database);
    }
}
