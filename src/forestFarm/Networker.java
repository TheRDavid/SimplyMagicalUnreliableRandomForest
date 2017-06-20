package forestFarm;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

	public class Networker implements Runnable
	{
	private ServerSocket server;
	public static final ForestSettings forestSettings = new ForestSettings(0.25f, 1, 3, 59*59);
	public static final int port = 4511;
	public static final String ip = "172.20.171.18";
	public static ArrayList<NetworkListener> networkListeners = new ArrayList<>();
	
	public Networker()
	{
		try
		{
			server = new ServerSocket(port);
			String addresses = "";
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) en.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if (!i.isLoopbackAddress() && i instanceof Inet4Address) {
			        	System.out.println(i.getHostAddress());
			        	if (!addresses.isEmpty()) addresses+= " or ";
			        	addresses += i.getHostAddress();
			        }
			    }
			}
			System.out.println("Server: Started @ "+addresses+" on port "+port);
			server.setSoTimeout(0);
			new Thread(this).start();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				new NetworkListener(server.accept());
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} 	
	
	public static void main(String[] args)
	{
		new Networker();
	}
}
