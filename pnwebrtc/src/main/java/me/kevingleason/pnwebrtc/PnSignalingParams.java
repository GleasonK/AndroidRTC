package me.kevingleason.pnwebrtc;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Define {@link org.webrtc.MediaConstraints} and {@link org.webrtc.PeerConnection.IceServer} for WebRTC PeerConnections</h1>
 * <pre>
 * Author:  Kevin Gleason - Boston College '16
 * File:    PnSignalingParams.java
 * Date:    7/20/15
 * Use:     Hold the signaling parameters of a WebRTC PeerConnection
 * &copy; 2009 - 2015 PubNub, Inc.
 * </pre>
 * <p>IceServers allow Trickling, so they are not final.</p>
 *
 */
public class PnSignalingParams {
    public List<PeerConnection.IceServer> iceServers;
    public final MediaConstraints pcConstraints;
    public final MediaConstraints videoConstraints;
    public final MediaConstraints audioConstraints;

    public PnSignalingParams(
            List<PeerConnection.IceServer> iceServers,
            MediaConstraints pcConstraints,
            MediaConstraints videoConstraints,
            MediaConstraints audioConstraints) {
        this.iceServers       = (iceServers==null)       ? defaultIceServers()       : iceServers;
        this.pcConstraints    = (pcConstraints==null)    ? defaultPcConstraints()    : pcConstraints;
        this.videoConstraints = (videoConstraints==null) ? defaultVideoConstraints() : videoConstraints;
        this.audioConstraints = (audioConstraints==null) ? defaultAudioConstraints() : audioConstraints;
    }

    /**
     * Default Ice Servers, but specified parameters.
     * @param pcConstraints
     * @param videoConstraints
     * @param audioConstraints
     */
    public PnSignalingParams(
            MediaConstraints pcConstraints,
            MediaConstraints videoConstraints,
            MediaConstraints audioConstraints) {
        this.iceServers       = PnSignalingParams.defaultIceServers();
        this.pcConstraints    = (pcConstraints==null)    ? defaultPcConstraints()    : pcConstraints;
        this.videoConstraints = (videoConstraints==null) ? defaultVideoConstraints() : videoConstraints;
        this.audioConstraints = (audioConstraints==null) ? defaultAudioConstraints() : audioConstraints;
    }

    /**
     * Default media params, but specified Ice Servers
     * @param iceServers
     */
    public PnSignalingParams(List<PeerConnection.IceServer> iceServers) {
        this.iceServers       = iceServers; //defaultIceServers();
        this.pcConstraints    = defaultPcConstraints();
        this.videoConstraints = defaultVideoConstraints();
        this.audioConstraints = defaultAudioConstraints();
//        addIceServers(iceServers);
    }

    /**
     * Default media params and ICE servers.
     */
    public PnSignalingParams() {
        this.iceServers       = defaultIceServers();
        this.pcConstraints    = defaultPcConstraints();
        this.videoConstraints = defaultVideoConstraints();
        this.audioConstraints = defaultAudioConstraints();
    }

    /**
     * The default parameters for media constraints. Might have to tweak in future.
     * @return default parameters
     */
    public static PnSignalingParams defaultInstance() {
        MediaConstraints pcConstraints    = PnSignalingParams.defaultPcConstraints();
        MediaConstraints videoConstraints = PnSignalingParams.defaultVideoConstraints();
        MediaConstraints audioConstraints = PnSignalingParams.defaultAudioConstraints();
        List<PeerConnection.IceServer> iceServers = PnSignalingParams.defaultIceServers();
        return new PnSignalingParams(iceServers, pcConstraints, videoConstraints, audioConstraints);
    }

    private static MediaConstraints defaultPcConstraints(){
        MediaConstraints pcConstraints = new MediaConstraints();
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        return pcConstraints;
    }

    private static MediaConstraints defaultVideoConstraints(){
        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxWidth","1280"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("maxHeight","720"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minWidth", "640"));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair("minHeight","480"));
        return videoConstraints;
    }

    private static MediaConstraints defaultAudioConstraints(){
        MediaConstraints audioConstraints = new MediaConstraints();
        return audioConstraints;
    }

    public static List<PeerConnection.IceServer> defaultIceServers(){
        List<PeerConnection.IceServer> iceServers = new ArrayList<PeerConnection.IceServer>(25);
        iceServers.add(new PeerConnection.IceServer("stun:stun.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.services.mozilla.com"));
        iceServers.add(new PeerConnection.IceServer("turn:turn.bistri.com:80", "homeo", "homeo"));
        iceServers.add(new PeerConnection.IceServer("turn:turn.anyfirewall.com:443?transport=tcp", "webrtc", "webrtc"));

        // Extra Defaults - 19 STUN servers + 4 initial = 23 severs (+2 padding) = Array cap 25
        iceServers.add(new PeerConnection.IceServer("stun:stun1.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stun2.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stun3.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:stun4.l.google.com:19302"));
        iceServers.add(new PeerConnection.IceServer("stun:23.21.150.121"));
        iceServers.add(new PeerConnection.IceServer("stun:stun01.sipphone.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.ekiga.net"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.fwdnet.net"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.ideasip.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.iptel.org"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.rixtelecom.se"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.schlund.de"));
        iceServers.add(new PeerConnection.IceServer("stun:stunserver.org"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.softjoys.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.voiparound.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.voipbuster.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.voipstunt.com"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.voxgratia.org"));
        iceServers.add(new PeerConnection.IceServer("stun:stun.xten.com"));

        return iceServers;
    }

    /**
     * Append default servers to the end of given list and set as iceServers instance variable
     * @param iceServers List of iceServers
     */
    public void addIceServers(List<PeerConnection.IceServer> iceServers){
        if(this.iceServers!=null) {
            iceServers.addAll(this.iceServers);
        }
        this.iceServers = iceServers;
    }

    /**
     * Instantiate iceServers if they are not already, and add Ice Server to beginning of list.
     * @param iceServers Ice Server to add
     */
    public void addIceServers(PeerConnection.IceServer iceServers){
        if (this.iceServers == null){
            this.iceServers = new ArrayList<PeerConnection.IceServer>();
        }
        this.iceServers.add(0, iceServers);
    }
}

