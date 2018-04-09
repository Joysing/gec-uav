package cc.joysing.uav;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FlyControl {
	private byte[] sendDataBuffer = new byte[34]; //发送给无人机的数据
	private byte[] readDataBuffer = new byte[34]; //读到的数据
	private Socket socket;
	FlyControl(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket=new Socket("192.168.4.1", 333);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void connect() {
		initData();
		//发起网络请求
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					socket=new Socket("192.168.4.1", 333);
					socket.getOutputStream().write("GEC\r\n".getBytes());
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public byte[] changeToByte(int x){ //参数x为十进制数      
		byte[] b=new byte[5];
		b[0]=(byte)(x>>8);//高八位           
		b[1]=(byte)(x& 0xff);//低八位
		return b;
	}
	public void changeAcceleration(int acceleration){
		byte[] hex=new byte[2];
		hex=decToHex(acceleration);
		sendDataBuffer[3]=hex[0];
		sendDataBuffer[4]=hex[1];
	}
	public void changeDirection(String direction,int directionValue){
		if("roll".equals(direction)) {
				sendDataBuffer[7]=(byte)decToHexH(1500);
				sendDataBuffer[8]=(byte)decToHexL(1500);
				sendDataBuffer[9]=(byte)decToHexH(directionValue);
				sendDataBuffer[10]=(byte)decToHexL(directionValue);
		}
		else if("pitch".equals(direction)) {
				sendDataBuffer[7]=(byte)decToHexH(directionValue);
				sendDataBuffer[8]=(byte)decToHexL(directionValue);
				sendDataBuffer[9]=(byte)decToHexH(1500);
				sendDataBuffer[10]=(byte)decToHexH(1500);
		}
		else if("onFinish".equals(direction)) {
				sendDataBuffer[7]=(byte)decToHexH(1500);
				sendDataBuffer[8]=(byte)decToHexH(1500);
				sendDataBuffer[9]=(byte)decToHexH(1500);
				sendDataBuffer[10]=(byte)decToHexH(1500);
		}	
	}
	public byte[] getAcceleration(){
		return sendDataBuffer;
	}
	public byte[] initData(){
		sendDataBuffer[0] = (byte) 0xAA;
		sendDataBuffer[1] = (byte) 0xC0;
		sendDataBuffer[2] = (byte) 0x1C;
		sendDataBuffer[3] = (byte)0x00; //因为传的数据为十进制，所以要进行位操作  
		sendDataBuffer[4] = (byte)0x00;
		sendDataBuffer[5] = (byte)decToHexH(1500);//航向高
		sendDataBuffer[6] = (byte)decToHexL(1500);//航向低
		sendDataBuffer[7] = (byte)decToHexH(1500);//横滚高
		sendDataBuffer[8] = (byte)decToHexL(1500);//横滚低
		sendDataBuffer[9] = (byte)decToHexH(1500);//俯仰高
		sendDataBuffer[10] = (byte)decToHexL(1500);//俯仰低
//		sendDataBuffer[5] = (byte)0x00;
//		sendDataBuffer[6] = (byte)0x00;
//		sendDataBuffer[7] = (byte)0x00;
//		sendDataBuffer[8] = (byte)0x00;
//		sendDataBuffer[9] = (byte)0x00;
//		sendDataBuffer[10] = (byte)0x00;

		sendDataBuffer[11] = (byte)0x00;
		sendDataBuffer[12] = (byte)0x00;
		sendDataBuffer[13] = (byte)0x00;
		sendDataBuffer[14] = (byte)0x00;
		sendDataBuffer[15] = (byte)0x00;
		sendDataBuffer[16] = (byte)0x00;
		sendDataBuffer[17] = (byte)0x00;
		sendDataBuffer[18] = (byte)0x00;
		sendDataBuffer[19] = (byte)0x00;
		sendDataBuffer[20] = (byte)0x00;
		sendDataBuffer[21] = (byte)0x00;
		sendDataBuffer[22] = (byte)0x00;
		sendDataBuffer[23] = (byte)0x00;
		sendDataBuffer[24] = (byte)0x00;
		sendDataBuffer[25] = (byte)0x00;
		sendDataBuffer[26] = (byte)0x00;
		sendDataBuffer[27] = (byte)0x00;
		sendDataBuffer[28] = (byte)0x00;
		sendDataBuffer[29] = (byte)0x00;
		sendDataBuffer[30] = (byte)0x00;
		sendDataBuffer[31] = (byte) 0x1C;
		sendDataBuffer[32] = (byte) 0x0D;
		sendDataBuffer[33] = (byte) 0x0A;
		return sendDataBuffer;
	}
	/* 控制数据
	 * 控制设置：（写状态0xC0设置）
	 * int[0]油门数据
	 * int[1]航向数据
	 * int[2]横滚数据
	 * int[3]俯仰数据
	 * 以上数据每次修改以50的倍数来增减
	 *		最小值	值含义		中间值	值含义		最大值
	 * 航向：	0		顺时针偏转		1500	逆时针偏转		3000
	 * 横滚：	0		向右			1500	向左			3000
	 * 俯仰：	0		向后			1500	向前			3000
	 * 油门：	0		下降			悬停值	上升			1000
	 */

	//转换Decimal TO Hex
	public byte[] decToHex(int dec){//参数dec为十进制数      
		byte[] hex=new byte[2];
		hex[0]=(byte)(dec>>8);                            //高八位           
		hex[1]=(byte)(dec&0xff);                         //低八位
		return hex;
	}
	//转换Decimal TO Hex高位数据
	public static byte decToHexH(int dec){
		return (byte)(dec>>8);//dec/(MATH.pow(2,8))//
	}
	//转换Decimal TO Hex低位数据
	public static byte decToHexL(int dec){
		return (byte)(dec&0xff);
	}
	
	//转换Hex TO Decimal
	public static int hexToDec(byte high,byte low){
		return (high<<8)+low;
	}
}
