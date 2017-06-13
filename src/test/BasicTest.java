package test;

import ir.DataSet;
import ir.Element;
import ir.RForest;
import ir.Vector3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class BasicTest {
	public static void main(String[] args) {

		System.out.println("START");
		//printData();
		//fileTest();
		//randomTest();
		//staticTest0();
		//staticTest1();
		emotionTest();
	}

	private static void emotionTest() {
		int landmarkPoints = 59;

		File resDir = new File("res");
		File[] allResFiles = resDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.getAbsolutePath().endsWith(".csv");
			}
		});

		ArrayList<Element<Vector3>> elements = new ArrayList<>();

		for (File resFile : allResFiles) {
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(resFile));

				int category = 0;
				if (resFile.getName().contains("anger"))
					category = 0;
				else if (resFile.getName().contains("joy"))
					category = 1;
				else if (resFile.getName().contains("fear"))
					category = 2;
				else if (resFile.getName().contains("contempt"))
					category = 3;
				else if (resFile.getName().contains("sadness"))
					category = 4;
				else if (resFile.getName().contains("disgust"))
					category = 5;
				else if (resFile.getName().contains("surprise"))
					category = 6;
				String line;
				while ((line = buffReader.readLine()) != null) {
					Vector3[] attribs = new Vector3[landmarkPoints * landmarkPoints];
					int attribIdx = 0;
					StringTokenizer tokenizer = new StringTokenizer(line, ";");
					String vec;
					while (tokenizer.hasMoreElements()) {
						vec = tokenizer.nextToken();
						String[] vals = vec.split(",");
						attribs[attribIdx++] = new Vector3(Double.parseDouble(vals[0]),
								Double.parseDouble(vals[1]), Double.parseDouble(vals[2]));
						/*
						attribs[attribIdx++] = Double.parseDouble(vals[0]);
						attribs[attribIdx++] = Double.parseDouble(vals[1]);
						attribs[attribIdx++] = Double.parseDouble(vals[2]);*/
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
		System.out.println(elements.size());
		
		float subSampleSize = .1f;
		int numSubSamples = 1, features = 59 * 59;
		float featureSampleSlice = 1f;

		DataSet<Vector3> dataSet = new DataSet<Vector3>(features, featureSampleSlice,
				elements);
		System.out.println(dataSet.generateCategoryMap(elements).size());
		RForest<Vector3> forest = new RForest<Vector3>(dataSet, subSampleSize,
				numSubSamples, RForest.DataMode.SAVE_ALL_THE_DATA);

		RForestUI<Vector3> ui = new RForestUI<Vector3>(forest);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static void printData() {
		printFor(-14, -12, -4, -2, 3);
		printFor(-12, -2, -1, 0, 1);
		printFor(-3, -12, -1, 0, 1);
		printFor(-1, -2, 3, 5, 3);
		printFor(1, -12, 12, -4, 2);
		printFor(-12, 2, -5, 4, 0);
		printFor(-12, 5, -10, 12, 2);
		printFor(-9, 8, -6, 12, 1);
		printFor(4, 2, 8, 6, 1);
		printFor(9, 1, 13, 8, 2);
		printFor(2, 9, 13, 12, 0);
	}

	private static void fileTest() {

		float subSampleSize = 0.25f;
		int numSubSamples = 50, features = 2;
		float featureSampleSlice = 1f;

		ArrayList<Element<Double>> elements = new ArrayList<>();
		File dataFile = new File("data.csv");
		try {
			List<String> data = Files.readAllLines(dataFile.toPath());
			for (String s : data) {
				String[] array = s.split(",");
				Element<Double> e = new Element<Double>(new Double[] {
						Double.parseDouble(array[0]), Double.parseDouble(array[1]) },
						Integer.parseInt(array[2]));
				elements.add(e);
			}

			DataSet<Double> dataSet = new DataSet<Double>(features, featureSampleSlice,
					elements);
			RForest<Double> forest = new RForest<Double>(dataSet, subSampleSize,
					numSubSamples, RForest.DataMode.SAVE_ALL_THE_DATA);

			RForestUI ui = new RForestUI(forest);
			ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printFor(double x, double y, double x1, double y1, int c) {
		int num = 20;
		for (int i = 0; i < num; i++) {
			double newX = (x + (x1 - x) * Math.random());
			double newY = (y + (y1 - y) * Math.random());
			System.out.println(newX + "," + newY + "," + c);
		}
	}

	private static void randomTest() {
		float subSampleSize = 0.1f;
		int numSubSamples = 5, features = 30;
		float featureSampleSlice = 0.25f;

		double maxVal = 200, minVal = -200;
		int numCats = 5;
		int numElements = 10000;
		final JDialog d = new JDialog();
		new Thread(new Runnable() {

			@Override
			public void run() {
				d.add(new JLabel("Building Tree ..."));
				d.pack();
				d.setLocationRelativeTo(null);
				d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
				d.setModal(true);
				d.setVisible(true);
			}
		}).start();

		ArrayList<Element<Double>> data = new ArrayList<>();
		for (int i = 0; i < numElements; i++) {
			Double[] attribs = new Double[features];
			for (int j = 0; j < features; j++)
				attribs[j] = (Math.random() * (maxVal - minVal) + minVal);
			data.add(new Element<Double>(attribs, (int) (Math.random() * numCats)));
		}

		DataSet<Double> dataSet = new DataSet<Double>(features, featureSampleSlice, data);
		RForest<Double> forest = new RForest<Double>(dataSet, subSampleSize,
				numSubSamples, RForest.DataMode.SAVE_ALL_THE_DATA);

		RForestUI ui = new RForestUI(forest);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		d.dispose();
	}

	public static void print(ArrayList<Element> data) {
		for (Element e : data)
			System.out.println(e);
	}
}
