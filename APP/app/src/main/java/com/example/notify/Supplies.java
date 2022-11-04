package com.example.notify;

public class Supplies {

    public static String IP = "172.19.71.230";
    public static int PORT = 5568;



    static final public String QQ = "com.tencent.mobileqq";
    static final public String WECHAT = "com.tencent.mm";

    public static int getSrc(String packagename){
        switch (packagename){
            case QQ:
                return R.drawable.qq_logo;
            case WECHAT:
                return R.drawable.wechat_logo;
            default:
                return R.mipmap.ic_launcher;
        }
    }
}
