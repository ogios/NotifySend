package com.example.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class NotificationListener extends NotificationListenerService {
    String TAG = "NotificationListener";

    static List<Notification_item> notificationItems = new ArrayList<>();


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        int id = sbn.getId();
        String title = extras.getString(Notification.EXTRA_TITLE, null);
        String content = extras.getString(Notification.EXTRA_TEXT, null);
        int smallIcon = extras.getInt(Notification.EXTRA_SMALL_ICON,R.mipmap.ic_launcher);
//        Bitmap bitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
//        Bitmap smallIcon =  extras.getParcelable(Notification.EXTRA_SMALL_ICON);
        String packageName = sbn.getPackageName();
        if (title != null && content != null){
            Notification_Adapter.getInstance().onPost(new Notification_item(id, title, content, packageName, smallIcon));
        }
        Log.d(TAG, "onNotificationPosted: !!!!!!!!!!!!!!!"+sbn.getPackageName());
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
//        Notification notification = sbn.getNotification();
//        Bundle extras = notification.extras;
//        int id = sbn.getId();
//        String title = extras.getString(Notification.EXTRA_TITLE, "No title");
//        String content = extras.getString(Notification.EXTRA_TEXT, "No content");
//        int smallIcon = extras.getInt(Notification.EXTRA_SMALL_ICON,R.mipmap.ic_launcher);
////        Bitmap bitmap = extras.getParcelable(Notification.EXTRA_LARGE_ICON);
//        String packageName = sbn.getPackageName();
//        Notification_Adapter.getInstance().onPost(new Notification_item(id, title, content, packageName, smallIcon));
//        Log.d(TAG, "onNotificationRemoved: !!!!!!!!!!!!!!!!");
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Notification_Adapter.getInstance().onConnected();
        Toast.makeText(this, "Listener已连接", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onListenerConnected: Connected!!!!!!!!!!!!!!!!!!");
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification.Builder builder = new Notification.Builder(this)
//                .setContentTitle("Notify已连接")
//                .setSmallIcon(R.drawable.ic_launcher_background);
//        Notification notification = builder.build();
//        notificationManager.notify(1, notification);

    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "onListenerDisconnected: Disconnected!!!!!!!!!!!!!!!");
    }
}
