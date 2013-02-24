package edu.cis350.mosstalkwords;
import android.widget.ImageView;

/*This class takes in a string for the name of the "stimulus" which is just the image and the associated hints and difficulty. For that purpose, it takes in an int difficulty
 * which is either 0 or 1 for now, 0 being easy, 1 being hard. It takes in an array of hints in string form, there should be three hints per stimulus. The imageIDName is
 * R.drawable.exampleImageName. That is represented as an int and can be used to change the Image Resource or Image Drawable of an ImageView. There are setter and getter 
 * methods for each input.*/
public class Stimulus {
	String name;
	String hints [];
	int difficulty;
	int image;
	public Stimulus(String stimulusName, int stimulusDifficulty, String stimulusHints[], int imageIDName)
	{
		name=stimulusName;
		hints=stimulusHints;
		difficulty= stimulusDifficulty;
		image=imageIDName;
	}
	public void setImage(int imageIDName)
	{
		image=imageIDName;
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
	public int getImage()
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
