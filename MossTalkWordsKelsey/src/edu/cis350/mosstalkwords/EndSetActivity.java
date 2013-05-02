package edu.cis350.mosstalkwords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.os.Bundle;

public class EndSetActivity extends Activity {
	User currentUser;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //setContentView(R.layout.dialog_endset);
	    //createDialog();
	    // TODO Auto-generated method stub
	    Intent i=getIntent();
	    currentUser=(User) i.getSerializableExtra("User");
	    String currentSet= (String) i.getStringExtra("currentSet");
	    
		//score.setNumStars(3);
	    Context mContext = this; //this.getApplicationContext();
	   
	    
	    
	    LayoutInflater inflater = this.getLayoutInflater();//(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.dialog_endset, null);
	   
	    RatingBar score=(RatingBar) layout.findViewById(R.id.scoreBar);
	    int starScore=currentUser.getStarScore(currentSet);
	    score.setNumStars(starScore);
	    score.setRating(starScore);
	    
	    TextView message=(TextView) layout.findViewById(R.id.Message);
	    String msg;
	    switch (starScore) {
	    	case 1: msg = getString(R.string.one_star); break;
	    	case 2: msg = getString(R.string.two_star); break;
	    	case 3: msg = getString(R.string.three_star); break;
	    	case 4: msg = "Four stars. Good job, but don't get cocky."; break;
	    	case 5: msg = "Five stars. If the picture was you, the word would be \"awesome\""; break;
	    	default: msg = "";
	    }
	    message.setText(msg);
	    
	    TextView completeness=(TextView) layout.findViewById(R.id.Completeness);
	    String efficiencyPercent=new Integer(currentUser.getAverageEfficiencyPercent(currentSet)).toString();
	    completeness.setText(completeness.getText()+efficiencyPercent+"%");
	    
	    TextView streak=(TextView) layout.findViewById(R.id.streak);
	    String longestStreak=new Integer(currentUser.getLongestStreak(currentSet)).toString();
	    streak.setText(streak.getText()+longestStreak);
	    
	    RatingBar bestScore=(RatingBar) layout.findViewById(R.id.bestScoreBar);
	    int bestStarScore=0;
	    if(currentUser.bestScoresForSets.containsKey(currentSet))
	    	bestStarScore=currentUser.bestScoresForSets.get(currentSet);
	    if(bestStarScore>0)
	    	bestScore.setNumStars(bestStarScore);
	    bestScore.setRating(bestStarScore);
	    
	    TextView bestCompleteness=(TextView) layout.findViewById(R.id.bestCompleteness);
	    String bestEfficiencyPercent=Integer.valueOf(currentUser.bestCompletenessForSets.get(currentSet)).toString();
	    bestCompleteness.setText(bestCompleteness.getText()+bestEfficiencyPercent+"%");
	    
	    TextView bestStreak=(TextView) layout.findViewById(R.id.bestStreak);
	    String bestLongestStreak=Integer.valueOf(currentUser.bestStreaksForSets.get(currentSet)).toString();
	    bestStreak.setText(bestStreak.getText()+bestLongestStreak);
	    
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setCancelable(false);
			builder.setTitle("Congratulations!")//hackish extra spaces to center the title since not an option
				.setView(layout)
				.setPositiveButton(R.string.send, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
						Intent i=getIntent();
						i.putExtra("Send", true);
						i.putExtra("No", false);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//go to main menu
						Intent i=getIntent();
						i.putExtra("Send", false);
						i.putExtra("No", true);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				/*.setNeutralButton(R.string.restart, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
						Intent i=getIntent();
						i.putExtra("Main Menu", false);
						i.putExtra("Replay Set", true);
						i.putExtra("Next Set", false);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				.setPositiveButton(R.string.nextSet, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
						Intent i=getIntent();
						i.putExtra("Main Menu", false);
						i.putExtra("Replay Set", false);
						i.putExtra("Next Set", true);
						setResult(RESULT_OK, i);
						finish();
					}
				})
				.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//go to main menu
						Intent i=getIntent();
						i.putExtra("Main Menu", true);
						i.putExtra("Replay Set", false);
						i.putExtra("Next Set", false);
						setResult(RESULT_OK, i);
						finish();
					}
				})*/;
					
			
				AlertDialog alert= builder.create();//create the AlertDialog object and return it
				//alert.setContentView(R.layout.dialog_endset);
				alert.show();
				
	}

}
