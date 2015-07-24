package me.kevingleason.androidrtc;

import android.app.Activity;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoCapturerAndroid;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoRendererGui;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import me.kevingleason.androidrtc.api.PnPeer;
import me.kevingleason.androidrtc.api.PnRTCClient;


public class VideoChatActivity extends Activity {
    public static final String VIDEO_TRACK_ID = "videoPN";
    public static final String AUDIO_TRACK_ID = "audioPN";
    public static final String LOCAL_MEDIA_STREAM_ID = "localStreamPN";

    public static final String PUB_KEY = "pub-c-561a7378-fa06-4c50-a331-5c0056d0163c"; // Your Pub Key
    public static final String SUB_KEY = "sub-c-17b7db8a-3915-11e4-9868-02ee2ddab7fe"; // Your Sub Key

    private PnRTCClient pnRTCClient;
    private MediaConstraints sdpMediaConstraints;
    private PeerConnectionFactory pcFactory;
    private VideoTrack localVideoTrack;
    private VideoRenderer.Callbacks thumbRenderer;
    private VideoRenderer.Callbacks mainRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(displaySize);

        // First, we initiate the PeerConnectionFactory with our application context and some options.
        PeerConnectionFactory.initializeAndroidGlobals(
                this,  // Context
                true,  // Audio Enabled
                true,  // Video Enabled
                true,  // Hardware Acceleration Enabled
                null); // Render EGL Context

        pcFactory = new PeerConnectionFactory();

        this.pnRTCClient = new PnRTCClient(this, PUB_KEY,SUB_KEY,"Kevin"); //TODO Fix Params.

        // Returns the number of cams & front/back face device name
        int camNumber = VideoCapturerAndroid.getDeviceCount();
        String frontFacingCam = VideoCapturerAndroid.getNameOfFrontFacingDevice();
        String backFacingCam  = VideoCapturerAndroid.getNameOfBackFacingDevice();

        // Creates a VideoCapturerAndroid instance for the device name
        VideoCapturerAndroid capturer = VideoCapturerAndroid.create(frontFacingCam);

        // First create a Video Source, then we can make a Video Track
        VideoSource videoSource = pcFactory.createVideoSource(capturer, this.pnRTCClient.videoConstraints());
        localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);

        // First we create an AudioSource then we can create our AudioTrack
        AudioSource audioSource = pcFactory.createAudioSource(this.pnRTCClient.audioConstraints());
        AudioTrack localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);

        // To create our VideoRenderer, we can use the included VideoRendererGui for simplicity
        // First we need to set the GLSurfaceView that it should render to
        GLSurfaceView videoView = (GLSurfaceView) findViewById(R.id.gl_surface);

        // Then we set that view, and pass a Runnable to run once the surface is ready
        VideoRendererGui.setView(videoView, null);

        // Now that VideoRendererGui is ready, we can get our VideoRenderer  TODO: MAYBE FIX THIS
        mainRender    = VideoRendererGui.create(0, 0, 100, 100, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        thumbRenderer = VideoRendererGui.create(70, 5, 25, 25, VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false);
        try {
//            localVideoTrack.addRenderer(mainRender);
        }
        catch (Exception e){ e.printStackTrace(); }


        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        // We start out with an empty MediaStream object,
        // created with help from our PeerConnectionFactory
        // Note that LOCAL_MEDIA_STREAM_ID can be any string
        MediaStream mediaStream = pcFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

        // Now we can add our tracks.
        mediaStream.addTrack(localVideoTrack);
        mediaStream.addTrack(localAudioTrack);

        this.pnRTCClient.attachLocalMediaStream(mediaStream);
        this.pnRTCClient.attachRTCListener(new DemoRTCListener());
        this.pnRTCClient.listenOn("Kevin");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DemoRTCListener extends LogRTCListener {
        @Override
        public void onLocalStream(MediaStream localStream) {
            super.onLocalStream(localStream); // Will log values
            VideoChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

        @Override
        public void onAddRemoteStream(final MediaStream remoteStream, final PnPeer peer) {
            super.onAddRemoteStream(remoteStream, peer); // Will log values
            VideoChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VideoChatActivity.this,"Connected to " + peer.getId(), Toast.LENGTH_SHORT).show();
                    try {
                        if(remoteStream.audioTracks.size()==0 || remoteStream.videoTracks.size()==0) return;
//                        localVideoTrack.removeRenderer(mainRender);
                        localVideoTrack.addRenderer(new VideoRenderer(thumbRenderer));
                        remoteStream.videoTracks.get(0).addRenderer(new VideoRenderer(mainRender));
                    }
                    catch (Exception e){ e.printStackTrace(); }
                }
            });
        }
    }
}
