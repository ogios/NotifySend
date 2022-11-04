package com.example.notify;

import android.graphics.Bitmap;

public class Notification_item {
    private int id;
    private String title;
    private String content;
    private String app;
    private int src;
    private Bitmap bitmap;

    public Notification_item(int id, String title, String content, String app, int src) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.app = app;
//        this.src = Supplies.getSrc(app);
        this.src = src;
//        this.bitmap = bitmap;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }



}
