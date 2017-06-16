package server;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.StringTokenizer;

import forest.Element;
import forest.RForest;
import forest.Vector3;
import test.BasicTest.Emotions;

public class Server implements Runnable
{
	
	private int port;	
	private boolean isRunning;
	
	private String clientSentence;
	private ServerSocket welcomeSocket;
	
	private RForest forest;

	
	public Server(int port, RForest forest) {
		this.port = port;
		this.isRunning = false;
		this.forest = forest;
	}
	

	@Override
	public void run()
	{
		isRunning = true;
		try
		{
			welcomeSocket = new ServerSocket(port);
			
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
			
			while (isRunning)
			{
				System.out.println("Server: Waiting for next request ...");
				
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Server: Got connection ...");
				
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				
				clientSentence = inFromClient.readLine();
				if (clientSentence == null) continue;
				
				Element e = parseRequest(clientSentence);
				if (e == null) continue;
				 
				int category = forest.categorize(e);
				String emotion = "";
				for (Emotions ems : Emotions.values()) {
					 if (ems.ordinal() == category) {
						 emotion = ems.name().toLowerCase();
						 break;
					 }
				}
				
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				outToClient.writeBytes(emotion+"\n");
				
				System.out.println("Server: Forest says request is Emotion '"+emotion+"'");
			}
			
			welcomeSocket.close();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private Element parseRequest(String request) {
		try {
			int landmarkPoints = 59;
			
			Vector3[] attribs = new Vector3[landmarkPoints * landmarkPoints];
			int attribIdx = 0;
			StringTokenizer tokenizer = new StringTokenizer(request, ";");
			String vec;
			boolean isSimpleChecksumOkay = false;
			while (tokenizer.hasMoreElements()) {
				vec = tokenizer.nextToken();
				
				if (vec.equals("#")) {
					isSimpleChecksumOkay = true;
					break;
				}
				
				String[] vals = vec.split(",");
				attribs[attribIdx++] = new Vector3(Double.parseDouble(vals[0]),
						Double.parseDouble(vals[1]), Double.parseDouble(vals[2]));
				/*
				attribs[attribIdx++] = Double.parseDouble(vals[0]);
				attribs[attribIdx++] = Double.parseDouble(vals[1]);
				attribs[attribIdx++] = Double.parseDouble(vals[2]);*/
			}
			
			if (!isSimpleChecksumOkay) {
				System.err.println("Server: Request not complete!");
				throw new Exception();
			}
			return new Element<Vector3>(attribs); 
		} catch (Exception e) {
			System.err.println("Server: Parsing request failed: "+e.getMessage());
			return null;
		}
		
	}
	
	public void close() {
		isRunning = false;
	}

}