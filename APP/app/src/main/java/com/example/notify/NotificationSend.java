package com.example.notify;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationSend implements Runnable{
    static Socket client;
    static boolean isOporating=true;
    MainActivity mainActivity;

    static List<String> iconSended = new ArrayList<>();
//    static List<Notification_item> notiList = new ArrayList<Notification_item>();


    NotificationSend(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }


//    public void recv(DataInputStream in, boolean isTimeout){
//        try {
//            byte[] tmp = new byte[1024];
//            StringBuilder stringBuilder = new StringBuilder();
//            int read;
//            while (true){
//                read=in.read(tmp);
//                if (read == -1){ break; }
//                stringBuilder.append(new String(tmp, 0, read, StandardCharsets.UTF_8));
//            }
//            iconSended = Arrays.asList(stringBuilder.toString().split(","));
//        }catch (Exception e){
//            e.printStackTrace();
//            isOporating = false;
//        }
//
//
//    }

    public void recv(){
        StringBuilder stringBuilder = new StringBuilder();
        byte[] tmp = new byte[1024];
        int read;
        try {
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            client.setSoTimeout(500);
            while (true) {
                read = dataInputStream.read(tmp);
                if (read == -1){
                    break;
                }
                client.setSoTimeout(100);
                stringBuilder.append(new String(tmp,0, read));
            }
        } catch (Exception e){
            if (stringBuilder.length()<=0){
                e.printStackTrace();
                return;
            }
        }

        String[] ls = stringBuilder.toString().split(",");
        Collections.addAll(iconSended,ls);
    }

    public static void send(Notification_item notification_item){
        boolean sendIcon;
        JSONObject data = new JSONObject();
//        Map<String,String> data = new HashMap<>();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            data.put("title", notification_item.getTitle());
            data.put("content", notification_item.getContent());
            data.put("packageName", notification_item.getApp());
            if (!iconSended.contains(notification_item.getApp())){
                data.put("sendIcon", "true");
                sendIcon = true;
            } else {
                data.put("sendIcon", "false");
                sendIcon = false;
            }
            String jsonObject = data.toString();
            dataOutputStream.write(jsonObject.getBytes());
            dataOutputStream.flush();
            if (!waitFor()){
                return;
            }
            if (sendIcon){
                Bitmap bitmap = notification_item.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, dataOutputStream);
                dataOutputStream.flush();
//            File f = new File(Send.getResource("icon_test")+"/test.png");
//            if (f.exists()){
//                FileInputStream fileInputStream = new FileInputStream(f);
//                byte[] tmp = new byte[1024];
//                int read;
//                while (true) {
//                    read = fileInputStream.read(tmp);
//                    if (read == -1){
//                        break;
//                    }
//                    System.out.println(tmp[2]);
//                    dataOutputStream.write(tmp, 0, read);
//                }
//                dataOutputStream.flush();
//            } else {
//                System.out.println("文件不存在");
//            }
            }
//            iconSended.add(notification_item.getApp());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return;
        }
    }

    // Wait for Server Sends back to say that it's recieved
    public static boolean waitFor(){
        try {
            client.setSoTimeout(3000);
            String t = new DataInputStream(new BufferedInputStream(client.getInputStream())).readLine();
            return t.equals("1");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }




    public void heartBeat() {
        while (true){
            System.out.println("Heartbeating...");
            try{
                if (NotificationSend.isOporating){
                    NotificationSend.client.sendUrgentData(0xFF);
                }
            } catch (Exception e){
                System.out.println("Connection break");
                e.printStackTrace();
                NotificationSend.isOporating = false;
                try {
                    NotificationSend.client.close();
                    NotificationSend.client = null;
                    NotificationSend.iconSended.clear();
                    mainActivity.socketConnect();
                    return;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        // Connect to ServerSocket
        while (isOporating){
            System.out.println("Connecting...");
            client = new Socket();
//            System.out.println(isOporating);
            try {
                client.connect(new InetSocketAddress(Supplies.IP, Supplies.PORT), 3000);
                break;
            } catch (ConnectException e) {
                System.out.println(e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(client.getInetAddress().getHostAddress());

        // Start heartbeat detection
        new Thread(new Runnable() {
            @Override
            public void run() {
                heartBeat();
            }
        }).start();

        // Get Icons that Server have
        recv();
        if (iconSended.size()>0){
            for (String i:iconSended){
                System.out.println(i);
            }
        } else {
            System.out.println("NoIconSended");
        }

    }
}
