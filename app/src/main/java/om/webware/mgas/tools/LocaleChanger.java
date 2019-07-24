package om.webware.mgas.tools;

import android.content.Context;

public class LocaleChanger {

    private Context context;
    private String languageCode;

    public LocaleChanger(Context context, String languageCode) {
        this.context = context;
        this.languageCode = languageCode;
    }
}
