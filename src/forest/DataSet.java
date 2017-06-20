package forest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSet<T extends Comparable<T>> implements Serializable {

	private ArrayList<Element<T>> elements;
	private HashMap<Integer, Integer> categories;
	private int numFeatures, absoluteFeatureSubSampleSize;
	private float featureSampleSlice;
	private int[] featureSubSet;

	public DataSet(int f, float fss, ArrayList<Element<T>> data) {
		numFeatures = f;
		featureSampleSlice = fss;
		absoluteFeatureSubSampleSize = (int) (featureSampleSlice * numFeatures);
		elements = new ArrayList<Element<T>>(data); // copy, the list itself must not be accessible

		categories = generateCategoryMap(elements);
	}

	public HashMap<Integer, Integer> generateCategoryMap(ArrayList<Element<T>> elems) {

		HashMap<Integer, Integer> cats = new HashMap<>();
		for (Element<T> e : elems)
			if (cats.containsKey(e.getCategory()))
				cats.put(e.getCategory(), cats.get(e.getCategory()) + 1);
			else
				cats.put(e.getCategory(), 1);

		return cats;
	}

	public DataSet<T>[] generateSubsamples(int num, int elementsPerSample) {
		DataSet<T>[] sets = new DataSet[num];

		for (int i = 0; i < num; i++) {
			sets[i] = randomSub(elementsPerSample);
		}

		return sets;
	}

	private DataSet<T> randomSub(int size) {
		ArrayList<Element<T>> randomData = new ArrayList<>();
		while (size > 0) {
			randomData
					.add(this.elements.get((int) (Math.random() * this.elements.size())));
			size--;
		}
		return new DataSet<T>(numFeatures, featureSampleSlice, randomData);
	}

	public SplitResult<T>[] calcAllSplits() {
		SplitResult<T>[] results = new SplitResult[featureSubSet.length * elements.size()];
		int resIdx = 0;
		for (int featureSubSetIndex = 0; featureSubSetIndex < featureSubSet.length; featureSubSetIndex++) {
			int feature = featureSubSet[featureSubSetIndex]; // at which feature do we split?
			for (Element<T> e : elements) { // at which value do we split?
				SplitPoint<T> splitPoint = new SplitPoint<T>(feature, e);
				DataSet<T>[] sets = split(splitPoint);
				results[resIdx++] = new SplitResult<T>(splitPoint, sets[0], sets[1],
						impurity(feature, e));
			}
		}

		Arrays.sort(results);
		return results;
	}

	public SplitResult<T> findBestSplit() {
		//System.out.println("Finding SplitPoints...");
		featureSubSet = generateRandomUniqueFeatures();
		SplitPoint<T> bestSplit = null;
		double smallestImpurity = Double.MAX_VALUE;
		for (int featureSubSetIndex = 0; featureSubSetIndex < featureSubSet.length; featureSubSetIndex++) {
			int feature = featureSubSet[featureSubSetIndex]; // at which feature do we split?
			//System.out.println("Best until ["+feature+"] = "+smallestImpurity);
			for (Element<T> e : elements) { // at which value do we split?
				double newImpurity = impurity(feature, e);
				if (newImpurity <= smallestImpurity) {
					smallestImpurity = newImpurity;
					bestSplit = new SplitPoint<T>(feature, e);
				}
			}
		}

		DataSet<T>[] sets = split(bestSplit);

		return new SplitResult<T>(bestSplit, sets[0], sets[1], smallestImpurity);
	}

	private DataSet<T>[] split(SplitPoint<T> point) {
		DataSet<T>[] ret = new DataSet[2]; // left and right set

		ArrayList<Element<T>> leftElements = new ArrayList<>(), rightElements = new ArrayList<>();

		for (Element<T> e : elements)
			if (e.getAttribute(point.getFeatureIndex()).compareTo(point.getSplitValue()) < 0)
				leftElements.add(e);
			else
				rightElements.add(e);

		//System.out.println("On the left: " + leftElements.size());
		//System.out.println("On the right: " + rightElements.size());

		ret[0] = new DataSet<T>(numFeatures, featureSampleSlice, leftElements);
		ret[1] = new DataSet<T>(numFeatures, featureSampleSlice, rightElements);

		return ret;
	}

	private int[] generateRandomUniqueFeatures() {
		Set<Integer> featureSet = new HashSet<>();
		int[] returnFeatures = new int[absoluteFeatureSubSampleSize];
		while (featureSet.size() < absoluteFeatureSubSampleSize) {
			featureSet.add((int) (Math.random() * numFeatures));
		}
		int idx = 0;
		for (int i : featureSet) {
			returnFeatures[idx++] = i;
		}
		return returnFeatures;
	}

	private double impurity(int feature, Element<T> e) {
		T value = e.getAttribute(feature);

		// perform the actual split
		ArrayList<Element<T>> leftElements = new ArrayList<>(), rightElements = new ArrayList<>();
		for (Element<T> el : elements)
			if (el.getAttribute(feature).compareTo(value) < 0)
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
			for (Element<T> el : leftElements)
				if (el.getCategory() != entry.getKey())
					leftError++;

			// error is zero, if there is no element of that category in the left set
			if (leftError == leftElements.size())
				leftError = 0;

			double rightError = 0;
			for (Element<T> el : rightElements)
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

	public static class SplitResult<T extends Comparable<T>> implements
			Comparable<SplitResult<T>> {
		private SplitPoint<T> splitPoint;
		private DataSet<T> leftSet, rightSet;
		private double impurity;

		public SplitResult(SplitPoint<T> point, DataSet<T> left, DataSet<T> right,
				double impurity) {
			splitPoint = point;
			leftSet = left;
			rightSet = right;
			this.impurity = impurity;
		}

		public SplitPoint<T> getSplitPoint() {
			return splitPoint;
		}

		public DataSet<T> getLeftSet() {
			return leftSet;
		}

		public DataSet<T> getRightSet() {
			return rightSet;
		}

		public double getImpurity() {
			return impurity;
		}

		@Override
		public int compareTo(SplitResult<T> arg0) {
			return splitPoint.getSplitValue().compareTo(
					arg0.getSplitPoint().getSplitValue());
		}
	}

	public int categoryCount() {
		return categories.size();
	}

	public int featureCount() {
		return numFeatures;
	}

	public float getFeatureSampleSlice() {
		return featureSampleSlice;
	}

	public ArrayList<Element<T>> getElements() {
		return elements;
	}
}
