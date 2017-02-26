package de.fachstudie.stressapp;

import android.app.Notification;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by Paul Kuznecov on 01.11.2016.
 */

public class NotificationRegistryService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("notification service", " created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)
                .toString();
        String title = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE)
                .toString();
        String app = sbn.getPackageName();

        Log.d("Notification App: ", app);
        Log.d("Notification Text: ", text);
        Log.d("Notification Title: ", title);

        Intent i = new Intent("de.fachstudie.stressapp.notification");
        i.putExtra("application", app);
        i.putExtra("title_length", title.length() + "");
        i.putExtra("content", text);
        i.putExtra("event", "NOTIFICATION");

        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        String app = sbn.getPackageName();
        CharSequence textSequence = sbn.getNotification().extras.getCharSequence(Notification
                .EXTRA_TEXT);
        String text = textSequence != null ? textSequence.toString() : "";
        CharSequence titleSequence = sbn.getNotification().extras.getCharSequence(Notification
                .EXTRA_TITLE);
        String title = titleSequence != null ? titleSequence.toString() : "";

        Log.d("Notification removed", app);

        Intent i = new Intent("de.fachstudie.stressapp.notification");
        i.putExtra("application", app);
        i.putExtra("title_length", title.length() + "");
        i.putExtra("content", text);
        i.putExtra("event", "NOTIFICATION_REMOVED");
        sendBroadcast(i);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d("listener connected", " ");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d("listener disconnected", " ");
    }
}