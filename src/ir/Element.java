package ir;

public class Element {
	private double[] attributes;
	private int category = -1; // used to determine best split points with the learning data

	public Element(double[] attrs) {
		attributes = attrs;
	}

	public Element(double[] attrs, int cat) {
		attributes = attrs;
		category = cat;
	}

	public double getAttribute(int index) {
		return attributes[index];
	}

	public int getCategory() {
		return category;
	};

	@Override
	public String toString() {
		String ret = category + ":";

		for (double a : attributes)
			ret += " \t " + a;
		return ret;
	}
}
