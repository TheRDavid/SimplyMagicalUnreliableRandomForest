package ir;
public class SplitPoint {
	private int featureIndex;
	private double splitValue;

	public SplitPoint(int fi, Element e) {
		featureIndex = fi;
		splitValue = e.getAttribute(fi);
	}

	public int getFeatureIndex() {
		return featureIndex;
	}

	public double getSplitValue() {
		return splitValue;
	}
}
