package om.webware.mgas.tools;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import om.webware.mgas.R;
import om.webware.mgas.api.InteractiveNotifChoice;
import om.webware.mgas.api.InteractiveNotification;

/**
 * Created by Zainab on 12/07/2019.
 */

public class ResponsiveNotification {

    private InteractiveNotification notification;
    private Context context;

    private boolean rtl;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private RemoteViews remoteViews;

    private int id;

    public ResponsiveNotification(InteractiveNotification notification, Context context) {
        this.notification = notification;
        this.context = context;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.responsive_notification);
        rtl = context.getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }

    public void createNotification() {
        if(notification.getImage() != null) {
            remoteViews.setImageViewUri(R.id.imageViewImage, Uri.parse(notification.getImage()));
        } else {
            remoteViews.setViewVisibility(R.id.imageViewImage, View.GONE);
        }

        if(rtl) {
            remoteViews.setTextViewText(R.id.textViewTitle, notification.getArabicTitle());
            remoteViews.setTextViewText(R.id.textViewText, notification.getArabicText());
        } else {
            remoteViews.setTextViewText(R.id.textViewTitle, notification.getTitle());
            remoteViews.setTextViewText(R.id.textViewText, notification.getText());
        }

        int x = 100;
        for(InteractiveNotifChoice choice : notification.getChoices().getChoices()) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.responsive_notification_choice);

            if(choice.getImage() != null) {
                views.setImageViewUri(R.id.imageViewChoiceImage, Uri.parse(choice.getImage()));
            } else {
                views.setViewVisibility(R.id.imageViewChoiceImage, View.GONE);
            }

            if(rtl) {
                views.setTextViewText(R.id.textViewChoiceTitle, choice.getArabicTitle());
            } else {
                views.setTextViewText(R.id.textViewChoiceTitle, choice.getTitle());
            }

            Intent selectChoiceAction = new Intent("SELECT_CHOICE_ACTION");
            selectChoiceAction.putExtra("INDEX", x - 100);
            selectChoiceAction.putExtra("CHOICE_ID", views.getLayoutId());
            PendingIntent pendingSelectChoiceAction = PendingIntent.getBroadcast(context, x, selectChoiceAction, 0);
            views.setOnClickPendingIntent(R.id.constraintLayoutNotifChoice, pendingSelectChoiceAction);

            remoteViews.addView(R.id.linearLayoutChoices, views);
            x++;
        }

        id = (int)System.currentTimeMillis();
        Intent submitAction = new Intent("SUBMIT_ACTION");
        submitAction.putExtra("NOTIFICATION_ID", id);
        submitAction.putExtra("NOTIFICATION", new Gson().toJson(notification));

        PendingIntent pendingSubmitAction = PendingIntent.getBroadcast(context, 1, submitAction, 0);
        remoteViews.setOnClickPendingIntent(R.id.buttonSubmit, pendingSubmitAction);
    }

    public void showNotification(NotificationCompat.Builder builder) {
        builder.setCustomBigContentView(remoteViews);
        manager.notify(id, builder.build());
    }
}
