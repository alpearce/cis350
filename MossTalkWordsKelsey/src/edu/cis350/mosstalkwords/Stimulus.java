package edu.cis350.mosstalkwords;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

/*This class takes in a string for the name of the "stimulus" which is just the image and the associated hints and difficulty. For that purpose, it takes in an int difficulty
 * which is either 0 or 1 for now, 0 being easy, 1 being hard. It takes in an array of hints in string form, there should be three hints per stimulus. The imageIDName is
 * R.drawable.exampleImageName. That is represented as an int and can be used to change the Image Resource or Image Drawable of an ImageView. There are setter and getter 
 * methods for each input.*/
public class Stimulus {
	public static final int NUMHINTS = 3;
	public static final int NUMRHYMES = 3;
	String name;
	String[] rhymes; //have to do this so that we can do them individually
	String sentence;
	int difficulty;
	int image;
	Drawable image_drawable; //for use with new S3 stuff
	public Stimulus(String stimulusName, int stimulusDifficulty, String[] rhymes, String sentence)
	{
		name=stimulusName;
		this.rhymes = rhymes;
		this.sentence = sentence;
		difficulty= stimulusDifficulty;
	}
	public void setImage(int imageIDName)
	{
		image=imageIDName;
	}
	public void setName(String stimulusName)
	{
		name=stimulusName;
	}
	public void setHints(String[] rhymes, String sentence)
	{
		this.rhymes = rhymes;
		this.sentence = sentence;
	}
	public void setDifficulty(int stimulusDifficulty)
	{
		difficulty= stimulusDifficulty;
	}
	public int getImage()
	{
		return image;
	}
	

	public void setDrawable(Drawable d) {
		this.image_drawable = d;
	}
	
	public Drawable getDrawable() {
		return image_drawable;
	}
	
	public String getName()
	{
		return name;
	}
	/*public ArrayList<String> hints[] getHints()
	{
		return hints;
	} */
	public String[] getRhymes() {
		return rhymes;
	}

	public String getSentence() {
		return sentence;
	}
	
	public int getDifficulty()
	{
		return difficulty;
	}

}
