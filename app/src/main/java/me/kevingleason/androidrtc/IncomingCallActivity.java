package me.kevingleason.androidrtc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

import me.kevingleason.androidrtc.util.Constants;
import me.kevingleason.pnwebrtc.PnPeerConnectionClient;


public class IncomingCallActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private String username;
    private String callUser;

    private Pubnub mPubNub;
    private TextView mCallerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        this.mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE);
        if (!this.mSharedPreferences.contains(Constants.USER_NAME)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        this.username = this.mSharedPreferences.getString(Constants.USER_NAME, "");

        Bundle extras = getIntent().getExtras();
        if (extras==null || !extras.containsKey(Constants.CALL_USER)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Need to pass username to IncomingCallActivity in intent extras (Constants.CALL_USER).",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.callUser = extras.getString(Constants.CALL_USER, "");
        this.mCallerID = (TextView) findViewById(R.id.caller_id);
        this.mCallerID.setText(this.callUser);

        this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        this.mPubNub.setUUID(this.username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptCall(View view){
        Intent intent = new Intent(IncomingCallActivity.this, VideoChatActivity.class);
        intent.putExtra(Constants.USER_NAME, this.username);
        intent.putExtra(Constants.CALL_USER, this.callUser);
        startActivity(intent);
    }

    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    public void rejectCall(View view){
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.username);
        this.mPubNub.publish(this.callUser,hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Intent intent = new Intent(IncomingCallActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(this.mPubNub!=null){
            this.mPubNub.unsubscribeAll();
        }
    }
}
