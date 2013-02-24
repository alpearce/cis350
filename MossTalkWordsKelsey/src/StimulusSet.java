
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
	public void setName(String name)
	{
		setName=name;
	}
	public void setStimuli(Stimulus [] stimuliInSet)
	{
		stimuli=stimuliInSet;
	}
}
