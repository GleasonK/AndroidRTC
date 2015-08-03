package me.kevingleason.pnwebrtc;

import android.app.Activity;

import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;

import java.util.Iterator;
import java.util.List;


/**
 * Created by GleasonK on 7/20/15.
 */
public class PnRTCClient {
    private Activity activity;
    private PnSignalingParams pnSignalingParams;
    private Pubnub mPubNub;
    private PnPeerConnectionClient pcClient;
    private String UUID;


    public PnRTCClient(Activity activity, String pubKey, String subKey) {
        this.UUID = generateRandomNumber();
        this.activity = activity;
        this.mPubNub  = new Pubnub(pubKey, subKey);
        this.mPubNub.setUUID(this.UUID);
        this.pnSignalingParams = PnSignalingParams.defaultInstance();
        this.pcClient = new PnPeerConnectionClient(this.mPubNub, this.pnSignalingParams, new PnRTCListener() {});
    }

    public PnRTCClient(Activity activity, String pubKey, String subKey, String UUID) {
        this.UUID = UUID;
        this.activity = activity;
        this.mPubNub  = new Pubnub(pubKey, subKey);
        this.mPubNub.setUUID(this.UUID);
        this.pnSignalingParams = PnSignalingParams.defaultInstance();
        this.pcClient = new PnPeerConnectionClient(this.mPubNub, this.pnSignalingParams, new PnRTCListener() {});
    }

    public MediaConstraints pcConstraints() {
        return pnSignalingParams.pcConstraints;
    }

    public MediaConstraints videoConstraints() {
        return pnSignalingParams.videoConstraints;
    }

    public MediaConstraints audioConstraints() {
        return pnSignalingParams.audioConstraints;
    }

    public Pubnub getPubNub(){
        return this.mPubNub;
    }

    public void setSignalParams(PnSignalingParams signalParams){
        this.pnSignalingParams = signalParams;
    }

    /**
     * Need to attach mediaStream before you can connect.
     * @param mediaStream Not null local media stream
     */
    public void attachLocalMediaStream(MediaStream mediaStream){
        this.pcClient.setLocalMediaStream(mediaStream);
    }

    /**
     * Attach custom listener for callbacks!
     * @param listener
     */
    public void attachRTCListener(PnRTCListener listener){
        this.pcClient.setRTCListener(listener);
    }

    public void setMaxConnections(int max){
        this.pcClient.MAX_CONNECTIONS = max;
    }

    /**
     * Subscribe to a channel using PubNub to listen for calls.
     * @param channel The channel to listen on, your "phone number"
     */
    public void listenOn(String channel){
        this.pcClient.listenOn(channel);
    }

    /**
     * Connect with another user by their ID.
     * @param userId The user to establish a WebRTC connection with
     */
    public void connect(String userId){
        this.pcClient.connect(userId);
    }

    public void closeConnection(String userId){
        this.pcClient.closeConnection(userId);
    }

    public void closeAllConnections(){
        this.pcClient.closeAllConnections();
    }

    public void transmitUser(String userId, JSONObject message){
        JSONObject usrMsgJson = new JSONObject();
        try {
            usrMsgJson.put(PnRTCMessage.JSON_USERMSG, message);
            this.pcClient.transmitMessage(userId, usrMsgJson);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void transmitAll(JSONObject message){
        List<PnPeer> peerList = this.pcClient.getPeers();
        for(PnPeer p : peerList){
            transmitUser(p.getId(), message);
        }
    }

    private static String generateRandomNumber(){
        String areaCode = String.valueOf((int)(Math.random()*1000));
        String digits   = String.valueOf((int)(Math.random()*10000));
        while (areaCode.length() < 3) areaCode += "0";
        while (digits.length()   < 4) digits   += "0";
        return areaCode + "-" + digits;
    }

// Use these in PnRTCPhone wrapper object.
//    /**
//     * Call this method in Activity.onPause()
//     */
//    public void onPause() {
//        if(videoSource != null) videoSource.stop();
//    }
//
//    /**
//     * Call this method in Activity.onResume()
//     */
//    public void onResume() {
//        if(videoSource != null) videoSource.restart();
//    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        this.pcClient.closeAllConnections();
//        pcClient.pcFactory.dispose();
        this.mPubNub.unsubscribeAll();
//        if (this.pcClient.getLocalMediaStream() != null){
//            this.pcClient.getLocalMediaStream().dispose();
//        }
    }

}
