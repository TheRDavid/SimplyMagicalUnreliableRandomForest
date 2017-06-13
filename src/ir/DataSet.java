package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import test.BasicTest;

public class DataSet {

	private ArrayList<Element> elements;
	private HashMap<Integer, Integer> categories;
	private int features;
	private float featureSampleSlice;
	private int[] featureSubSet;

	public DataSet(int f, float fss, ArrayList<Element> data) {
		features = f;
		featureSampleSlice = fss;
		elements = new ArrayList<>(data); // copy, the list itself must not be accessible

		categories = generateCategoryMap(elements);
	}

	public static HashMap<Integer, Integer> generateCategoryMap(ArrayList<Element> elems) {

		HashMap<Integer, Integer> cats = new HashMap<>();
		for (Element e : elems)
			if (cats.containsKey(e.getCategory()))
				cats.put(e.getCategory(), cats.get(e.getCategory()) + 1);
			else
				cats.put(e.getCategory(), 1);

		return cats;
	}

	public DataSet[] generateSubsamples(int num, int elementsPerSample) {
		DataSet[] sets = new DataSet[num];

		for (int i = 0; i < num; i++) {
			sets[i] = randomSub(elementsPerSample);
		}

		return sets;
	}

	private DataSet randomSub(int size) {
		ArrayList<Element> randomData = new ArrayList<>();
		while (size > 0) {
			randomData
					.add(this.elements.get((int) (Math.random() * this.elements.size())));
			size--;
		}
		return new DataSet(features, featureSampleSlice, randomData);
	}

	public SplitResult[] calcAllSplits() {
		SplitResult[] results = new SplitResult[featureSubSet.length * elements.size()];
		int resIdx = 0;
		for (int featureSubSetIndex = 0; featureSubSetIndex < featureSubSet.length; featureSubSetIndex++) {
			int feature = featureSubSet[featureSubSetIndex]; // at which feature do we split?
			for (Element e : elements) { // at which value do we split?
				SplitPoint splitPoint = new SplitPoint(feature, e);
				DataSet[] sets = split(splitPoint);
				results[resIdx++] = new SplitResult(splitPoint, sets[0], sets[1],
						impurity(feature, e));
			}
		}

		Arrays.sort(results, new Comparator<SplitResult>() {
			@Override
			public int compare(SplitResult arg0, SplitResult arg1) {
				// TODO Auto-generated method stub
				if (arg0.getImpurity() < arg1.getImpurity())
					return -1;
				if (arg0.getImpurity() > arg1.getImpurity())
					return 1;
				return 0;
			}
		});
		return results;
	}

	public SplitResult findBestSplit() {
	//	System.out.println("Finding SplitPoints...");
		featureSubSet = generateRandomUniqueFeatures();
		SplitPoint bestSplit = null;
		double smallestImpurity = Double.MAX_VALUE;
		for (int featureSubSetIndex = 0; featureSubSetIndex < featureSubSet.length; featureSubSetIndex++) {
			int feature = featureSubSet[featureSubSetIndex]; // at which feature do we split?
			//System.out.println("Best until ["+feature+"] = "+smallestImpurity);
			for (Element e : elements) { // at which value do we split?
				double newImpurity = impurity(feature, e);
				if (newImpurity <= smallestImpurity) {
					smallestImpurity = newImpurity;
					bestSplit = new SplitPoint(feature, e);
				}
			}
		}

		DataSet[] sets = split(bestSplit);

		return new SplitResult(bestSplit, sets[0], sets[1], smallestImpurity);
	}

	private DataSet[] split(SplitPoint point) {
		DataSet[] ret = new DataSet[2]; // left and right set

		ArrayList<Element> leftElements = new ArrayList<>(), rightElements = new ArrayList<>();

		for (Element e : elements)
			if (e.getAttribute(point.getFeatureIndex()) < point.getSplitValue())
				leftElements.add(e);
			else
				rightElements.add(e);

		//System.out.println("On the left: " + leftElements.size());
		//System.out.println("On the right: " + rightElements.size());

		ret[0] = new DataSet(features, featureSampleSlice, leftElements);
		ret[1] = new DataSet(features, featureSampleSlice, rightElements);

		return ret;
	}

	private int[] generateRandomUniqueFeatures() {
		int numberFeatures = (int) (featureSampleSlice * features);
		Set<Integer> featureSet = new HashSet<>();
		int[] returnFeatures = new int[numberFeatures];
		while (featureSet.size() < numberFeatures) {
			featureSet.add((int) (Math.random() * features));
		}
		int idx = 0;
		for (int i : featureSet) {
			returnFeatures[idx++] = i;
		}
		return returnFeatures;
	}

	private double impurity(int feature, Element e) {
		double value = e.getAttribute(feature);

		// perform the actual split
		ArrayList<Element> leftElements = new ArrayList<>(), rightElements = new ArrayList<>();
		for (Element el : elements)
			if (el.getAttribute(feature) < value)
				leftElements.add(el);
			else
				rightElements.add(el);
		/*	System.out.println("Calculating IMPURITY for feature "+feature+" @ "+value);
			System.out.println("Left:");
			BasicTest.print(leftElements);
			System.out.println("Right:");
			BasicTest.print(rightElements);*/

		double impurity = 0;

		// for each category...
		for (Map.Entry<Integer, Integer> entry : categories.entrySet()) { // key = category, value = num occurences
			// calculate probability...
			double probability = (double) entry.getValue() / elements.size();
			//System.out.println("Probablity of Category "+entry.getKey()+": "+probability);
			// and error on the left...
			double leftError = 0;
			for (Element el : leftElements)
				if (el.getCategory() != entry.getKey())
					leftError++;

			// error is zero, if there is no element of that category in the left set
			if (leftError == leftElements.size())
				leftError = 0;

			double rightError = 0;
			for (Element el : rightElements)
				if (el.getCategory() != entry.getKey())
					rightError++;

			// error is zero, if there is no element of that category in the left set
			if (rightError == rightElements.size())
				rightError = 0;

			// error is weighed with probablity and added to the impurity
			impurity += (leftError + rightError) * probability;

		}

		//System.out.println("IMPURITY: " + impurity);

		return impurity;
	}

	public int size() {
		return elements.size();
	}

	public class SplitResult {
		private SplitPoint splitPoint;
		private DataSet leftSet, rightSet;
		private double impurity;

		public SplitResult(SplitPoint point, DataSet left, DataSet right, double impurity) {
			splitPoint = point;
			leftSet = left;
			rightSet = right;
			this.impurity = impurity;
		}

		public SplitPoint getSplitPoint() {
			return splitPoint;
		}

		public DataSet getLeftSet() {
			return leftSet;
		}

		public DataSet getRightSet() {
			return rightSet;
		}

		public double getImpurity() {
			return impurity;
		}
	}

	public int categoryCount() {
		return categories.size();
	}

	public int featureCount() {
		return features;
	}

	public float getFeatureSampleSlice() {
		return featureSampleSlice;
	}

	public ArrayList<Element> getElements() {
		return elements;
	}
}
