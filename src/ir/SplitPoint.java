package ir;
public class SplitPoint<T extends Comparable<T>> {
	private int featureIndex;
	private T splitValue;

	public SplitPoint(int fi, Element<T> e) {
		featureIndex = fi;
		splitValue = e.getAttribute(fi);
	}

	public int getFeatureIndex() {
		return featureIndex;
	}

	public T getSplitValue() {
		return splitValue;
	}
}
