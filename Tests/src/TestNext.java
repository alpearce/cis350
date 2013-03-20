   import junit.framework.Assert;

import com.jayway.android.robotium.solo.Solo;

import edu.cis350.mosstalkwords.MainActivity;
import edu.cis350.mosstalkwords.User;
import android.test.ActivityInstrumentationTestCase2;


public class TestNext extends ActivityInstrumentationTestCase2<MainActivity> {
	 User user = new User();
	 public TestNext() {
          super(MainActivity.class);
	  }
	  private Solo solo;
	  @Override
	  protected void setUp() throws Exception
	  {
	  solo = new Solo(getInstrumentation(),getActivity());
	  }
	 
	  public void testNextButton () {
		  solo.clickOnButton("Next");
		  solo.clickOnButton("Sentence");
		  Assert.assertTrue(solo.searchText("hibernate"));
	  }
	  
	  public void testNextSet() {
		  solo.clickOnButton("Next Set");
		  solo.clickOnButton("Sentence");
		  Assert.assertTrue(solo.searchText("Africa"));
	  }
	  public void testSpeakButton()  {
	  	solo.clickOnButton("Speak");
	  	Assert.assertTrue(solo.searchText("Say what you see in the picture..."));
	  }
	  
	  public void testScore() {
		  solo.clickOnButton("Next");
		  solo.clickOnButton("Next");
		  solo.clickOnButton("Next"); 
		  Assert.assertEquals(new Integer(0), user.starsForSets.get("livingEasySets"));
	  }

}
