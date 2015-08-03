package me.kevingleason.androidrtc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import me.kevingleason.androidrtc.R;
import me.kevingleason.androidrtc.adt.ChatMessage;


/**
 * Created by GleasonK on 6/25/15.
 */
public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private static final long FADE_TIMEOUT = 3000;

    private final Context context;
    private LayoutInflater inflater;
    private List<ChatMessage> values;

    public ChatAdapter(Context context, List<ChatMessage> values) {
        super(context, R.layout.chat_message_row_layout, android.R.id.text1, values);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.values=values;
    }

    class ViewHolder {
        TextView sender;
        TextView message;
        TextView timeStamp;
        ChatMessage chatMsg;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ChatMessage chatMsg;
        if(position >= values.size()){ chatMsg = new ChatMessage("","",0); } // Catch Edge Case
        else { chatMsg = this.values.get(position); }
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_message_row_layout, parent, false);
            holder.sender = (TextView) convertView.findViewById(R.id.chat_user);
            holder.message = (TextView) convertView.findViewById(R.id.chat_message);
            holder.timeStamp = (TextView) convertView.findViewById(R.id.chat_timestamp);
            convertView.setTag(holder);
            Log.d("Adapter", "Recreating fadeout.");
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.sender.setText(chatMsg.getSender() + ": ");
        holder.message.setText(chatMsg.getMessage());
        holder.timeStamp.setText(formatTimeStamp(chatMsg.getTimeStamp()));
        holder.chatMsg=chatMsg;
        setFadeOut3(convertView, chatMsg);
        return convertView;
    }

    @Override
    public int getCount() {
        return this.values.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position){
        if (position >= values.size()){ return -1; }
        return values.get(position).hashCode();
    }

    public void removeMsg(int loc){
        this.values.remove(loc);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage chatMsg){
        this.values.add(chatMsg);
        notifyDataSetChanged();
    }

    private void setFadeOut2(final View view, final ChatMessage message){
        Log.i("AdapterFade", "Caling Fade2");
        view.animate().setDuration(1000).setStartDelay(2000).alpha(0)
        .withEndAction(new Runnable() {
            @Override
            public void run() {
                if (values.contains(message))
                    values.remove(message);
                notifyDataSetChanged();
                view.setAlpha(1);
            }
        });
    }

    private void setFadeOut3(final View view, final ChatMessage message){
        Log.i("AdapterFade", "Caling Fade3");
        long elapsed = System.currentTimeMillis() - message.getTimeStamp();
        if (elapsed >= FADE_TIMEOUT){
            if (values.contains(message))
                values.remove(message);
            notifyDataSetChanged();
            return;
        }
        view.animate().setStartDelay(FADE_TIMEOUT - elapsed).setDuration(1500).alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        if (values.contains(message)){
                            values.remove(message);
                        }
                        notifyDataSetChanged();
                        view.setAlpha(1);
                    }
                });
    }


    private void setFadeOut(final View view, final ChatMessage message){
        long elapsed = System.currentTimeMillis() - message.getTimeStamp();
        if (elapsed >= FADE_TIMEOUT){
            if (values.contains(message))
                values.remove(message);
            notifyDataSetChanged();
            return;
        }

        view.setHasTransientState(true);
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(FADE_TIMEOUT - elapsed);
        fadeOut.setDuration(1000);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeOut);
        animation.setRepeatCount(1);

        view.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (values.contains(message)){
                    values.remove(message);
                }
                notifyDataSetChanged();
                view.setAlpha(1);
                view.setHasTransientState(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
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
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm.ss a");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        return formatter.format(calendar.getTime());
    }

}