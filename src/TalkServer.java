import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TalkServer {
	
	Socket s;
	List<Solve> solves = new ArrayList<Solve>();
	
	public TalkServer(int port) {
		try {
			// 设置sever端的链接
			ServerSocket ss = new ServerSocket(port);
			while (true) {
				s = ss.accept();// sever等待链接
				Solve solve = new Solve(s);
				new Thread(solve).start();
				solves.add(solve);
			}

		}
		catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				s.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class Solve implements Runnable {
		private Socket s;
		private DataInputStream dis = null;
		private DataOutputStream dos = null;
		
		public Solve(Socket s) {
			this.s = s;
			try {
				dis = new DataInputStream(s.getInputStream());
				dos = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void send(String str) {
			try {
				System.out.println("server,send");
				dos.writeUTF(str);
			} catch (IOException e) {
				solves.remove(this);
				//System.out.println("对方退出了！我从List里面去掉了！");
				//e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
					while(true) {
						String str = dis.readUTF();
						System.out.println(str);
						for(int i=0; i<solves.size(); i++) {
							Solve c = solves.get(i);
							c.send(str);
						}
					}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(dis != null) dis.close();
					if(dos != null) dos.close();
					if(s != null)  {
						s.close();
						//s = null;
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		
	}
}
 

// 创建一个进程用来进行接收读取数据
//class MyServerReader extends Thread {
//	private DataInputStream dis;
//	private Thread msw;
//	private DataOutputStream dos;
//	public MyServerReader(DataInputStream dis, Thread msw, DataOutputStream dos) {
//		this.dis = dis;
//		this.msw = msw;
//		this.dos = dos;
//	}
//
//	public void run() {
//		String msg;
//		try {
//
//			msg = dis.readUTF();
//			//System.out.println("对方说:" + msg);
//			msw = new MyServerWriter(dos, msg);
//			msw.start();
//		} catch (IOException e) {
//			System.out.println(e);
//		}
//	}
//}
//
//// 创建一个进程用来写入并发送数据
//class MyServerWriter extends Thread {
//	private DataOutputStream dos;
//	private String msg;
//	public MyServerWriter(DataOutputStream dos, String msg) {
//		this.dos = dos;
//		this.msg = msg;
//	}
//
//	public void run() {
//		try {
//				dos.writeUTF(msg);
//				System.out.println("server:" + msg);
//		} catch (IOException e) {
//			System.out.println(e);
//		}
//	}
//}
