package om.webware.mgas.api;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class Lotteries {

    private ArrayList<Lottery> lotteries;

    public Lotteries(String json) {
        Lottery[] array = new Gson().fromJson(json, Lottery[].class);
        lotteries = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<Lottery> getLotteries() {
        return lotteries;
    }
}
