   import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;
import android.app.Activity;

import edu.cis350.mosstalkwords.EndSetActivity;
import edu.cis350.mosstalkwords.MainActivity;
import edu.cis350.mosstalkwords.R;
import edu.cis350.mosstalkwords.User;
import edu.cis350.mosstalkwords.WelcomeActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;


public class Test extends ActivityInstrumentationTestCase2<MainActivity> {
  User user = new User();
  public Test() {
          super(MainActivity.class);
   }
   private Solo solo;
   @Override
   protected void setUp() throws Exception
   {
   solo = new Solo(getInstrumentation(),getActivity());
   }
   //testing the endScreen
   public void testClickOnSet () {
	//Button b = (Button) findViewById(R.id.btnChangeImage);
	solo.assertCurrentActivity("Welcome Activity first",WelcomeActivity.class);
	solo.clickOnButton("Living easy");
	solo.assertCurrentActivity("Main activity now", MainActivity.class);

   }
   public void testEndActivity() {
		solo.clickOnButton("Living easy");
		for (int i = 0; i < 10; i++) {
			solo.clickOnButton(R.id.btnChangeImage);
		}
		solo.assertCurrentActivity("Should Be EndActivity", EndSetActivity.class);
   }
   public void testMainMenuButton() {
	   solo.clickOnButton("Living easy");
		for (int i = 0; i < 10; i++) {
			solo.clickOnButton(R.id.btnChangeImage);
		}
		solo.clickOnButton("Main Menu");
		solo.assertCurrentActivity("WelcomeActivity", WelcomeActivity.class);
   }
   //if click next all the way through completeness should be 84%
   public void testCompletenessSCore() {
	   solo.clickOnButton("Living easy");
	 		for (int i = 0; i < 10; i++) {
	 			solo.clickOnButton(R.id.btnChangeImage);
	 		}
	 	assertTrue(solo.searchText("84"));
   }
   public void testReset() {
	   solo.clickOnButton("Living easy");
		for (int i = 0; i < 10; i++) {
			solo.clickOnButton(R.id.btnChangeImage);
		}
		solo.clickOnButton("Replay Set");
		solo.assertCurrentActivity("Back to Main Activity", MainActivity.class);
   }
   public void testHints() {
	   solo.clickOnButton("living medium");
	   solo.clickOnButton("Sentence");
	   solo.searchText("Geice uses a talking one of these to try to sell you car insurance.");
   }
 

}
