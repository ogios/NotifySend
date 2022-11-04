package com.example.notify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    int NOTIFY_PERMISSION = 70;

//    String ip="172.19.71.230";
//    int port =5568;

    Button NotificationPermissionBtn;
    Button SetIP;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        Notification_Adapter notification_adapter = new Notification_Adapter(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(notification_adapter);


        SetIP = findViewById(R.id.setip);
        NotificationPermissionBtn = findViewById(R.id.notificationPermission);
        NotificationPermissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotiEnabled()){
                    Log.d(TAG, "getNotiPermission: NotiPressiom Granted!!!!!!!!!!!");
                    showMSG("Notification Permission Granted");
                    setupService();
                    getNotiPermission();
                } else {
                    startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), 1);
                }
            }
        });
        setupService();
        Log.d(TAG, "onCreate: "+ isNotiEnabled());


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOTIFY_PERMISSION){
            if (isNotiEnabled()){
                Log.d(TAG, "onActivityResult:  NotiConnected!!!!!!!!!!!!!!");
//                Toast.makeText(this, "通知服务已开启", Toast.LENGTH_SHORT).show();
                setupService();
            } else {
                Log.d(TAG, "onActivityResult:  开启失败!!!!!!!!!!!!!!");
                Toast.makeText(this, "通知服务未开启", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void listenerConnected(){
        recyclerView.setBackgroundColor(Color.parseColor("#FFEB3B"));
        socketConnect();
    }

    public void socketConnect(){
        NotificationSend.isOporating = true;
        try {
            NotificationSend.client.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        new Thread(new NotificationSend(this)).start();
    }

    public void showMSG(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public Bitmap getBitmap(String pkgName, int src){
        Bitmap smallIcon;
        Context otherPkgContext;
        Drawable drawable;
        try {
//            otherPkgContext = this.createPackageContext("com.tencent.mm", 0);
//            drawable = otherPkgContext.getDrawable(src);
            drawable = getAppIcon(pkgName);
            if (drawable != null){
//                smallIcon = ((BitmapDrawable) drawable).getBitmap();
                smallIcon = getIconBitmap(drawable);
            } else {
                drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                smallIcon = ((BitmapDrawable) drawable).getBitmap();
            }
        } catch (Exception e){
            e.printStackTrace();
            drawable = getResources().getDrawable(R.mipmap.ic_launcher);
            smallIcon = ((BitmapDrawable) drawable).getBitmap();
        }
        return smallIcon;
    }

    @SuppressLint("RestrictedApi")
    public Drawable getAppIcon(String pkgName) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pkgName, pm.GET_UNINSTALLED_PACKAGES);
            return info.loadIcon(pm);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

//        .getDrawable(R.mipmap.ic_default, context.getTheme());
    }


    public static Bitmap getIconBitmap(Drawable drawable) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable instanceof AdaptiveIconDrawable) {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            } else {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        } catch (Exception e) {
            return null;
        }
    }


    public void getNotiPermission(){
        startActivityForResult(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), NOTIFY_PERMISSION);
    }

//    private void toggleNotificationListenerService() {
//        PackageManager pm = getPackageManager();
//        pm.setComponentEnabledSetting(new ComponentName(this, com.xinghui.notificationlistenerservicedemo.NotificationListenerServiceImpl.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
//        pm.setComponentEnabledSetting(new ComponentName(this, com.xinghui.notificationlistenerservicedemo.NotificationListenerServiceImpl.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//    }

    public void setupService(){
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationListener.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(getApplicationContext(), NotificationListener.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public boolean isNotiEnabled(){
        Set PackagesNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        return PackagesNames.contains(getPackageName());
    }
}