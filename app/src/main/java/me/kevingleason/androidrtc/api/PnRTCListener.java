package me.kevingleason.androidrtc.api;

import org.webrtc.MediaStream;

/**Todo: Think about parameters.
 * <p>
 *     Created by GleasonK on 7/20/15 for PubNub WebRTC Signaling.
 *     PubNub '15
 *     Boston College '16
 * </p>
 * Implement this interface to be notified of WebRTC events.
 * It is an abstract class with default behaviors of doing nothing.
 * Use a PnRTCListener to implement the various callbacks of your WebRTC application.
 */
public abstract class PnRTCListener{
    public void onCallReady(String callId){} // TODO: Maybe not needed?

    /**
     * Called in {@link com.pubnub.api.Pubnub} object's subscribe connected callback.
     * Means that you are ready to receive calls.
     * @param userId The channel you are subscribed to, the userId you may be called on.
     */
    public void onConnected(String userId){}

    /**
     * Peer status changed. {@link me.kevingleason.androidrtc.api.PnPeer} status changed, can be
     * INITIALIZING, CONNECTING, CONNECTED, or DISCONNECTED.
     * @param peer The peer object, can use to check peer.getStatus()
     */
    public void onPeerStatusChanged(PnPeer peer){}

    /**
     * Called in {@link me.kevingleason.androidrtc.api.PnPeerConnectionClient} when setLocalStream
     * is invoked.
     * @param localStream The users local stream from Android's front or back camera.
     */
    public void onLocalStream(MediaStream localStream){}

    /**
     * Called when a remote stream is added in the {@link org.webrtc.PeerConnection.Observer}
     * in {@link me.kevingleason.androidrtc.api.PnPeer}.
     * @param remoteStream The remote stream that was added
     * @param peer The peer that added the remote stream
     *             Todo: Maybe not the right peer?
     */
    public void onAddRemoteStream(MediaStream remoteStream, PnPeer peer){}

    /**
     * Called in the {@link org.webrtc.PeerConnection.Observer} implemented
     * by {@link me.kevingleason.androidrtc.api.PnPeer}.
     * @param remoteStream The stream that was removed by your peer
     * @param peer The peer that removed the stream.
     */
    public void onRemoveRemoteStream(MediaStream remoteStream, PnPeer peer){}

    /**
     * Called when a user message is send via {@link com.pubnub.api.Pubnub} object.
     * @param peer The peer who sent the message
     * @param message The {@link org.json.JSONObject} message sent by the user.
     */
    public void onMessage(PnPeer peer, Object message){}

    /**
     * A helpful debugging callback for testing and developing your app.
     * @param message The {@link me.kevingleason.androidrtc.api.PnRTCMessage} debug message.
     */
    public void onDebug(PnRTCMessage message){}
}