package om.webware.mgas.tools;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class NotificationsService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        String type = data.get("type");
        if(type.equals("interactive")) {
            Log.v("SPLASH_CHOICES", data.get("choices"));
        }
    }
}
