package om.webware.mgas.tools;

import android.app.Application;
import android.content.Context;

public class MGasApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleChanger.setLocale(base));
    }
}
