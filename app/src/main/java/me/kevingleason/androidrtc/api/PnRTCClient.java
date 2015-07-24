package me.kevingleason.androidrtc.api;

import android.app.Activity;

import com.pubnub.api.Pubnub;

import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;


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

    public void listenOn(String channel){
        this.pcClient.connect(channel);
    }

    private static String generateRandomNumber(){
        String areaCode = String.valueOf((int)(Math.random()*1000));
        String digits   = String.valueOf((int)(Math.random()*10000));
        while (areaCode.length() < 3) areaCode += "0";
        while (digits.length()   < 4) digits   += "0";
        return areaCode + "-" + digits;
    }

}
