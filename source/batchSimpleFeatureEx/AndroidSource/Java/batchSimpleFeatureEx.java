/****************************************************************************************************************************
 * 
 * batchSimpleFeatureEx
 * 
 * batchSimpleFeatureEx handles some basic functionalities for implementing Batch Automatic unlocking system
 * into your Game Maker Pro game.
 * 
 * With batchSimpleFeatureEx you can:
 * - configure Batch with your API_KEY
 * - open a popup which listens for any offer and callbacks to a Game Maker Studio Async Social event whenever
 *   an offer is found.
 * Both feature and resources types of offers are supported, 
 * and their reference and value (or quantity) are both passed to Game Maker.
 *
 * NOTE: for resource offers, quantity is passed to Game Maker as String.
 * Into the Async Social "batchFeature" or "batchResource" are passed as "type", references are 
 * passed as "reference" while value and quantity are passed both as "value".
 * 
 * batchSimpleFeatureEx is intended as extension for Game Maker Studio Pro v1.3+
 * to be used into your Android project.
 * This is just the Java file, to make this work into your Game Maker game, you need to properly setup your
 * project, and you need to have batch.jar into the extension lib folder.
 * 
 * For information about batchSimpleFeatureEx usage, check the code comments 
 * and Batch documentation and API references.
 *
 * IMPORTANT NOTE: this is just a simple implementation of the Batch unlock feature, in fact, batchSimpleFeatureEx
 * can handle only one offer at time, because after any offer is found the Game Maker Studio Async Social event is
 * called and the waiting popup is closed automatically.
 *
 * ADDITIONAL NOTE:
 * The popup will close automatically even if the received offer is not handled by your Game Maker code, 
 * it will simply close, without notifying anything to the user, whenever any offer is redeemed.
 * Also. remember that, at the moment of the Game Maker Async Social event call, the offer is considered
 * as already redeem by the user, so it cannot be redeemed anymore by the same user again.
 * So, be sure that you handle all the offers that can be received, and properly notify the user
 * about the offer just redeemed.
 * 
 * I hope this implementation can help you, even if you need to implement more complex Batch functionalities.
 * 
 *
 * Author: Mattia Fortunati
 * Contact: mattia@mattiafortunati.com
 * Website: http://www.mattiafortunati.com
 * 
 ****************************************************************************************************************************/

package ${YYAndroidPackageName};

//Basic imports
import android.util.Log;
import java.lang.String;
//
import android.app.Activity;
import android.content.Intent;

//Import Batch
import com.batch.android.*;

//Import Game Maker classes
import ${YYAndroidPackageName}.R;
import com.yoyogames.runner.RunnerJNILib;
import ${YYAndroidPackageName}.RunnerActivity;

//Other imports for implementing buttons and texts
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.Gravity;

public class batchSimpleFeatureEx extends Activity implements BatchUnlockListener
{

    //var declarations
    private static TextView msg;
    private static String titleStr;
    private static String messageStr;
    private static String btnStr;
    private static final int EVENT_OTHER_SOCIAL = 70;


    //Public methods, to be called from Game Maker Studio.
    
    //Call batchSetup at the very beginning of your game. 
    //Use your Batch API_KEY
    public void batchSetup(String API_KEY) {
        Batch.setConfig(new Config(API_KEY));
        //Log.i("yoyo","*****BATCH CONFIGURED WITH API KEY: "+ API_KEY);
    }

    //Call batchOpen whenever you want to open the popup and start listening for offers.
    //titleString is the title of the popup, the header.
    //messageString is the message to show, while listening.
    //buttonString is the close button text.
    public void batchOpen(String titleString, String messageString, String buttonString) {
        Intent intent = new Intent(RunnerActivity.CurrentActivity, batchSimpleFeatureEx.class);
        RunnerActivity.CurrentActivity.startActivity(intent);
        titleStr = titleString;
        messageStr = messageString;
        btnStr = buttonString;
        Log.i("yoyo","*****BATCH OPENED");
    }


    //Overrides
    @Override
    protected void onStart()
    {
        super.onStart();        
        
        //set batch unlock listener passing this, because BatchUnlockListener is implemented
        Batch.Unlock.setUnlockListener(this);
        Log.i("yoyo","*****BATCH LISTENER  SET");
        
        //start batch
        Batch.onStart(this);
        Log.i("yoyo","*****BATCH STARTED");
        
        //create some linear layouts for handling text and button
        LinearLayout lLayout = new LinearLayout(this);
        lLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams lLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams lLayoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
        lLayoutParams2.gravity = Gravity.CENTER;
        LayoutParams lLayoutParams3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
        lLayoutParams3.gravity = Gravity.RIGHT;
        setContentView(lLayout , lLayoutParams);

        //set activity title, header.
        this.setTitle(titleStr);

        //create text and set message
        msg = new TextView(this);
        msg.setText(messageStr);
        msg.setLayoutParams(lLayoutParams2);
        msg.setTextSize(20); 
        lLayout.addView(msg);

        //create button and set text
        Button btn = new Button(this);
        btn.setText(btnStr);
        lLayout.addView(btn,lLayoutParams3);
        
        //button listener
        btn.setOnClickListener( new OnClickListener() {

            @Override
            public void onClick(View v) {              
                finish();
            }
        });
        
    }

    @Override
    protected void onStop()
    {
        //stop batch
        Batch.onStop(this);
        Log.i("yoyo","*****BATCH STOPPED");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        //destroy batch
        Batch.onDestroy(this);
        Log.i("yoyo","*****BATCH DESTROYED");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        //
        Batch.onNewIntent(this, intent);
        Log.i("yoyo","*****BATCH NEW INTENT");
        super.onNewIntent(intent);
    }

    @Override
    //remember that this will handle just one offer at time, since as soon as
    //an offer is found, Game Maker Social Async event is called and finish() is called
    //Note that at this point, the offer has already been redeemed by the user, 
    //and cannot be redeemed anymore by the same user again.
    public void onRedeemAutomaticOffer(Offer offer)
    {
        Log.i("yoyo","*****BATCH OFFER RECEIVED");
        
        //check for feature offer
        for(Feature feature : offer.getFeatures())
        {
            //get reference and value
            String featureRef = feature.getReference();
            String value = feature.getValue();
            Log.i("yoyo","*****BATCH OFFER FEATURE REDEEMED");
            Log.i("yoyo","*****BATCH OFFER FEATURE REF:");
            Log.i("yoyo",featureRef);
            //make sure value is not null or empty
            if (value != null && !value.isEmpty()){
                    Log.i("yoyo","*****BATCH OFFER VALUE:");
                    Log.i("yoyo",value);
            }else{
                value = "";
            }
            //call Game Maker Social Async event
            //passing it reference and value
            ReturnAsync("batchFeature",featureRef,value);

        }

        //check for resource offer
        for(Resource resource : offer.getResources() )
        {
            //get reference and quantity
            String resourceRef = resource.getReference();
            int quantity = resource.getQuantity();
            Log.i("yoyo","*****BATCH OFFER RESOURCE REDEEMED");
            Log.i("yoyo","*****BATCH OFFER RESOURCE REF:");
            Log.i("yoyo",resourceRef);
            Log.i("yoyo","*****BATCH OFFER QUANTITY:");
            Log.i("yoyo",String.valueOf(quantity));
            //call Game Maker Social Async event
            //passing it reference and quantity (as string)
            ReturnAsync("batchResource",resourceRef,String.valueOf(quantity));
        }

     finish();

    }

    //ReturnAsync is called whenever an offer is found
    //it will call the Game Maker Social Async event
    //passing "batchFeature" or "batchResource" as type
    //references as "reference"
    //and value or quantity as "value"
    public void ReturnAsync(String tp, String ref, String val)
    {
        int dsMapIndex = RunnerJNILib.jCreateDsMap(null, null, null);
        RunnerJNILib.DsMapAddString( dsMapIndex, "type", tp );
        RunnerJNILib.DsMapAddString( dsMapIndex, "reference", ref);
        RunnerJNILib.DsMapAddString( dsMapIndex, "value", val);
        RunnerJNILib.CreateAsynEventWithDSMap(dsMapIndex, EVENT_OTHER_SOCIAL);
    }

}


