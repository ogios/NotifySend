import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Notify {
    static String OS;
    static SystemTray systemTray = null;
    static TrayIcon trayIcon = null;
    static String trayIconPath = Send.getResource("icon");
    static StringBuilder stringBuilder = new StringBuilder();

    static {
        OS = System.getProperty("os.name").toLowerCase();
        if (SystemTray.isSupported()){
            systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(trayIconPath);
            trayIcon = new TrayIcon(image,"MSG");
//            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("ToolTip?");
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void sendMSG(String title, String content, String iconPath){
        System.out.println("Title: "+title);
        System.out.println("Content: "+content);
        System.out.println("IconPath: "+iconPath);
        try {
            String[] cmd = null;
            if (OS.contains("linux")){
                cmd = linux_sendMSG(title, content, iconPath);
            } else if (OS.contains("windows")) {
                cmd = win_sendMSG(title, content, iconPath);
            }
            if (cmd != null && !cmd.equals("")){
                for (String i:cmd){
                    stringBuilder.append(i);
                }
                System.out.println("Executing...");
                System.out.println(stringBuilder.toString());
                Process a = Runtime.getRuntime().exec(cmd);
                byte[] tmp = new byte[1024];
                int read;
                read = a.getInputStream().read(tmp);
                if (read == -1){
                    System.out.println("null");
                } else {
                    System.out.println(new String(tmp,0,read));
                }
                stringBuilder.delete(0,stringBuilder.length());
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void sendErrorNotice(String error){
        try {
            Runtime.getRuntime().exec("notify-send ErrorOccured "+error);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] linux_sendMSG(String title, String content, String iconPath){

        List<String> stringList = new ArrayList<>();
        stringList.add("notify-send");
        if (!title.equals("") && title != null){
            stringList.add(title+" ");
        }
        if (!content.equals("") && content != null){
            stringList.add(content+" ");
        }
        if (!iconPath.equals("") && iconPath != null){
            stringList.add("-i");
            stringList.add(iconPath);
        }


        String[] strings = new String[stringList.size()];
        strings = stringList.toArray(strings);

        return strings;
//        return new String[]{"notify-send", title, content, "-i", iconPath};
//        return String.format("notify-send \"%s\" \"%s\" -i %s ", title, content, iconPath);
    }

    public static String[] win_sendMSG(String title, String content, String iconPath) throws AWTException {

        trayIcon.displayMessage(title, content, TrayIcon.MessageType.INFO);
        return new String[]{"cmd","/c","echo","shit"};
    }

    public static void main(String[] args) throws IOException, AWTException {
//        sendMSG("title", "content", "/home/moiiii/ideaProjects/NotiRecieve/res/icon/android_default.png");
//        String cmd = "notify-send \"This is a Test send\" \"Content said yes\" -i /home/moiiii/ideaProjects/NotiRecieve/target/classes/icon/com.tencent.mobileqq.png \n";
//        cmd.split(" ");
//        String[] cmd = new String[]{"notify-send", "This is a test", "Content said yes", "-i", "/home/moiiii/ideaProjects/NotiRecieve/target/classes/icon/com.tencent.mobileqq.png"};
//        Process a = Runtime.getRuntime().exec(cmd);
//        System.out.println(new BufferedReader(new InputStreamReader(a.getInputStream())).readLine());
//        System.out.printf(OS);
//        sendMSG("Test","test","");
    }
}
