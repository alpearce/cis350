package edu.cis350.mosstalkwords;

public class BestReport {
	private int longestStreak;
	private int completeness;
	private int score;
	public BestReport()
	{
		longestStreak=0;
		completeness=0;
		score=0;
		
	}
	public void setBestStreak(int streak)
	{
		longestStreak=streak;
	}
	public void setBestCompleteness(int comp)
	{
		completeness=comp;
	}
	public void setBestScore(int scr)
	{
		score=scr;
	}
	public int getBestStreak()
	{
		return longestStreak;
	}
	public int getBestCompleteness()
	{
		return completeness;
	}
	public int getBestScore()
	{
		return score;
	}
}
