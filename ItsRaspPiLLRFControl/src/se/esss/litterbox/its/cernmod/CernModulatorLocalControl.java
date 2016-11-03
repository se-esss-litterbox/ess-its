package se.esss.litterbox.its.cernmod;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import se.esss.litterbox.icetray.icecubedevice.FileToStringArray;
import se.esss.litterbox.icetray.icecubedevice.IceCubeDeviceList;
import se.esss.litterbox.simplemqttclient.SimpleMqttClient;

public class CernModulatorLocalControl  extends SimpleMqttClient
{
	private Socket clientSocket;
	private IceCubeDeviceList cernModulatorSettingList = null;
	private IceCubeDeviceList cernModulatorReadingList = null;

	public Socket getClientSocket() {return clientSocket;}
	public void setClientSocket(Socket clientSocket) {this.clientSocket = clientSocket;}
	
	public CernModulatorLocalControl(String brokerUrl, String brokerKey, String brokerSecret, URL cernmodSettingProtocolUrl, URL cernmodReadingProtocolUrl) throws Exception 
	{
		super(brokerUrl, brokerKey, brokerSecret);
		cernModulatorSettingList = new IceCubeDeviceList(FileToStringArray.fileToStringArray(cernmodSettingProtocolUrl));
		cernModulatorReadingList = new IceCubeDeviceList(FileToStringArray.fileToStringArray(cernmodReadingProtocolUrl));
	}
	public static void sendBytes(byte[] myByteArray, Socket socket) throws Exception 
	{
	    OutputStream out = socket.getOutputStream(); 
	    DataOutputStream dos = new DataOutputStream(out);
	    dos.write(myByteArray, 0, myByteArray.length);
	}
	public static byte[] receiveBytes(Socket socket, int len) throws Exception
	{
	    InputStream in = socket.getInputStream();
	    DataInputStream dis = new DataInputStream(in);

	    byte[] data = new byte[len];
	    dis.readFully(data);
	    return data;
	}
	@Override
	public void newMessage(String topic, MqttMessage mqttMessage) throws Exception 
	{
		System.out.println("    Message Recieved - Topic:" + topic + " at " + new Date().toString());
		if (topic.equals("its/cernmodulator/toModulator/set/forget"))
		{
			cernModulatorSettingList.putByteArray(mqttMessage.getPayload());
        	sendBytes(cernModulatorSettingList.getByteArray(), clientSocket);
    		System.out.println("    ...data sent");
            System.out.println("    Awaiting command...");
		}
		if (topic.equals("its/cernmodulator/toModulator/set/read"))
		{
			cernModulatorSettingList.putByteArray(mqttMessage.getPayload());
        	sendBytes(cernModulatorSettingList.getByteArray(), clientSocket);
    		System.out.println("    ...data sent");
    		System.out.println("    reading data...");
    		byte[] readData = receiveBytes(clientSocket, cernModulatorReadingList.numberOfBytes());
    		cernModulatorReadingList.putByteArray(readData);
			this.publishMessage("its", "cernmodulator/fromModulator/echo/read", "cernModulatorLocalControl", cernModulatorReadingList.getByteArray(), 0);
    		System.out.println("    ...data read.");
            System.out.println("    Awaiting command...");
		}
		if (topic.equals("its/cernmodulator/toModulator/echo/set"))
		{
			this.publishMessage("its", "cernmodulator/fromModulator/echo/set", "cernModulatorLocalControl", cernModulatorSettingList.getByteArray(), 0);
		}
		if (topic.equals("its/cernmodulator/toModulator/echo/read"))
		{
			this.publishMessage("its", "cernmodulator/fromModulator/echo/read", "cernModulatorLocalControl", cernModulatorReadingList.getByteArray(), 0);
		}

	}
	public static void main(String[] args) throws Exception 
	{
		int portNumber = 8000;
        InetAddress addr = InetAddress.getByName("192.168.5.4");
		System.out.println("Integration Test Stand CernModulatorLocalControl ver 1.0 David McGinnis 27-Oct-2016 14:41");
        System.out.println("Waiting for client to accept...");
        ServerSocket serverSocket = new ServerSocket(portNumber, 20, addr);
		Socket clientSocket = serverSocket.accept();
    	System.out.println("...Client accepted");
        System.out.println("Awaiting command...");
		URL cernmodSettingUrl = new URL("http://192.168.0.105:8080/IceCubeDeviceProtocols/protocols/CernModulatorProtocolSet.csv");
		URL cernmodReadingUrl = new URL("http://192.168.0.105:8080/IceCubeDeviceProtocols/protocols/CernModulatorProtocolRead.csv");
        CernModulatorLocalControl cernModulatorLocalControl = new CernModulatorLocalControl("tcp://broker.shiftr.io:1883", "c8ac7600", "1e45295ac35335a5", cernmodSettingUrl, cernmodReadingUrl);
        cernModulatorLocalControl.setEchoInfo(false);
        cernModulatorLocalControl.setClientSocket(clientSocket);
        cernModulatorLocalControl.subscribe("its", "cernmodulator/toModulator/#", "cernModulatorLocalControl", 0);
        
        serverSocket.close();
	}
}
