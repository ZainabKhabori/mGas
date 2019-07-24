package om.webware.mgas.tools;

import java.util.HashMap;

public class SavedObjects {
    private static HashMap<String, Object> savedObjects = new HashMap<>();

    public static HashMap<String, Object> getSavedObjects() {
        return savedObjects;
    }
}
