package forest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class RForest<T extends Comparable<T>> {
	private ArrayList<RTree<T>> trees = new ArrayList<>();
	private DataSet<T> data;
	public enum DataMode {SAVE_ALL_THE_DATA, HURRY_UP_M8}
	public DataMode mode;
	private DataSet<T>[] subs;

	public RForest(DataSet<T> d, float subSampleSize, int numSubSamples, DataMode mode) {
		data = d;
		this.mode = mode;
		subs = data.generateSubsamples(numSubSamples,
				(int) (data.size() * subSampleSize));
	}

	public void grow()
	{
		for (DataSet<T> sub : subs) {
			trees.add(new RTree<T>(sub, data.getFeatureSampleSlice(), mode));
		}
	}
	
	public ArrayList<RTree<T>> getTrees() {
		return trees;
	}

	public DataSet<T> getData() {
		return data;
	}

	public int categorize(Element<T> element) {
		int[] vote = new int[data.categoryCount()];
		for (RTree<T> tree : trees) {
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
