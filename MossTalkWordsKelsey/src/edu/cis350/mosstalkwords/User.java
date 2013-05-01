package edu.cis350.mosstalkwords;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Intent;
import android.util.Log;

public class User implements Serializable {
	//score, speed, efficiency, streaks
	//int longestStreak=0;
	public static final int NUMSTARS = 3; 
	private int totalScore=0;
	private int currentStreak;
	int maxAttemptsAllowed=3;//number of attempts considered for efficiency calculations, anything over this is considered 0 efficiency
	public HashMap<String, Integer> starsForSets;
	public HashMap<String, int []> stimulusSetScores;
	public HashMap<String, Integer> longestStreakForSets;
	public HashMap<String, int [][]> stimulusSetEfficiencies;
	public HashMap<String, Integer> percentEfficiencyForSets;
	public HashMap<String, Integer> bestStreaksForSets;
	public HashMap<String, Integer> bestCompletenessForSets;//can't make BestReport object or serializable passing into intent breaks
	public HashMap<String, Integer> bestScoresForSets;
	public String name;
	public String email;
	
	public User() {
		totalScore = 0;
		currentStreak = 0;
		starsForSets = new HashMap<String, Integer>();
		stimulusSetScores = new HashMap<String, int[]>();
		longestStreakForSets = new HashMap<String, Integer>();
		stimulusSetEfficiencies = new HashMap<String, int[][]>();
		percentEfficiencyForSets = new HashMap<String,Integer>();	
		bestStreaksForSets=new HashMap<String, Integer>();
		bestCompletenessForSets=new HashMap<String, Integer>();
		bestScoresForSets=new HashMap<String, Integer>();
		name = null;
		email=null;
		
	}
	
	public void initSet(String setName, int setSize) {
		int[] scores = new int[setSize];
		int[][] eff = new int[setSize][2];
		stimulusSetScores.put(setName,scores);
		stimulusSetEfficiencies.put(setName,eff);
	}
	
	public void updateImageScore(String setName, int imageIdx, int score) {	
		stimulusSetScores.get(setName)[imageIdx] = score;
		this.totalScore += score;
	}
	
	public void updateImageEfficiency(String setName, int imageIdx, int hints, int attempts) {
		stimulusSetEfficiencies.get(setName)[imageIdx][0] = hints;
		stimulusSetEfficiencies.get(setName)[imageIdx][1] = attempts;
		System.out.printf("updating efficiency hints: %d attempts %d \n", hints, attempts);
	}
	
	public int getTotalScore() {
		return this.totalScore;
	}
	public String getTotalScoreString()
	{
		return Integer.toString(totalScore);
	}
	public void increaseStreak() {
		currentStreak++;
	}
	public void setTotalScore(int totscore)
	{
		totalScore=totscore;
	}
	public void endedSet(String setName) {
		this.calculateStarScore(setName);
		this.calculateAverageEfficiencyPercent(setName);//calculate the percentage for this set
		this.streakEnded(setName);
		updateBestReport(getLongestStreak(setName),getAverageEfficiencyPercent(setName),getStarScore(setName),setName);
		//nextSet();
	}
	
	public boolean hasPlayedSet(String setName) {
		return this.stimulusSetScores.containsKey(setName);
	}
	public HashSet<String> getSetScoresInMapOfStrings()
	{
		HashSet<String> scoreStrings=new HashSet<String>();
		for(int i:starsForSets.values())
		{
			scoreStrings.add(Integer.toString(i));
		}
		return scoreStrings;
	}
	public String generateSetReport(StimulusSet currentSet)
	{
		String fullReport="";
		fullReport+=("User: "+name+"\n");
		fullReport+=("CurrentSet: "+currentSet.getName()+"\n");
		String efficiencyPercent=Integer.valueOf(getAverageEfficiencyPercent(currentSet.getName())).toString();
		fullReport+=("Completeness: "+efficiencyPercent+"%\n");
		String longestStreak=Integer.valueOf(getLongestStreak(currentSet.getName())).toString();
		fullReport+=("Longest Streak: "+longestStreak+"\n");
		fullReport+=("\nImage By Image Statistics:\n\n");
		int index=0;
		for(Stimulus image:currentSet.stimuli)
		{
			fullReport+=generateImageStatistics(currentSet,image,index);
			index++;
		}
		
		return fullReport;
	}
	public String generateImageStatistics(StimulusSet currentSet, Stimulus currentImage, int index)
	{
		String imageReport="";
		imageReport+=currentImage.name+":\n";
		imageReport+="Score: "+Integer.valueOf(stimulusSetScores.get(currentSet.getName())[index]).toString()+"\n";
		imageReport+="Number Of Hints Used: "+Integer.valueOf((stimulusSetEfficiencies.get(currentSet.getName()))[index][0]).toString()+"\n";
		imageReport+="Number of Attempts: "+Integer.valueOf((stimulusSetEfficiencies.get(currentSet.getName()))[index][1])+"\n";
		imageReport+="\n";
		return imageReport;
	}
	public void streakEnded(String currentSet)
	{
		//if a streak already exists, and this streak is larger, update the streak
		if(this.longestStreakForSets.containsKey(currentSet)&&
				currentStreak>this.longestStreakForSets.get(currentSet))
		{	
			this.longestStreakForSets.remove(currentSet);
			this.longestStreakForSets.put(currentSet, currentStreak);
		}
		//if there is no streak, make this the longest streak
		else if(!this.longestStreakForSets.containsKey(currentSet)) {
			this.longestStreakForSets.put(currentSet, currentStreak);
		}
		//reset streak counter
		currentStreak=0;
	}
	
	
	public int getStarScore(String stimulusSetName)
	{
		return starsForSets.get(stimulusSetName);
	}
	public boolean containsBestStarScore(String stimulusSetName)
	{
		return bestScoresForSets.containsKey(stimulusSetName);
	}
	public void calculateStarScore(String stimulusSetName)
	{
		int temp[]=stimulusSetScores.get(stimulusSetName);
		int totalScore= temp.length*300;
		int interval = totalScore/NUMSTARS;
		int setScore=0;
		for(int i: temp)
		{
			setScore+=i;
		}
		if(setScore>2*interval) {
			starsForSets.put(stimulusSetName, Integer.valueOf(3));
		}
		else if(setScore>1*interval) {
			starsForSets.put(stimulusSetName, Integer.valueOf(2));
		} else {
			starsForSets.put(stimulusSetName, Integer.valueOf(1));
		}
	}
	public void updateBestReport(int streak, int complete, int score, String setName)
	{
		if(bestScoresForSets.containsKey(setName))
		{
			if(bestScoresForSets.get(setName)<score)
				bestScoresForSets.put(setName,score);
			if(bestStreaksForSets.get(setName)<streak)
				bestStreaksForSets.put(setName,streak);
			if(bestCompletenessForSets.get(setName)<complete)
				bestCompletenessForSets.put(setName, complete);
		}
		else
		{
			bestScoresForSets.put(setName,score);
			bestStreaksForSets.put(setName,streak);
			bestCompletenessForSets.put(setName, complete);
		}
	}
	public int getAverageEfficiencyPercent(String stimulusSetName)
	{
		return percentEfficiencyForSets.get(stimulusSetName);
	}
	public int getLongestStreak(String stimulusSetName)
	{
		return longestStreakForSets.get(stimulusSetName);
	}
	public void resetLongestStreak(String stimulusSetName)
	{
		longestStreakForSets.put(stimulusSetName, 0);
	}
	public void calculateAverageEfficiencyPercent(String stimulusSetName)
	{
		int temp[][]=stimulusSetEfficiencies.get(stimulusSetName);
		int maxHintsUsed=temp.length*3;
		int maxNumberOfAttempts=temp.length*maxAttemptsAllowed;
		System.out.printf("max hints used:%d max number of attempts: %d", maxHintsUsed, maxNumberOfAttempts);
		Log.d("efficiency avg calc","max hints used: "+ String.valueOf(maxHintsUsed)+" max number of attempts: "+ String.valueOf(maxNumberOfAttempts));
		int userHintsUsed=0;
		int userNumberOfAttempts=0;
		for(int i[]: temp)
		{
			userHintsUsed+=i[0];
			Log.d("attempts image", "attempts used: "+String.valueOf(i[1]));
			if(i[1]>3)
				userNumberOfAttempts+=3;
			else
				userNumberOfAttempts+=(i[1]);
		
		}
		
		int hintsEfficiencyComponent=(50-userHintsUsed*50/maxHintsUsed);
		Log.d("hints efficiency comp.","hints Efficiency Component: "+ String.valueOf(hintsEfficiencyComponent));
		int attemptsEfficiencyComponent=(50-userNumberOfAttempts*50/maxNumberOfAttempts);
		Log.d("attempts efficiency comp.","attempts Efficiency Component: "+ String.valueOf(attemptsEfficiencyComponent));
		int percentEfficiency=hintsEfficiencyComponent+attemptsEfficiencyComponent;
		percentEfficiencyForSets.put(stimulusSetName, Integer.valueOf(percentEfficiency));
		
	}

}
