package forestFarm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import forest.DataSet;
import forest.Element;
import forest.RForest;
import forest.RTree;
import forest.Vector3;
import test.BasicTest.Emotions;

public class TreeSender {

	private static final String serializedEmotionFileName = "smurf.emotions";
	private static final int landmarkPoints = 59;

	public static void main(String[] args) {
		new TreeSender();
	}

	int i = 0;
	ArrayList<Element<Vector3>> elements = null;
	int treeCount = 0;

	public TreeSender() {
		File resDir = new File("res");
		if (!resDir.exists())
			resDir.mkdir();
		Socket socket = null;
		try {
			socket = new Socket(Networker.ip, Networker.port);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			System.exit(0);
		}

		File f = new File(serializedEmotionFileName);
		if (f.exists() && !f.isDirectory()) {
			elements = deserialize(f);
		} else {
			elements = serialize(f);
		}

		System.out.println("emotions loaded");

		// -----

		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("having " + cores + " cores, yey!");

		ConcurrentLinkedQueue<RTree<Vector3>> toSend = new ConcurrentLinkedQueue<>();
		for (i = 0; i < cores; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("Starting Thread");
					DataSet<Vector3> dataSet = new DataSet<Vector3>(Networker.forestSettings.getNumFeatures(),
							Networker.forestSettings.getFeatureSampleSize(), elements);
					System.out.println("Done with Dataset");
					RForest<Vector3> forest = new RForest<Vector3>(dataSet, Networker.forestSettings.getSubSampleSize(),
							Networker.forestSettings.getNumTrees(), RForest.DataMode.HURRY_UP_M8, false);
					System.out.println("Done with Forest init");

					try {
						while (true) {
							Thread.sleep(100);
							System.out.println("Thread " + i + " Building Tree #" + treeCount);
							RTree<Vector3> newTree = forest.growNextSingleTree();
							File newFile = new File("res//" + treeCount++ + ".rt");
							newTree.saveAs(newFile);
							toSend.add(newTree);
						}
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}).start();
		}

		while (true) {
			if (!toSend.isEmpty()) {
				try {
					OutputStream os = socket.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					System.out.println("Sending Tree");
					oos.writeObject(toSend.poll());
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Could not send");
					try {
						socket = new Socket(Networker.ip, Networker.port);
					} catch (UnknownHostException e1) {
						System.out.println("Could not reconnect");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("Could not reconnect");
					}
				}
			} else
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		}

	}

	private static ArrayList<Element<Vector3>> serialize(File f) {

		System.out.println("reading csv's and then serializing ...");

		ArrayList<Element<Vector3>> elements = new ArrayList<>();

		File resDir = new File("res");
		File[] allResFiles = resDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getAbsolutePath().endsWith(".csv");
			}
		});

		for (File resFile : allResFiles) {
			System.out.println("Reading " + resFile.getName());
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(resFile));

				String name = resFile.getName();
				name = name.substring(name.indexOf(".") + 1);
				name = name.substring(0, name.indexOf("."));

				int category = -1;
				for (Emotions ems : Emotions.values()) {
					if (ems.name().equalsIgnoreCase(name)) {
						category = ems.ordinal();
						break;
					}
				}

				if (category == -1) {
					System.out.println("Not correct emotion found, skipping");
					continue;
				}

				String line;
				while ((line = buffReader.readLine()) != null) {
					Vector3[] attribs = new Vector3[landmarkPoints * landmarkPoints];
					int attribIdx = 0;
					StringTokenizer tokenizer = new StringTokenizer(line, ";");
					String vec;
					while (tokenizer.hasMoreElements()) {
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

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		System.out.println(elements.size() + " Elements found");

		// serialize
		OutputStream file = null;
		OutputStream buffer = null;
		ObjectOutput output = null;

		try {
			file = new FileOutputStream(f);
			buffer = new BufferedOutputStream(file);
			output = new ObjectOutputStream(buffer);
			output.writeObject(elements);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				output.flush();
				output.close();
				buffer.close();
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// end

		return elements;
	}

	private static ArrayList<Element<Vector3>> deserialize(File f) {

		System.out.println("deserializing ...");

		ArrayList<Element<Vector3>> elements = null;

		try {
			InputStream file = new FileInputStream(f);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			elements = (ArrayList<Element<Vector3>>) input.readObject();
			file.close();
			buffer.close();
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return elements;
	}

}
