package edu.cis350.mosstalkwords;

import java.util.HashMap;

public class User {
	//score, speed, efficiency, streaks
	int longestStreak=0;
	int score=0;
	int maxAttemptsAllowed=3;//number of attempts considered for efficiency calculations, anything over this is considered 0 efficiency
	public HashMap<String, Integer> starsForSets;
	public HashMap<String, int []> stimulusSetScores;
	public HashMap<String, int [][]> stimulusSetEfficiencies;
	public HashMap<String, Integer> percentEfficiencyForSets;
	public void calculateStarScore(String stimulusSetName)
	{
		int temp[]=stimulusSetScores.get(stimulusSetName);
		int totalScore= temp.length*300;
		int userScore=0;
		for(int i: temp)
		{
			userScore+=i;
		}
		if(userScore*100/totalScore>70)
			starsForSets.put(stimulusSetName, Integer.valueOf(3));
		else if(userScore*100/totalScore>55)
			starsForSets.put(stimulusSetName, Integer.valueOf(2));
		else
			starsForSets.put(stimulusSetName, Integer.valueOf(1));
	}
	public void calculateAverageEfficiencyPercent(String stimulusSetName)
	{
		int temp[][]=stimulusSetEfficiencies.get(stimulusSetName);
		int maxHintsUsed=temp[0].length*3;
		int maxNumberOfAttempts=temp[1].length*maxAttemptsAllowed;
		int userHintsUsed=0;
		int userNumberOfAttempts=0;
		for(int i: temp[0])
		{
			userHintsUsed+=i;
		}
		for(int i: temp[1])
		{
			userNumberOfAttempts+=i;
		}
		int hintsEfficiencyComponent=(50-userHintsUsed*50/maxHintsUsed);
		int attemptsEfficiencyComponent=(50-userNumberOfAttempts*50/maxNumberOfAttempts);
		int percentEfficiency=hintsEfficiencyComponent+attemptsEfficiencyComponent;
		percentEfficiencyForSets.put(stimulusSetName, Integer.valueOf(percentEfficiency));
		
	}

}
