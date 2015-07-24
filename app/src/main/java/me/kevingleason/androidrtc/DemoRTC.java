package me.kevingleason.androidrtc;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

/**
 * Created by GleasonK on 7/17/15.
 */
public class DemoRTC extends Activity {
/*
    public static final String VIDEO_TRACK_ID        = "MyVideo";
    public static final String AUDIO_TRACK_ID        = "MyAudio";
    public static final String LOCAL_MEDIA_STREAM_ID = "MyStream";
    public static int x, y, width, height;


    @Override
    public void onCreate(){
        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();

        // Setup Video Stream Locally
        VideoCapturerAndroid.getDeviceCount();
        VideoCapturerAndroid.getNameOfFrontFacingDevice();
        VideoCapturerAndroid.getNameOfBackFacingDevice();
        VideoCapturer capturer = VideoCapturerAndroid.create("KevinsDroid");
        MediaConstraints videoConstraints = new MediaConstraints();
        // TODO: Setup Video Constraints
        VideoSource videoSource =  peerConnectionFactory.createVideoSource(capturer, videoConstraints);
        VideoTrack localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

        // Setup Audio Stream
        MediaConstraints audioConstraints = new MediaConstraints();
        // TODO Setup Audio Constraints
        AudioSource audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        AudioTrack localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.glview_call);
        VideoRendererGui.setView(videoView, runnable);
        VideoRenderer renderer = VideoRendererGui.createGui(x, y, width, height, scaleType, false);
        localVideoTrack.addRenderer(renderer);

        // Our Local MediaStream object
        MediaStream mediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);

        // TODO PUBNUB TO SET UP PEER CONNECTION
        PeerConnection peerConnection = peerConnectionFactory.createPeerConnection(
                iceServers,
                constraints,
                observer);
        peerConnection.addStream(mediaStream);
        peerConnection.createOffer();
        peerConnection.createAnswer();
    }

*/
}
