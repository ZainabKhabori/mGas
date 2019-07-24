package om.webware.mgas.api;

import android.database.Cursor;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Zainab on 5/17/2019.
 */

public class BankCards {

    private ArrayList<BankCard> bankCards;

    public BankCards(Cursor cursor) {
        bankCards = new ArrayList<>();

        while(cursor.moveToNext()) {
            String id = cursor.getString(0);
            String owner = cursor.getString(1);
            String cardNo = cursor.getString(2);
            int expDateMonth = cursor.getInt(3);
            int expDateYear = cursor.getInt(4);
            int cvv = cursor.getInt(5);

            BankCard bankCard = new BankCard(id, owner, cardNo, expDateMonth, expDateYear, cvv);
            bankCards.add(bankCard);
        }
    }

    public BankCards(String json) {
        BankCard[] array = new Gson().fromJson(json, BankCard[].class);
        bankCards = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<BankCard> getBankCards() {
        return bankCards;
    }
}
