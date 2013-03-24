package edu.cis350.mosstalkwords;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.os.Bundle;

public class EndSetActivity extends Activity {
	User currentUser;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    //createDialog();
	    // TODO Auto-generated method stub
	    Intent i=getIntent();
	    currentUser=(User) i.getSerializableExtra("User");
	
			LayoutInflater inflater=getParent().getLayoutInflater();
			AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
			builder.setTitle(R.string.congrats)
				.setView(inflater.inflate(R.layout.dialog_endset,null))
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
				alert.show();
	}

}
