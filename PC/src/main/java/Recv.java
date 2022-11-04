import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Recv{
    Socket client;
    boolean isClosed=false;
    DataInputStream in;




    Recv(Socket client){
        this.client = client;
    }

    public void recv(){
        try {
            String head;
            String content;
            this.in = new DataInputStream(new BufferedInputStream(this.client.getInputStream()));
            while (true){
                if (isClosed){ break; }

                head = headRecv(in);
                if (!head.equals("")){
                    System.out.println("Send yes");
                    sendYes();
                    contentResolve(head);
                }

            }
        } catch (IOException e){
            e.printStackTrace();
            isClosed = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 接收头文件(字符串)
    public String headRecv(DataInputStream in) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] tmp = new byte[1024];
        int read;
        client.setSoTimeout(3000);
        try {
            while (true){
                read = in.read(tmp);
                if (read == -1){
                    break;
                }
                client.setSoTimeout(100);
                stringBuilder.append(new String(tmp, 0, read, StandardCharsets.UTF_8));
            }
            return stringBuilder.toString();
        } catch (Exception e){
            String str;
            if (stringBuilder.isEmpty()){
                str = "";
            } else {
                str = stringBuilder.toString();
            }
            client.setSoTimeout(3000);
            return str;
        }
    }

    // 处理接受的字符串
    public void contentResolve(String string){
        try {
            Map<String, Object> map = jsonToMap(string);
            if (map != null){
                System.out.println(map.get("packageName")+""+map.get("sendIcon"));
                String title = (String) map.get("title");
                String content = (String) map.get("content");
                String packageName = (String) map.get("packageName");
                String iconPath;
                if (map.get("sendIcon").equals(true) || map.get("sendIcon").equals("true")){
                    DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                    iconPath = iconRecv(dataInputStream, packageName);
                } else {
                    if (!new File(Send.getResource("icon")+"/"+packageName+".png").exists()){
                        iconPath = Send.getResource("icon")+"/android_default.png";
                    } else {
                        iconPath = Send.getResource("icon")+"/"+packageName+".png";
                    }
                }
            System.out.println();
            Notify.sendMSG(title, content, iconPath);
            }
        } catch (Exception e){
            e.printStackTrace();
            Notify.sendErrorNotice(e.getMessage());
        }

    }

    // 通知图标接收
    public String iconRecv(DataInputStream in, String packageName) throws IOException {
        byte[] tmp = new byte[1024];
        int read;
        File f = new File(Send.getResource("icon")+"/"+packageName+".png");
        if (!f.exists()){
            f.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(f);

        client.setSoTimeout(3000);
        try{
            while (true){
                read = in.read(tmp);
                if (read == -1){
                    break;
                }
                client.setSoTimeout(1000);
                System.out.println("c: "+tmp[1]);
                fileOutputStream.write(tmp,0, read);
            }
        }catch (Exception e){
            fileOutputStream.close();
            e.printStackTrace();
            if (f.length()<=0){
                e.printStackTrace();
                return Send.getResource("icon")+"/android_default.png";
            }
        }

        fileOutputStream.close();
        return f.getAbsolutePath();
    }

    public void sendYes() throws IOException {
        PrintWriter pw = new PrintWriter(client.getOutputStream());
        pw.write("1"+"\r\n");
        pw.flush();
    }

    // 字符串转json对象
    public Map<String, Object> jsonToMap(String string){
        try{
            JSONObject jsonObject = JSON.parseObject(string);
            return jsonObject.getInnerMap();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }





    // 心跳方法
    public void heartBeat() {
        while (true){
            try{
                client.sendUrgentData(0xFF);
                Thread.sleep(3000);
            } catch (InterruptedException interruptedException){
                interruptedException.printStackTrace();
                isClosed = true;
                break;
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
                ioException.printStackTrace();
                isClosed = true;
                break;
            }

        }
    }

    // 心跳检测线程

    public void run() {
        // 心跳
        new Thread(new Runnable() {
            @Override
            public void run() {
                heartBeat();
            }
        }).start();

        // 接收
        recv();

    }


/*
    class SendHearbeat implements Runnable{
        Recv recv;

        SendHearbeat(Recv recv){
            this.recv = recv;
        }



        @Override
        public void run() {
            heartBeat();
        }
    }
*/
}
