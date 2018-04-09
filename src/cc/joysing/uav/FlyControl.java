package cc.joysing.uav;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class FlyControl {
	private byte[] sendDataBuffer = new byte[34]; //���͸����˻�������
	private byte[] readDataBuffer = new byte[34]; //����������
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
		//������������
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
	
	public byte[] changeToByte(int x){ //����xΪʮ������      
		byte[] b=new byte[5];
		b[0]=(byte)(x>>8);//�߰�λ           
		b[1]=(byte)(x& 0xff);//�Ͱ�λ
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
		sendDataBuffer[3] = (byte)0x00; //��Ϊ��������Ϊʮ���ƣ�����Ҫ����λ����  
		sendDataBuffer[4] = (byte)0x00;
		sendDataBuffer[5] = (byte)decToHexH(1500);//�����
		sendDataBuffer[6] = (byte)decToHexL(1500);//�����
		sendDataBuffer[7] = (byte)decToHexH(1500);//�����
		sendDataBuffer[8] = (byte)decToHexL(1500);//�����
		sendDataBuffer[9] = (byte)decToHexH(1500);//������
		sendDataBuffer[10] = (byte)decToHexL(1500);//������
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
	/* ��������
	 * �������ã���д״̬0xC0���ã�
	 * int[0]��������
	 * int[1]��������
	 * int[2]�������
	 * int[3]��������
	 * ��������ÿ���޸���50�ı���������
	 *		��Сֵ	ֵ����		�м�ֵ	ֵ����		���ֵ
	 * ����	0		˳ʱ��ƫת		1500	��ʱ��ƫת		3000
	 * �����	0		����			1500	����			3000
	 * ������	0		���			1500	��ǰ			3000
	 * ���ţ�	0		�½�			��ֵͣ	����			1000
	 */

	//ת��Decimal TO Hex
	public byte[] decToHex(int dec){//����decΪʮ������      
		byte[] hex=new byte[2];
		hex[0]=(byte)(dec>>8);                            //�߰�λ           
		hex[1]=(byte)(dec&0xff);                         //�Ͱ�λ
		return hex;
	}
	//ת��Decimal TO Hex��λ����
	public static byte decToHexH(int dec){
		return (byte)(dec>>8);//dec/(MATH.pow(2,8))//
	}
	//ת��Decimal TO Hex��λ����
	public static byte decToHexL(int dec){
		return (byte)(dec&0xff);
	}
	
	//ת��Hex TO Decimal
	public static int hexToDec(byte high,byte low){
		return (high<<8)+low;
	}
}
