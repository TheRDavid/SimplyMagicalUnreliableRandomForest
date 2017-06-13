package ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class RForest {
	private ArrayList<RTree> trees = new ArrayList<>();
	private DataSet data;

	public RForest(DataSet d, float subSampleSize, int numSubSamples) {
		data = d;
		DataSet[] subs = data.generateSubsamples(numSubSamples,
				(int) (data.size() * subSampleSize));
		for (DataSet sub : subs) {
			trees.add(new RTree(sub, data.getFeatureSampleSlice()));
		}
	}

	public ArrayList<RTree> getTrees() {
		return trees;
	}

	public DataSet getData() {
		return data;
	}

	public int categorize(Element element) {
		int[] vote = new int[data.categoryCount()];
		for (RTree tree : trees) {
			int voice = tree.categorize(element);
			vote[voice]++;
			System.out.println("tree " + tree.treeID + " votes for " + voice);
		}
		int currentCat = -1, currentVotes = -1;
		for (int i = 0; i < vote.length; i++) {
			System.out.println("Current category: "+currentCat+ " with "+currentVotes);
			if (vote[i] > currentVotes) {
				currentCat = i;
				currentVotes = vote[i];
			}
		}
		for(int i = 0; i < vote.length; i++) System.out.println(i+": "+vote[i]);
		return currentCat;
	}
}
