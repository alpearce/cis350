package edu.cis350.mosstalkwords;


import java.io.Serializable;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WelcomeActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_page);
		
		Intent i=getIntent();
		User currentUser=(User) i.getSerializableExtra("User");
		
		String currStimSetName="livingthingseasy2";
		
		RatingBar score=(RatingBar) findViewById(R.id.scoreBar1);
		int starScore=0;
		if(currentUser.containsBestStarScore(currStimSetName))
		{
			starScore=currentUser.bestScoresForSets.get(currStimSetName);
			Log.d("welcome stars","contains star score"+starScore);
		}
	    score.setNumStars(starScore);
	    score.setRating(starScore);
	    System.out.println("star score: " +starScore);
	    currStimSetName="nonlivingthingseasy2";
		score=(RatingBar) findViewById(R.id.scoreBar2);
		starScore=0;
		if(currentUser.containsBestStarScore(currStimSetName))
			starScore=currentUser.bestScoresForSets.get(currStimSetName);
	    score.setNumStars(starScore);
	    score.setRating(starScore);
	    
	    currStimSetName="livingthingsmedium2";
		score=(RatingBar) findViewById(R.id.scoreBar3);
		starScore=0;
		if(currentUser.containsBestStarScore(currStimSetName))
			starScore=currentUser.bestScoresForSets.get(currStimSetName);
	    score.setNumStars(starScore);
	    score.setRating(starScore);
	    
	    currStimSetName="nonlivingthingshard2";
		score=(RatingBar) findViewById(R.id.scoreBar4);
		starScore=0;
		if(currentUser.containsBestStarScore(currStimSetName))
			starScore=currentUser.bestScoresForSets.get(currStimSetName);
	    score.setNumStars(starScore);
	    score.setRating(starScore);
	    
	    currStimSetName="livingthingshard2";
		score=(RatingBar) findViewById(R.id.scoreBar5);
		starScore=0;
		if(currentUser.containsBestStarScore(currStimSetName))
			starScore=currentUser.bestScoresForSets.get(currStimSetName);
	    score.setNumStars(starScore);
	    score.setRating(starScore);
		//Button b1 = (Button)findViewById(R.id.livingeasy);
		//b1.setAlpha((float) 0.8);
	}
	
	public void onClickLivingEasy(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 0);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickLivingMedium(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 1);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickLivingHard(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 2);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickNonLivingEasy(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 3);
		setResult(RESULT_OK, i);
		finish();
	}
	public void onClickNonLivingHard(View view) {
		Intent i = getIntent();
		i.putExtra("indexOfSetsArray", 4);
		setResult(RESULT_OK, i);
		finish();
	}

}
