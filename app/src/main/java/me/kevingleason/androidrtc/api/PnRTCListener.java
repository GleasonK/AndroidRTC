package me.kevingleason.androidrtc.api;

/**
 * Created by GleasonK on 7/20/15.
 */

import org.json.JSONObject;
import org.webrtc.MediaStream;

/**
 * Implement this interface to be notified of WebRTC events.
 * Abstract class with default behaviors of doing nothing.
 *
 * TODO: Specify the roles of endPoints, need to work out the parameters
 */
public abstract class PnRTCListener{
    public void onCallReady(String callId){}

    public void onConnected(String userId){}

    public void onStatusChanged(PnPeer peer){}

    public void onLocalStream(MediaStream localStream){}

    public void onAddRemoteStream(MediaStream remoteStream, PnPeer peer){}

    public void onRemoveRemoteStream(MediaStream remoteStream, PnPeer peer){}

    public void onMessage(PnPeer peer, Object message){}

    public void onDebug(PnRTCMessage message){}
}