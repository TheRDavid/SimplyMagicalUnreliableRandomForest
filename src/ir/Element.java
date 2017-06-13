package ir;

public class Element<T extends Comparable<T>>{
	private T[] attributes;
	private int category = -1; // used to determine best split points with the learning data
	private static final int maxFeaturePrints = 25;
	public Element(T[] attrs) {
		attributes = attrs;
	}

	public Element(T[] attrs, int cat) {
		attributes = attrs;
		category = cat;
	}

	public T getAttribute(int index) {
		return attributes[index];
	}

	public int getCategory() {
		return category;
	};

	@Override
	public String toString() {
		String ret = category + ":";

		for (int i = 0; i < maxFeaturePrints && i < attributes.length; i++)
			ret += " \t " + attributes[i];
		return ret;
	}

	
}
