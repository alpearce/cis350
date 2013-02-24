import android.widget.ImageView;
public class Stimulus {
	String name;
	String hints [];
	int difficulty;
	ImageView image;
	public Stimulus(String stimulusName, int stimulusDifficulty, String stimulusHints[], ImageView stimulusImage)
	{
		name=stimulusName;
		hints=stimulusHints;
		difficulty= stimulusDifficulty;
		image=stimulusImage;
	}
	public void setImage(ImageView stimulusImage)
	{
		image=stimulusImage;
	}
	public void setName(String stimulusName)
	{
		name=stimulusName;
	}
	public void setHints(String stimulusHints[])
	{
		hints=stimulusHints;
	}
	public void setDifficulty(int stimulusDifficulty)
	{
		difficulty= stimulusDifficulty;
	}
	public ImageView getImage()
	{
		return image;
	}
	public String getName()
	{
		return name;
	}
	public String [] getHints()
	{
		return hints;
	}
	public int getDifficulty()
	{
		return difficulty;
	}

}
