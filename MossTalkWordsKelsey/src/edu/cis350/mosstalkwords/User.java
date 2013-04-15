package edu.cis350.mosstalkwords;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Intent;
import android.util.Log;

public class User implements Serializable {
	//score, speed, efficiency, streaks
	//int longestStreak=0;
	private int totalScore=0;
	private int currentStreak;
	int maxAttemptsAllowed=3;//number of attempts considered for efficiency calculations, anything over this is considered 0 efficiency
	public HashMap<String, Integer> starsForSets;
	public HashMap<String, int []> stimulusSetScores;
	public HashMap<String, Integer> longestStreakForSets;
	public HashMap<String, int [][]> stimulusSetEfficiencies;
	public HashMap<String, Integer> percentEfficiencyForSets;
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
	
	public void increaseStreak() {
		currentStreak++;
	}
	
	public void endedSet(String setName) {
		this.calculateStarScore(setName);
		this.calculateAverageEfficiencyPercent(setName);//calculate the percentage for this set
		//nextSet();
	}
	
	public boolean hasPlayedSet(String setName) {
		return this.stimulusSetScores.containsKey(setName);
	}
		
	public void streakEnded(StimulusSet currentSet)
	{
		//if a streak already exists, and this streak is larger, update the streak
		if(this.longestStreakForSets.containsKey(currentSet.setName)&&
				currentStreak>this.longestStreakForSets.get(currentSet.setName))
		{	
			this.longestStreakForSets.remove(currentSet.setName);
			this.longestStreakForSets.put(currentSet.setName, currentStreak);
		}
		//if there is no streak, make this the longest streak
		else if(!this.longestStreakForSets.containsKey(currentSet.setName)) {
			this.longestStreakForSets.put(currentSet.setName, currentStreak);
		}
		//reset streak counter
		currentStreak=0;
	}
	
	
	public int getStarScore(String stimulusSetName)
	{
		return starsForSets.get(stimulusSetName);
	}
	public boolean containsStarScore(String stimulusSetName)
	{
		return starsForSets.containsKey(stimulusSetName);
	}
	public void calculateStarScore(String stimulusSetName)
	{
		int temp[]=stimulusSetScores.get(stimulusSetName);
		int totalScore= temp.length*300;
		int setScore=0;
		for(int i: temp)
		{
			setScore+=i;
		}
		if(setScore*100/totalScore>70) {
			starsForSets.put(stimulusSetName, Integer.valueOf(3));
		}
		else if(setScore*100/totalScore>55) {
			starsForSets.put(stimulusSetName, Integer.valueOf(2));
		}
		else {
			starsForSets.put(stimulusSetName, Integer.valueOf(1));
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
			userNumberOfAttempts+=i[1];
		
		}
		int hintsEfficiencyComponent=(50-userHintsUsed*50/maxHintsUsed);
		Log.d("hints efficiency comp.","hints Efficiency Component: "+ String.valueOf(hintsEfficiencyComponent));
		int attemptsEfficiencyComponent=(50-userNumberOfAttempts*50/maxNumberOfAttempts);
		Log.d("attempts efficiency comp.","attempts Efficiency Component: "+ String.valueOf(attemptsEfficiencyComponent));
		int percentEfficiency=hintsEfficiencyComponent+attemptsEfficiencyComponent;
		percentEfficiencyForSets.put(stimulusSetName, Integer.valueOf(percentEfficiency));
		
	}

}
