package forest;

import java.io.Serializable;

public class Vector3 implements Comparable<Vector3>, Serializable {
	private double x, y, z;

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public int compareTo(Vector3 arg0) {
		return (int) ((x + y + z) - (arg0.x + arg0.y + arg0.z));
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	@Override
	public String toString() {
		return x + ", " + y + ", " + z;
	}
}
