package forestFarm;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class Networker implements Runnable
{
	private ServerSocket server;
	public static final ForestSettings forestSettings = new ForestSettings(0.10f, 1, 9999999, 59*59);
	public static final int port = 4511;
	public static final String ip = "194.95.194.172";
	public static ArrayList<NetworkListener> networkListeners = new ArrayList<>();

	public Networker()
	{
		try
		{
			server = new ServerSocket(port);
			String addresses = "";
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements())
			{
				NetworkInterface n = (NetworkInterface) en.nextElement();
				Enumeration ee = n.getInetAddresses();
				while (ee.hasMoreElements())
				{
					InetAddress i = (InetAddress) ee.nextElement();
					if (!i.isLoopbackAddress() && i instanceof Inet4Address)
					{
						System.out.println(i.getHostAddress());
						if (!addresses.isEmpty())
							addresses += " or ";
						addresses += i.getHostAddress();
					}
				}
			}
			System.out.println("Server: Started @ " + addresses + " on port " + port);
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
		while (true)
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
	
	private static String getIPAddresses() {
		String addresses = "";
		Enumeration en;
		try {
			en = NetworkInterface.getNetworkInterfaces();
			while(en.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) en.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements())
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        if (!i.isLoopbackAddress() && i instanceof Inet4Address) {
			        	if (!addresses.isEmpty()) addresses+= " or ";
			        	addresses += i.getHostAddress();
			        }
			    }
			}
			return addresses;
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return addresses;
		}
	}
}
