package forestFarm;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class NetworkListener implements Runnable
{

	private Socket client = null;
	private File dir;
	private int treeNum = 0;

	public NetworkListener(Socket client)
	{
		dir = new File(client.getInetAddress().getHostAddress());
		dir.mkdir();
		this.client = client;
		System.out.println("Connected to " + client.getInetAddress().getHostAddress());
		new Thread(this).start();
	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

					FileOutputStream fileOutputStream = new FileOutputStream(
							dir.getAbsolutePath() + "\\" + treeNum + ".rt");

					ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
					System.out.println("tree " + treeNum++ + " from " + client.getInetAddress().getHostName());
					oos.writeObject(ois.readObject());
					oos.flush();
					oos.close();
					fileOutputStream.close();
			} catch (SocketException e)
			{
				// TODO Auto-generated catch block
				System.out.println(client.getInetAddress().getHostName()+" disconnected");
				break;
			}catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
}
