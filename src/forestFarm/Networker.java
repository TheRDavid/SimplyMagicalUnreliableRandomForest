package forestFarm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Networker implements Runnable
{
	private ServerSocket server;
	public static final ForestSettings forestSettings = new ForestSettings(0.25f, 1, 3, 59*59);
	public static final int port = 2731;
	public static final String ip = "172.20.171.18";
	public static ArrayList<NetworkListener> networkListeners = new ArrayList<>();
	
	public Networker()
	{
		try
		{
			server = new ServerSocket(port);
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
