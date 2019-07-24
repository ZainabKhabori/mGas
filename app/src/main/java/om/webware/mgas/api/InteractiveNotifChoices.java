package om.webware.mgas.api;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class InteractiveNotifChoices {

    private ArrayList<InteractiveNotifChoice> choices;

    public InteractiveNotifChoices(String json) {
        InteractiveNotifChoice[] array = new Gson().fromJson(json, InteractiveNotifChoice[].class);
        choices = new ArrayList<>(Arrays.asList(array));
    }

    public ArrayList<InteractiveNotifChoice> getChoices() {
        return choices;
    }
}
