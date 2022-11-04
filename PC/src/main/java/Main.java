import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Pattern;

public class Main {

	static Socket client;
	static int PORT = 5568;

	public static void init() throws Exception{
		System.out.println("Start listening at "+PORT);
		ServerSocket serverSocket = new ServerSocket(PORT);
		Socket client = serverSocket.accept();
		Main.client= client;
		System.out.println("Recieved connection from "+client.getRemoteSocketAddress());
	}

	public static void main(String[] args) throws Exception{
		while (true){
			init();
			Send.sendAquiredIcon(client);
			Notify.sendMSG("NotifyListener Connected!", "", "");
			System.out.printf(Send.getResource("icon"));
			Recv recv = new Recv(client);
			recv.run();
			Notify.sendMSG("NotifyListener Offline!", "", "");
			System.out.println("Connection offline.\nRestarting...");
			Thread.sleep(200);
		}
	}

}
// 传输总类
class Send {

	static 	File iconDir;

	static {
		try {
			File f = new File(Send.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if (f.isDirectory()) {
				iconDir = new File(f.getPath() + "/icon");
				if (!iconDir.exists()) {
					iconDir.mkdirs();
				}
			} else {
				iconDir = new File(f.getParent() + "/icon");
				if (!iconDir.exists()) {
					iconDir.mkdirs();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	// 获取res文件夹路径
	public static String getResource(String key){
//		return new Send().getClass().getResource(key).getPath();
		return iconDir.getPath();
	}

	// 获取包名与发送总方法
	public static void sendAquiredIcon(Socket client) throws Exception {
		String filename = getFileNames();
		PrintWriter pw = new PrintWriter(client.getOutputStream());
		pw.write(filename.toString());
		pw.flush();
//		pw.close();
	}

	// 获取包名
	public static String getFileNames(){
		// 获取icon文件夹路径
		File dir = new File(getResource("icon"));
		StringBuilder stringBuffer = new StringBuilder();
		if (dir.isDirectory()){
			// 获取所有文件
			File[] fs = dir.listFiles();
			String filename;
			for (File f:fs){
				filename = f.getName();
				// 匹配文件后缀名.png
				if (!Pattern.matches(".*\\.png$", filename)) {
//					System.out.println(filename+"");
					continue;
				}
				if (FileUtils.sizeOf(f) <= 0){
					continue;
				}

				// 先倒过来文字匹配gnp.然后删除第一个，再倒回来
				String name = reverseString(filename);
				name = Pattern.compile("(^gnp\\.)").matcher(name).replaceFirst("");
				name = reverseString(name);
				stringBuffer.append(name+",");
			}
		} else {
			return "";
		}
		// 为空的话就是没有
		if (stringBuffer.toString().equals("")){
			System.out.println("ISNULL");
		} else {
			System.out.println(stringBuffer.toString());
		}
		return stringBuffer.toString();
	}

	// 倒转文字总方法
	public static String reverseString(String str){
		// 获取文件一半的长度，前后文字对调
		int length = (int) Math.floor(str.length()/2);
		char[] name = str.toCharArray();
		char tmp;
		for (int i=0; i<length; i++){
			tmp = name[i];
			name[i] = name[str.length()-1-i];
			name[str.length()-1-i] = tmp;
		}
		return String.valueOf(name);
	}
}