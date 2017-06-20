package forestFarm;

public class ForestSettings
{
	private float subSampleSize, featureSampleSize;
	private int numTrees, numFeatures;

	public ForestSettings(float subSSize, float featureSSize, int numT, int numF)
	{
		subSampleSize = subSSize;
		featureSampleSize = featureSSize;
		numTrees = numT;
		numFeatures = numF;
	}

	public float getSubSampleSize()
	{
		return subSampleSize;
	}

	public void setSubSampleSize(float subSampleSize)
	{
		this.subSampleSize = subSampleSize;
	}

	public float getFeatureSampleSize()
	{
		return featureSampleSize;
	}

	public void setFeatureSampleSize(float featureSampleSize)
	{
		this.featureSampleSize = featureSampleSize;
	}

	public int getNumTrees()
	{
		return numTrees;
	}

	public void setNumTrees(int numTrees)
	{
		this.numTrees = numTrees;
	}

	public int getNumFeatures()
	{
		return numFeatures;
	}

	public void setNumFeatures(int numFeatures)
	{
		this.numFeatures = numFeatures;
	}
}
