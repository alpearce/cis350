package edu.cis350.mosstalkwords;


import java.util.ArrayList;

/*This class takes in a name that represents the name of the stimulus set in string form. A stimulus set is just a collection of images, grouped based on difficulty or category
 * eventually. There are setter and getter methods.*/
public class StimulusSet {
	String setName;
	ArrayList<Stimulus> stimuli;
	
	public StimulusSet(String name) {
		setName = name;
		stimuli = new ArrayList<Stimulus>();
	}
	
	public StimulusSet(String name, ArrayList<Stimulus> stimuliInSet)
	{
		setName=name;
		stimuli=stimuliInSet;
	}
	
	public String getName()
	{
		return setName;
	}
	public ArrayList<Stimulus> getStimuli()
	{
		return stimuli;
	}
	
	//return just the names of the stimuli to use in S3 urls
	public String[] getStimuliNames() {
		String[] names = new String[this.stimuli.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = stimuli.get(i).getName();
		}
		return names;
	}
	
	public void setName(String name)
	{
		setName=name;
	}
	public void setStimuli(ArrayList<Stimulus> stimuliInSet)
	{
		stimuli=stimuliInSet;
	}
	
	public int length() {
		return stimuli.size();
	}
}
