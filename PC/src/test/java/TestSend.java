import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

public class TestSend {

    public static String packageName="com.tencent.mm";
    public static String title="This is a Test send";
    public static String content="Content said yes";

    public static List<String> knownPackages = new ArrayList<>();

    public static String IP="127.0.0.1";
    public static int PORT=5568;

    public static Socket socket;

    public static void main(String[] args) throws IOException, InterruptedException {
        socket = new Socket();

        socket.connect(new InetSocketAddress(IP,PORT), 3000);
        recv();
        System.out.println("存在的packages: ");
        for (String i:knownPackages){
            System.out.println(i);
        }
        send();
//        send(dataOutputStream);
        Thread.sleep(20000000);
    }

    public static void send() throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        boolean sendIcon;
        Map<String,String> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("packageName", packageName);
        if (!knownPackages.contains(packageName)){
            data.put("sendIcon", "true");
            sendIcon = true;
        } else {
            data.put("sendIcon", "false");
            sendIcon = false;
        }
        String jsonObject = JSONObject.toJSONString(data);
        dataOutputStream.write(jsonObject.getBytes());
        dataOutputStream.flush();

        if (!waitFor()){
            return;
        }
        if (sendIcon){
            File f = new File(Send.getResource("icon_test")+"/test.png");
            if (f.exists()){
                FileInputStream fileInputStream = new FileInputStream(f);
                byte[] tmp = new byte[1024];
                int read;
                while (true) {
                    read = fileInputStream.read(tmp);
                    if (read == -1){
                        break;
                    }
                    System.out.println(tmp[2]);
                    dataOutputStream.write(tmp, 0, read);
                }
                dataOutputStream.flush();
            } else {
                System.out.println("文件不存在");
            }
        }
    }

    public static void recv() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        byte[] tmp = new byte[1024];
        int read;
        socket.setSoTimeout(3000);
        try {
            while (true) {
                read = dataInputStream.read(tmp);
                if (read == -1){
                    break;
                }
                socket.setSoTimeout(100);
                stringBuilder.append(new String(tmp,0, read));
            }
        } catch (Exception e){
            if (stringBuilder.isEmpty()){
                e.printStackTrace();
                return;
            }
        }

        String[] ls = stringBuilder.toString().split(",");
        Collections.addAll(knownPackages,ls);
    }

    public static boolean waitFor() throws IOException {
//        byte[] tmp = new byte[512];
        socket.setSoTimeout(30000);
        try {
            String t = new DataInputStream(new BufferedInputStream(socket.getInputStream())).readLine();
            return t.equals("1");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
