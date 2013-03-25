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
	    
	    TextView completeness=(TextView) layout.findViewById(R.id.Completeness);
	    String efficiencyPercent=new Integer(currentUser.getAverageEfficiencyPercent(currentSet)).toString();
	    completeness.setText(completeness.getText()+efficiencyPercent+"%");
	    
	    TextView streak=(TextView) layout.findViewById(R.id.streak);
	    String longestStreak=new Integer(currentUser.getLongestStreak(currentSet)).toString();
	    streak.setText(streak.getText()+longestStreak);
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle("          Congratulations!")//hackish extra spaces to center the title since not an option
				.setView(layout)
				.setPositiveButton(R.string.restart, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//restart set
					}
				})
				.setNegativeButton(R.string.menu, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						//go to main menu
					}
				});
					
			
				AlertDialog alert= builder.create();//create the AlertDialog object and return it
				//alert.setContentView(R.layout.dialog_endset);
				alert.show();
				
	}

}
