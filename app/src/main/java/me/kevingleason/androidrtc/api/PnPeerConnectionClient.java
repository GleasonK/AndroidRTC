package me.kevingleason.androidrtc.api;

import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     Created by GleasonK on 7/20/15 for PubNub WebRTC Signaling.
 *     PubNub '15
 *     Boston College '16
 * </p>
 * {@link me.kevingleason.androidrtc.api.PnPeerConnectionClient} is used to manage peer connections.
 */
public class PnPeerConnectionClient {
    private int startBitrate;
    private boolean useFrontFacingCamera = true;
    private SessionDescription localSdp  = null; // either offer or answer SDP
    private MediaStream localMediaStream = null;
    PeerConnectionFactory pcFactory;
    PnRTCListener mRtcListener;
    PnSignalingParams signalingParams;

    private Pubnub mPubNub;
    private PnRTCReceiver mSubscribeReceiver;
    private Map<String,PnAction> commandMap;
    private Map<String,PnPeer> peers;
    private String id;

    public PnPeerConnectionClient(Pubnub pubnub, PnSignalingParams signalingParams, PnRTCListener rtcListener){
        this.mPubNub = pubnub;
        this.signalingParams = signalingParams;
        this.mRtcListener = rtcListener;
        this.pcFactory = new PeerConnectionFactory(); // TODO: Check it allowed, else extra param
        this.peers = new HashMap<String, PnPeer>();
        init();
    }

    private void init(){
        this.commandMap = new HashMap<String, PnAction>();
        this.commandMap.put(CreateOfferAction.TRIGGER,     new CreateOfferAction());
        this.commandMap.put(CreateAnswerAction.TRIGGER,    new CreateAnswerAction());
        this.commandMap.put(SetRemoteSDPAction.TRIGGER,    new SetRemoteSDPAction());
        this.commandMap.put(AddIceCandidateAction.TRIGGER, new AddIceCandidateAction());
        mSubscribeReceiver = new PnRTCReceiver();
    }

    boolean connect(String myId){  // Todo: return success?
        if (localMediaStream==null){       // Not true for streaming?
            mRtcListener.onDebug(new PnRTCMessage("Need to add media stream before you can connect."));
            return false;
        }
        if (this.id != null){  // Prevent listening on multiple channels.
            mRtcListener.onDebug(new PnRTCMessage("Already listening on " + this.id + ". Cannot have multiple connections."));
            return false;
        }
        this.id = myId;
        subscribe(myId);
        return true;
    }

    public void setRTCListener(PnRTCListener listener){
        this.mRtcListener = listener;
    }

    private void subscribe(String channel){
        try {
            mPubNub.subscribe(channel, this.mSubscribeReceiver);
        } catch (PubnubException e){
            e.printStackTrace();
        }
    }

    public void setLocalMediaStream(MediaStream localStream){
        this.localMediaStream = localStream;
        mRtcListener.onLocalStream(localStream);
    }

    public MediaStream getLocalMediaStream(){
        return this.localMediaStream;
    }

    private PnPeer addPeer(String id) {
        PnPeer peer = new PnPeer(id, this);
        peers.put(id, peer);
        return peer;
    }

    PnPeer removePeer(String id) {
        PnPeer peer = peers.get(id);
        peer.pc.close();
        return peers.remove(peer.id);
    }

    /**
     * Send SDP Offers/Answers nd ICE candidates to peers.
     * @param toID The id or "number" that you wish to transmit a message to.
     * @param packet The JSON data to be transmitted
     */
    void transmitMessage(String toID, JSONObject packet){
        if (this.id==null){ // Not logged in. Put an error in the debug cb.
            mRtcListener.onDebug(new PnRTCMessage("Cannot transmit before calling Client.connect"));
        }
        try {
            JSONObject message = new JSONObject();
            message.put(PnRTCMessage.JSON_PACKET, packet);
            message.put(PnRTCMessage.JSON_ID, ""); //Todo: session id, unused in js SDK?
            message.put(PnRTCMessage.JSON_NUMBER, this.id);
            this.mPubNub.publish(toID, message, new Callback() {  // Todo: reconsider callback.
                @Override
                public void successCallback(String channel, Object message, String timetoken) {
                    mRtcListener.onDebug(new PnRTCMessage((JSONObject)message));
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    mRtcListener.onDebug(new PnRTCMessage(error.errorObject));
                }
            });
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private interface PnAction{
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateOfferAction implements PnAction{
        public static final String TRIGGER = "init";
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("COCommand","CreateOfferCommand");
            PnPeer peer = peers.get(peerId);
            peer.setDialed(true);
            peer.setType(PnPeer.TYPE_ANSWER);
            peer.pc.createOffer(peer, signalingParams.pcConstraints);
        }
    }

    private class CreateAnswerAction implements PnAction{
        public static final String TRIGGER = "offer";
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("CACommand","CreateAnswerCommand");
            PnPeer peer = peers.get(peerId);
            peer.setType(PnPeer.TYPE_OFFER);
            peer.setStatus(PnPeer.STATUS_CONNECTED);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, signalingParams.pcConstraints);
        }
    }

    private class SetRemoteSDPAction implements PnAction{
        public static final String TRIGGER = "answer";
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("SRSCommand","SetRemoteSDPCommand");
            PnPeer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.getString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateAction implements PnAction{
        public static final String TRIGGER = "candidate";
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("AICCommand","AddIceCandidateCommand");
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.getString("sdpMid"),
                        payload.getInt("sdpMLineIndex"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    private class PnUserMessageAction implements PnAction{
        public static final String TYPE = "usermsg";
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Log.d("PnUserMessage","AddIceCandidateCommand");
            PnPeer peer = peers.get(peerId);
            mRtcListener.onMessage(peer, payload);
        }
    }

    private class PnRTCReceiver extends Callback {

        @Override
        public void connectCallback(String channel, Object message) { // Todo, onSubscribe Actions?
            mRtcListener.onDebug(new PnRTCMessage(((JSONArray) message).toString()));
            // Maybe an onConnect?
            mRtcListener.onConnected(channel);
        }

        @Override
        public void successCallback(String channel, Object message) {
            if (!(message instanceof JSONObject)) return; // Ignore if not valid JSON.
            JSONObject jsonMessage = (JSONObject) message;
            mRtcListener.onDebug(new PnRTCMessage(jsonMessage));
            try {
                String peerId     = jsonMessage.getString(PnRTCMessage.JSON_NUMBER);
                JSONObject packet = jsonMessage.getJSONObject(PnRTCMessage.JSON_PACKET);
                PnPeer peer;
                if (!peers.containsKey(peerId)){
                    // Possibly threshold number of allowed users
                    peer = addPeer(peerId);
                    peer.pc.addStream(localMediaStream);
                } else {
                    peer = peers.get(peerId);
                }
                if (peer.getStatus().equals(PnPeer.STATUS_DISCONNECTED)) return; // Do nothing if disconnected.
                if (packet.has(PnRTCMessage.JSON_USERMSG)) {
                    commandMap.get(PnUserMessageAction.TYPE).execute(peerId,packet);
                    return;
                }
                if (packet.has(PnRTCMessage.JSON_THUMBNAIL) || packet.has(PnRTCMessage.JSON_HANGUP)) {
                    return;   // No handler for thumbnail or hangup yet, will be separate controller callback
                }
                if (packet.has(PnRTCMessage.JSON_SDP)) {
                    if(!peer.received) {
                        peer.setReceived(true);
                        mRtcListener.onDebug(new PnRTCMessage("SDP - " + peer.toString()));
                        // Todo: reveivercb(peer);
                    }
                    String type = packet.getString(PnRTCMessage.JSON_TYPE);
                    commandMap.get(type).execute(peerId, packet);
                    return;
                }
                if (packet.has(PnRTCMessage.JSON_ICE)){
                    commandMap.get(AddIceCandidateAction.TRIGGER).execute(peerId,packet);
                }
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            super.errorCallback(channel, error);
        }

    }

}
