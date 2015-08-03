package me.kevingleason.androidrtc.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.kevingleason.androidrtc.MainActivity;
import me.kevingleason.androidrtc.R;
import me.kevingleason.androidrtc.adt.ChatUser;
import me.kevingleason.androidrtc.adt.HistoryItem;
import me.kevingleason.androidrtc.util.Constants;

/**
 * Created by GleasonK on 7/31/15.
 */
public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
    private final Context context;
    private Pubnub mPubNub;
    private LayoutInflater inflater;
    private List<HistoryItem> values;
    private Map<String, ChatUser> users;


    public HistoryAdapter(Context context, List<HistoryItem> values, Pubnub pubnub) {
        super(context, R.layout.history_row_layout, android.R.id.text1, values);
        this.context  = context;
        this.inflater = LayoutInflater.from(context);
        this.mPubNub  = pubnub;
        this.values   = values;
        this.users    = new HashMap<String, ChatUser>();
        updateHistory();
    }

    class ViewHolder {
        TextView    user;
        TextView    status;
        TextView    time;
        ImageButton callBtn;
        HistoryItem histItem;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final HistoryItem hItem = this.values.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView    = inflater.inflate(R.layout.history_row_layout, parent, false);
            holder.user    = (TextView) convertView.findViewById(R.id.history_name);
            holder.status  = (TextView) convertView.findViewById(R.id.history_status);
            holder.time    = (TextView) convertView.findViewById(R.id.history_time);
            holder.callBtn = (ImageButton) convertView.findViewById(R.id.history_call);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.user.setText(hItem.getUser().getUserId());
        holder.time.setText(formatTimeStamp(hItem.getTimeStamp()));
        holder.status.setText(hItem.getUser().getStatus());
        if (hItem.getUser().getStatus().equals(Constants.STATUS_OFFLINE))
            getUserStatus(hItem.getUser(), holder.status);
        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).dispatchCall(hItem.getUser().getUserId());
            }
        });
        holder.histItem=hItem;
        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    public void removeButton(int loc){
        this.values.remove(loc);
        notifyDataSetChanged();
    }

    private void getUserStatus(final ChatUser user, final TextView statusView){
        String stdByUser = user.getUserId() + Constants.STDBY_SUFFIX;
        this.mPubNub.getState(stdByUser, user.getUserId(), new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                JSONObject jsonMsg = (JSONObject) message;
                try {
                    if (!jsonMsg.has(Constants.JSON_STATUS)) return;
                    final String status = jsonMsg.getString(Constants.JSON_STATUS);
                    user.setStatus(status);
                    ((Activity)getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText(status);
                        }
                    });
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateHistory(){
        final List<HistoryItem> rtcHistory = new LinkedList<HistoryItem>();
        String usrStdBy = this.mPubNub.getUUID() + Constants.STDBY_SUFFIX;
        this.mPubNub.history(usrStdBy, 25, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                Log.d("HA-uH","HISTORY: " + message.toString());
                try {
                    JSONArray historyArray = ((JSONArray) message).getJSONArray(0);
                    for(int i=0; i< historyArray.length(); i++){
                        JSONObject historyJson = historyArray.getJSONObject(i);
                        String userName = historyJson.getString(Constants.JSON_CALL_USER);
                        long timeStamp  = historyJson.getLong(Constants.JSON_CALL_TIME);
                        ChatUser cUser  = new ChatUser(userName);
                        if (users.containsKey(userName)){
                            cUser = users.get(userName);
                        } else {
                            users.put(userName, cUser);
                        }
                        rtcHistory.add(0, new HistoryItem(cUser, timeStamp));
                    }
                    values = rtcHistory;
                    updateAdapter();
                } catch (JSONException e){
                    // e.printStackTrace();
                }
            }
        });
    }

    private void updateAdapter(){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Format the long System.currentTimeMillis() to a better looking timestamp. Uses a calendar
     *   object to format with the user's current time zone.
     * @param timeStamp
     * @return
     */
    public static String formatTimeStamp(long timeStamp){
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, h:mm a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }
}

