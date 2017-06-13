package ir;

import ir.DataSet.SplitResult;

import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.Map;

import test.BasicTest;

public class RTree {

	private DataSet sample;
	private RNode root;
	public final long treeID = TREE_ID++;
	protected static long NODE_ID = 0, TREE_ID = 0;

	public RTree(DataSet s, float featureSampleSlice) {
		System.out.println("Tree #"+treeID+" @ " + NODE_ID + " Nodes");
		sample = s;
		root = new RNode(sample, featureSampleSlice);
	}

	public class RNode {
		private SplitPoint splitPoint;
		private RNode left = null, right = null;
		private DataSet sample;
		private int category = -1;
		private int numElements;
		public final long nodeID = NODE_ID++;

		public RNode(DataSet set, float featureSampleSlice) {
			sample = set;

			HashMap<Integer, Integer> categories = DataSet.generateCategoryMap(sample
					.getElements());
			int currentCategory = -1, currentNum = -1;
			for (Map.Entry<Integer, Integer> e : categories.entrySet())
				if (e.getValue() > currentNum) {
					currentNum = e.getValue();
					currentCategory = e.getKey();
				}
			category = currentCategory;

			splitPoint = new SplitPoint(1, sample.getElements().get(0));
			numElements = sample.getElements().size();

			// is it a leaf?
			//System.out.println("\nnew Node with " + set.categoryCount() + " categories");
			//BasicTest.print(set.getDataCopy());
			if (set.categoryCount() == 1)
				return;

			// generate SplitPoint
			SplitResult sResult = set.findBestSplit();
			if (sResult.getLeftSet().size() == 0 || sResult.getRightSet().size() == 0) // should not happen
				return;

			splitPoint = sResult.getSplitPoint();
			//System.out.println("Going Left");
			left = new RNode(sResult.getLeftSet(), featureSampleSlice);
			//System.out.println("Going Right");
			right = new RNode(sResult.getRightSet(), featureSampleSlice);
		}

		public SplitPoint getSplitPoint() {
			return splitPoint;
		}

		public int getCategory() {
			return category;
		}

		public RNode getLeft() {
			return left;
		}

		public RNode getRight() {
			return right;
		}

		public DataSet getSample() {
			return sample;
		}

		public int categorize(Element element) {
			if (left == null)
				return category;
			if (element.getAttribute(splitPoint.getFeatureIndex()) < splitPoint
					.getSplitValue())
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

	public int categorize(Element element) {
		return root.categorize(element);
	}

}
