package me.kevingleason.androidrtc;

import android.util.Log;

import org.json.JSONObject;
import org.webrtc.MediaStream;

import me.kevingleason.androidrtc.api.PnPeer;
import me.kevingleason.androidrtc.api.PnRTCListener;
import me.kevingleason.androidrtc.api.PnRTCMessage;

/**
 * Created by GleasonK on 7/23/15.
 */
public class LogRTCListener extends PnRTCListener {
    @Override
    public void onCallReady(String callId) {
        Log.i("RTCListener", "OnCallReady - " + callId);
    }

    @Override
    public void onConnected(String userId) {
        Log.i("RTCListener", "OnConnected - " + userId);
    }

    @Override
    public void onStatusChanged(PnPeer peer) {
        Log.i("RTCListener", "OnStatusChanged - " + peer.toString());
    }

    @Override
    public void onLocalStream(MediaStream localStream) {
        Log.i("RTCListener", "OnLocalStream - " + localStream.toString());
    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, PnPeer peer) {
        Log.i("RTCListener", "OnAddRemoteStream - " + peer.toString());
    }

    @Override
    public void onRemoveRemoteStream(MediaStream remoteStream, PnPeer peer) {
        Log.i("RTCListener", "OnRemoveRemoteStream - " + peer.toString());
    }

    @Override
    public void onMessage(PnPeer peer, Object message) {
        Log.i("RTCListener", "OnMessage - " + ((JSONObject)message).toString());
    }

    @Override
    public void onDebug(PnRTCMessage message) {
        Log.i("RTCListener", "OnDebug - " + message.getMessage());
    }
}
