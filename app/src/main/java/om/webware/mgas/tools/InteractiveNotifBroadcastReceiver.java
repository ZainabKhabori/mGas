package om.webware.mgas.tools;

import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import om.webware.mgas.R;
import om.webware.mgas.api.InteractiveNotification;
import om.webware.mgas.api.User;
import om.webware.mgas.fragments.dialogs.ErrorDialogFragment;
import om.webware.mgas.server.MGasApi;
import om.webware.mgas.server.Server;

/**
 * Created by Zainab on 12/07/2019.
 */

public class InteractiveNotifBroadcastReceiver extends BroadcastReceiver {

    private int choiceIndex;
    private RemoteViews main;

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("SELECT_CHOICE_ACTION")) {
            selectChoiceAction(context, intent);
        } else {
            submitAction(context, intent);
        }
    }

    private void selectChoiceAction(Context context, Intent intent) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.responsive_notification_choice);
        views.setInt(R.id.constraintLayoutNotifChoice, "setBackgroundResource", R.drawable.round_edged_background_accent);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(intent.getIntExtra("CHOICE_ID", 0), views);

        choiceIndex = intent.getIntExtra("INDEX", 0);
    }

    private void submitAction(Context context, Intent intent) {
        String notificationJson = intent.getStringExtra("NOTIFICATION");
        InteractiveNotification notification = new Gson().fromJson(notificationJson, InteractiveNotification.class);

        DatabaseHelper helper = new DatabaseHelper(context);
        User user = (User)helper.select(DatabaseHelper.Tables.USERS, null);

        JsonObject json = new JsonObject();
        json.addProperty("choiceId", notification.getChoices().getChoices().get(choiceIndex).getId());

        main = new RemoteViews(context.getPackageName(), R.layout.responsive_notification);
        main.setViewVisibility(R.id.progressBarWait, View.VISIBLE);

        submitChoice(context, notification.getId(), user.getToken(), json.toString(), intent);
    }

    private void submitChoice(final Context context, final String notificationId, final String token,
                              final String body, final Intent intent) {
        String url = MGasApi.makeUrl(MGasApi.SUBMIT_CHOICE, notificationId);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("authorization", "Bearer " + token);

        Server.request(context, Request.Method.POST, url, headers, body,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.startsWith(MGasApi.IISNODE_ERROR)) {
                            submitChoice(context, notificationId, token, body, intent);
                        } else {
                            Toast.makeText(context, context.getString(R.string.choice_submitted), Toast.LENGTH_SHORT).show();
                            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.cancel(intent.getIntExtra("NOTIFICATION_ID", 0));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        main.setViewVisibility(R.id.progressBarWait, View.GONE);
                        String err = context.getString(R.string.unable_to_submit_choice_error);
                        ErrorDialogFragment fragment = ErrorDialogFragment.creteDialog(err);
                        fragment.show(((AppCompatActivity)context).getSupportFragmentManager(), "SEND_CHOICE_ERROR_DIALOG");

/*                        Log.v("SPLASH_CHOICE_ERR", error.networkResponse.statusCode + "");
                        Log.v("SPLASH_CHOICE_ERR", new String(error.networkResponse.data, StandardCharsets.UTF_8));*/
                    }
                });
    }
}
