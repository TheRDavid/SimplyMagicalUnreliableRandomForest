package test;

import ir.DataSet;
import ir.Element;
import ir.RForest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	
	private static void emotionTest()
	{
		
	}
	
	private static void printData()
	{
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
		
		ArrayList<Element> elements = new ArrayList<>();
		File dataFile = new File("data.csv");
		try {
			List<String> data = Files.readAllLines(dataFile.toPath());
			for(String s : data)
			{
				String[] array = s.split(",");
				Element e = new Element(new double[]{Double.parseDouble(array[0]), Double.parseDouble(array[1])},Integer.parseInt(array[2]));
				elements.add(e);
			}

			DataSet dataSet = new DataSet(features, featureSampleSlice, elements);
			RForest forest = new RForest(dataSet, subSampleSize, numSubSamples);

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

	private static void staticTest0() {
		float subSampleSize = 0.5f;
		int numSubSamples = 4, features = 2;
		float featureSampleSlice = 1f;

		ArrayList<Element> data = new ArrayList<>();
		data.add(new Element(new double[] { 2, 2 }, 0));
		data.add(new Element(new double[] { 4, 1 }, 0));
		data.add(new Element(new double[] { 4, 3 }, 0));
		data.add(new Element(new double[] { 3, 7 }, 0));
		data.add(new Element(new double[] { 1, 4 }, 0));
		data.add(new Element(new double[] { 2, 3 }, 0));

		data.add(new Element(new double[] { -2, 1 }, 1));
		data.add(new Element(new double[] { -3, 3 }, 1));
		data.add(new Element(new double[] { -6, 2 }, 1));
		data.add(new Element(new double[] { -3, 2 }, 1));
		data.add(new Element(new double[] { -5, 6 }, 1));
		data.add(new Element(new double[] { -8, 3 }, 1));

		data.add(new Element(new double[] { -2, -3 }, 2));
		data.add(new Element(new double[] { -2, -6 }, 2));
		data.add(new Element(new double[] { -4, -6 }, 2));
		data.add(new Element(new double[] { -1, -6 }, 2));
		data.add(new Element(new double[] { -4, -8 }, 2));
		data.add(new Element(new double[] { -2, -9 }, 2));

		data.add(new Element(new double[] { 2, -4 }, 3));
		data.add(new Element(new double[] { 5, -3 }, 3));
		data.add(new Element(new double[] { 6, -2 }, 3));
		data.add(new Element(new double[] { 4, -2 }, 3));
		data.add(new Element(new double[] { 5, -1 }, 3));
		data.add(new Element(new double[] { 3, -8 }, 3));

		DataSet dataSet = new DataSet(features, featureSampleSlice, data);
		RForest forest = new RForest(dataSet, subSampleSize, numSubSamples);

		RForestUI ui = new RForestUI(forest);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private static void staticTest1() {
		float subSampleSize = 0.5f;
		int numSubSamples = 4, features = 2;
		float featureSampleSlice = 1f;

		ArrayList<Element> data = new ArrayList<>();
		data.add(new Element(new double[] { 2, 2 }, 0));
		data.add(new Element(new double[] { 4, 1 }, 0));
		data.add(new Element(new double[] { 4, 3 }, 0));
		data.add(new Element(new double[] { 3, 7 }, 0));
		data.add(new Element(new double[] { 1, 4 }, 0));
		data.add(new Element(new double[] { 2, 3 }, 0));

		data.add(new Element(new double[] { -2, 1 }, 1));
		data.add(new Element(new double[] { -3, 3 }, 1));
		data.add(new Element(new double[] { -6, 2 }, 1));
		data.add(new Element(new double[] { -3, 2 }, 1));
		data.add(new Element(new double[] { -5, 6 }, 1));
		data.add(new Element(new double[] { -8, 3 }, 1));

		data.add(new Element(new double[] { -2, -3 }, 2));
		data.add(new Element(new double[] { -2, -6 }, 2));
		data.add(new Element(new double[] { -4, -6 }, 2));
		data.add(new Element(new double[] { -1, -6 }, 2));
		data.add(new Element(new double[] { -4, -8 }, 2));
		data.add(new Element(new double[] { -2, -9 }, 2));

		data.add(new Element(new double[] { 2, -4 }, 3));
		data.add(new Element(new double[] { 5, -3 }, 3));
		data.add(new Element(new double[] { 6, -2 }, 3));
		data.add(new Element(new double[] { 4, -2 }, 3));
		data.add(new Element(new double[] { 5, -1 }, 3));
		data.add(new Element(new double[] { 3, -8 }, 3));

		DataSet dataSet = new DataSet(features, featureSampleSlice, data);
		RForest forest = new RForest(dataSet, subSampleSize, numSubSamples);

		RForestUI ui = new RForestUI(forest);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

		ArrayList<Element> data = new ArrayList<>();
		for (int i = 0; i < numElements; i++) {
			double[] attribs = new double[features];
			for (int j = 0; j < features; j++)
				attribs[j] = (Math.random() * (maxVal - minVal) + minVal);
			data.add(new Element(attribs, (int) (Math.random() * numCats)));
		}

		DataSet dataSet = new DataSet(features, featureSampleSlice, data);
		RForest forest = new RForest(dataSet, subSampleSize, numSubSamples);

		RForestUI ui = new RForestUI(forest);
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		d.dispose();
	}

	public static void print(ArrayList<Element> data) {
		for (Element e : data)
			System.out.println(e);
	}
}
