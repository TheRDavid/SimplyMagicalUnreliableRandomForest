package ir;

import ir.DataSet.SplitResult;
import ir.RForest.DataMode;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;

import test.BasicTest;

public class RTree<T extends Comparable<T>> {

	private DataSet<T> sample;
	private RNode<T> root;
	private RForest.DataMode dataMode;
	public final long treeID = TREE_ID++;
	protected static long NODE_ID = 0, TREE_ID = 0;

	public RTree(DataSet<T> s, float featureSampleSlice, RForest.DataMode mode) {
		System.out.println("Tree #"+treeID+" @ " + NODE_ID + " Nodes");
		dataMode = mode;
		if (mode == DataMode.SAVE_ALL_THE_DATA) sample = s;
		root = new RNode<T>(s, featureSampleSlice);
	}

	public class RNode<T extends Comparable<T>> {
		private SplitPoint<T> splitPoint;
		private RNode<T> left = null, right = null;
		private DataSet<T> sample;
		private int category = -1;
		private int numElements;
		public final long nodeID = NODE_ID++;

		public RNode(DataSet<T> set, float featureSampleSlice) {
			System.out.println("Node #"+nodeID);
			if (RTree.this.dataMode == DataMode.SAVE_ALL_THE_DATA)  sample = set;

			HashMap<Integer, Integer> categories = set.generateCategoryMap(set
					.getElements());
			int currentCategory = -1, currentNum = -1;
			for (Map.Entry<Integer, Integer> e : categories.entrySet())
				if (e.getValue() > currentNum) {
					currentNum = e.getValue();
					currentCategory = e.getKey();
				}
			category = currentCategory;

			splitPoint = new SplitPoint<T>(1, set.getElements().get(0));
			numElements = set.getElements().size();

			// is it a leaf?
			//System.out.println("\nnew Node with " + set.categoryCount() + " categories");
			//BasicTest.print(set.getDataCopy());
			//System.out.println(set.categoryCount());
			if (set.categoryCount() == 1)
				return;

			// generate SplitPoint
			//System.out.println("Best split?");
			SplitResult<T> sResult = set.findBestSplit();
			if (sResult.getLeftSet().size() == 0 || sResult.getRightSet().size() == 0) // should not happen
				return;

			splitPoint = sResult.getSplitPoint();
			//System.out.println("Going Left");
			left = new RNode<T>(sResult.getLeftSet(), featureSampleSlice);
			//System.out.println("Going Right");
			right = new RNode<T>(sResult.getRightSet(), featureSampleSlice);
		}

		public SplitPoint<T> getSplitPoint() {
			return splitPoint;
		}

		public int getCategory() {
			return category;
		}

		public RNode<T> getLeft() {
			return left;
		}

		public RNode<T> getRight() {
			return right;
		}

		public DataSet<T> getSample() {
			return sample;
		}

		public int categorize(Element<T> element) {
			if (left == null)
				return category;
			if (element.getAttribute(splitPoint.getFeatureIndex()).compareTo(splitPoint
					.getSplitValue())<0)
				return left.categorize(element);
			return right.categorize(element);
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "DATA [" + numElements + "] + #" + nodeID;
		}
	}

	public RNode getRoot() {
		return root;
	}

	public int categorize(Element<T> element) {
		return root.categorize(element);
	}

}