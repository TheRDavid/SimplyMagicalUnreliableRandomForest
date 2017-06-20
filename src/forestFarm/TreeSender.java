package forestFarm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import forest.DataSet;
import forest.Element;
import forest.RForest;
import forest.RTree;
import forest.Vector3;
import test.BasicTest.Emotions;

public class TreeSender
{

	public static void main(String[] args)
	{
		new TreeSender();
	}

	public TreeSender()
	{
		int landmarkPoints = 59;

		File resDir = new File("res");
		File[] allResFiles = resDir.listFiles(new FileFilter()
		{

			@Override
			public boolean accept(File arg0)
			{
				return arg0.getAbsolutePath().endsWith(".csv");
			}
		});

		ArrayList<Element<Vector3>> elements = new ArrayList<>();

		for (File resFile : allResFiles)
		{
			try
			{
				BufferedReader buffReader = new BufferedReader(new FileReader(resFile));

				String name = resFile.getName();
				name = name.substring(name.indexOf(".") + 1);
				name = name.substring(0, name.indexOf("."));

				int category = -1;
				for (Emotions ems : Emotions.values())
				{
					if (ems.name().equalsIgnoreCase(name))
					{
						category = ems.ordinal();
						break;
					}
				}

				if (category == -1)
				{
					System.out.println("Not correct emotion found, skipping");
					continue;
				}

				String line;
				while ((line = buffReader.readLine()) != null)
				{
					Vector3[] attribs = new Vector3[landmarkPoints * landmarkPoints];
					int attribIdx = 0;
					StringTokenizer tokenizer = new StringTokenizer(line, ";");
					String vec;
					while (tokenizer.hasMoreElements())
					{
						vec = tokenizer.nextToken();
						String[] vals = vec.split(",");
						attribs[attribIdx++] = new Vector3(Double.parseDouble(vals[0]), Double.parseDouble(vals[1]),
								Double.parseDouble(vals[2]));
						/*
						 * attribs[attribIdx++] = Double.parseDouble(vals[0]);
						 * attribs[attribIdx++] = Double.parseDouble(vals[1]);
						 * attribs[attribIdx++] = Double.parseDouble(vals[2]);
						 */
					}
					elements.add(new Element<Vector3>(attribs, category));
				}

			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println(elements.size() + " Elements found");

		DataSet<Vector3> dataSet = new DataSet<Vector3>(Networker.forestSettings.getNumFeatures(),
				Networker.forestSettings.getFeatureSampleSize(), elements);
		System.out.println(dataSet.generateCategoryMap(elements).size());
		RForest<Vector3> forest = new RForest<Vector3>(dataSet, Networker.forestSettings.getSubSampleSize(),
				Networker.forestSettings.getNumTrees(), RForest.DataMode.HURRY_UP_M8);
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Socket socket;
				try
				{
					socket = new Socket(Networker.ip, Networker.port);
					int currentTree = 0;
					while (currentTree < Networker.forestSettings.getNumTrees())
					{
						if (forest.getTrees().size() > currentTree)
						{
							RTree<Vector3> newestTree = forest.getTrees().get(currentTree);
							File newFile = new File(currentTree++ + ".rt");
							newestTree.saveAs(newFile);
							System.out.println("Save Tree @"+newFile.getAbsolutePath());
							OutputStream os;
							try
							{
								os = socket.getOutputStream();
								ObjectOutputStream oos = new ObjectOutputStream(os);
								oos.writeObject(newestTree);
								os.flush();
								os.close();
							} catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (UnknownHostException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}).start();
		forest.grow();
	}

}
