package com.xdja.presenter_mainframe.autoupdate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;




@SuppressWarnings("ReturnOfNull")
public class UpdateSocket {
	private String ip = null;
	private int   port = 0;
	private Socket conn = null;
	public InputStream sin = null; // 网络输入流
	private OutputStream sou = null;// 网络输出流
	private boolean connected = false;

	private static final int TIMEOUT = 20000;
	private static final int TIMEDIFF = 5000;
	private static final int CALC = 0xff;

	public UpdateSocket(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}
	
	
	/**
	 * 连接网络，由于j2me在连网时不能放到一个线程中，所以单独起线程连网
	 */
	class Connecter implements Runnable {

		Connecter() {
		}

		public void run() {
			connectNetwork();
		}

		private void connectNetwork() {
			try {
			
				conn = new Socket(ip, port);
				conn.setSoTimeout(TIMEOUT);
				sin = conn.getInputStream();
				sou = conn.getOutputStream();
				connected = true;
				// debug infor
				System.out.println("connect ok：" );
			} catch (Exception e) {
				e.getMessage();
				connected = false;
				// debug infor
				System.out.println("connect error：" );
			}
		}
	}
	
	/**
	 * 连接服务器
	 * 
	 * @return 成功返回0 不成功返回-1 errmsg中包含错误信息
	 */
	public int connect() {
		connected = false;
		// Thread connThread = new Thread(new Connecter(), "conn_hread");
		Thread connThread = new Thread(new Connecter());
		connThread.start();
		long ctime = System.currentTimeMillis();
		while (!connected) {

			if (System.currentTimeMillis() - ctime > TIMEDIFF) {
				// connThread.interrupt();
				connThread = null;
				break;
			}
			if (!connThread.isAlive()) {
				break;
			}
		}
		if (connected) {
			return 0;
		} else {
			return -1;
		}
	}

	@SuppressWarnings("NumericCastThatLosesPrecision")
	public int sendData(String reqstr)
	{
		int dataLen = 0;
		byte []data = null;
    	try {
    		dataLen = reqstr.getBytes().length;
    		data = new byte[dataLen+2];
    		data[0] = (byte)(dataLen>>8&CALC);
    		data[1] = (byte)(dataLen&CALC);//两个字节数据长度
    		System.arraycopy(reqstr.getBytes(), 0, data, 2, dataLen);//数据内容
    		
			sou.write(data);
			sou.flush();
			return 0;
		} catch (Exception e) {
			e.getStackTrace();
			return -1;
		}
	}
	
	public String recvData()
	{
		try {
			byte []data = null;
			int dataLen = 0;
			int currentLen = 0;
			
			byte[] len = new byte[2];//头2个字节是数据长度+ 后面数据内容
	
			int headlen = sin.read(len);
			if (headlen == -1){
				return null;
			}
			while (headlen < 2)	{
				headlen += sin.read(len, headlen, 2-headlen);
			}
			dataLen =Bytes2ToInt(len);// (len[0]&0xff)<<8 + len[1]&0xff;
			data = new byte[dataLen];
			while (currentLen < dataLen) {
				currentLen += sin.read(data, currentLen, dataLen-currentLen);
			}
		
			return new String(data, "utf-8");
		} catch (IOException e) {
			e.getStackTrace();
			return null;
		}
	}
	
	public byte[] recvDataNolength(int dataLen)
	{
		try {
			byte []data = new byte[dataLen];

			int currentLen = 0;
			
		    while(currentLen < dataLen ){
			    currentLen += sin.read(data,currentLen,dataLen-currentLen);
		    }
			return data;
		} catch (IOException e) {
			e.getStackTrace();
			return null ;
		}
	}
	public static int Bytes2ToInt(byte[] b) {
		int mask = CALC;
		int temp = 0;
		int res = 0;
		for (int i = 0; i < 2; i++) {
			res <<= 8;
			temp = b[i] & mask;
			res |= temp;
		}
		return res;
	}
	
	 @SuppressWarnings("NumericCastThatLosesPrecision")
	 public static byte[] IntToBytes2(int num) {
		    byte[] b = new byte[2];
		    for (int i = 0; i < 2; i++) {
			    b[i] = (byte) (num >> 8 - i * 8 &CALC);
		    }
		    return b;
	    }
	public void close()
	{
		try {
			sin.close();
			sou.close();
			conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
