package se.esss.litterbox.icecube.ioc.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import se.esss.litterbox.icecube.ioc.IceCubePeriodicPollIoc;

public abstract class IceCubeTcpIoc extends IceCubePeriodicPollIoc 
{
	ServerSocket serverSocket;
	Socket clientSocket;

	public IceCubeTcpIoc(String clientId, String mqttBrokerInfoFilePath, String inetAddress, int portNumber) throws Exception 
	{
		super(clientId, mqttBrokerInfoFilePath);
		InetAddress addr = InetAddress.getByName(inetAddress);
		setStatus("Waiting for client to accept...");
		serverSocket = new ServerSocket(portNumber, 20, addr);
		clientSocket = serverSocket.accept();
		setStatus("...Client accepted");
	}
	public void sendBytes(byte[] myByteArray)  throws Exception
	{
	    OutputStream out = clientSocket.getOutputStream(); 
	    DataOutputStream dos = new DataOutputStream(out);
	    dos.write(myByteArray, 0, myByteArray.length);
	}
	public byte[] receiveBytes(int len) throws Exception
	{
	    InputStream in = clientSocket.getInputStream();
	    DataInputStream dis = new DataInputStream(in);

	    byte[] data = new byte[len];
	    dis.readFully(data);
	    return data;
	}
}