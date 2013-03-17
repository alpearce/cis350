package edu.cis350.mosstalkwords;

/*This class takes in a name that represents the name of the stimulus set in string form. A stimulus set is just a collection of images, grouped based on difficulty or category
 * eventually. There are setter and getter methods.*/
public class StimulusSet {
	String setName;
	Stimulus[] stimuli;
	public StimulusSet(String name, Stimulus[] stimuliInSet)
	{
		setName=name;
		stimuli=stimuliInSet;
	}
	
	public String getName()
	{
		return setName;
	}
	public Stimulus [] getStimuli()
	{
		return stimuli;
	}
	
	//return just the names of the stimuli to use in S3 urls
	public String[] getStimuliNames() {
		String[] names = new String[this.stimuli.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = stimuli[i].getName();
		}
		return names;
	}
	
	public void setName(String name)
	{
		setName=name;
	}
	public void setStimuli(Stimulus [] stimuliInSet)
	{
		stimuli=stimuliInSet;
	}
	
	public int length() {
		return stimuli.length;
	}
}
