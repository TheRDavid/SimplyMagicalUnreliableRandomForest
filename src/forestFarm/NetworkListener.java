package forestFarm;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	int bytesRead = 0;

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(1000);

				int current = 0;
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				while (inFromClient.ready())
				{
					System.out.println("Listening for another tree...");
					byte[] byteArray = new byte[6022386];

					InputStream inputStream = client.getInputStream();
					FileOutputStream fileOutputStream = new FileOutputStream(
							dir.getAbsolutePath() + "\\" + treeNum++ + ".rt");
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					bytesRead = inputStream.read(byteArray, 0, byteArray.length);

					System.out.println("GOT ONE");

					current = bytesRead;
					do
					{
						bytesRead = inputStream.read(byteArray, current, (byteArray.length - current));
						if (bytesRead >= 0)
							current += bytesRead;
					} while (bytesRead > -1);
					bufferedOutputStream.write(byteArray, 0, current);
					System.out.println("Received Tree #" + treeNum);
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					inFromClient.close();
				}
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
