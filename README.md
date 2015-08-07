# AndroidRTC
An example of native WebRTC on Android using PubNub's PnWebRTC API for signaling.

### Big News: PubNub Releases Android WebRTC Signaling API! 

![cover_img](http://kevingleason.me/AndroidRTC/assets/PnWebRTC.png)

This means that you can now create video chatting applications natively on Android in a breeze. Best of all, it is fully compatible with the [PubNub Javascript WebRTC SDK][JS SDK]! That's right, you are minutes away from creating your very own cross platform video-chatting application.

[__Get it now!__][PnWebRTC]

## The AndroidRTC Example App

This app shows how to accomplish signling on a standby channel to coordinate users, then hop into a video chat and use the PnWebRTC API for signaling. You can even make a call to the AndroidRTC App [from the web interface](http://kevingleason.me/AndroidRTC).

<img src="http://kevingleason.me/AndroidRTC/assets/Main.png" height=500 />

Users in this app layout must be subscribed to `username-stdby`, a standby channel. The Android side checks if a user is online by checking presence on the standby channel.

In this app, a call can be placed by sending a JSON packet to the user's standby channel:

    {"call_user":"UserName","call_time":currentTimeMillis}

Upon accepting the call, the answerer creates the SDP Offer, and video chat begins.

<img src="http://kevingleason.me/AndroidRTC/assets/Kevin.png" height=500 />

### Incoming Calls

AndroidRTC provides a good example of how to handle incoming calls.

<img src="http://kevingleason.me/AndroidRTC/assets/Incoming.png" height=500 />

### User Messages

This app also shows how to send custom user messages. These messages could be chat, game scores, and much more.

<img src="http://kevingleason.me/AndroidRTC/assets/Kurt.png" height=500 />


[PnWebRTC]:https://github.com/GleasonK/pubnub-android-webrtc
[JavaDoc]:http://kevingleason.me/pubnub-android-webrtc/
[AndroidRTC]:https://github.com/GleasonK/AndroidRTC/
[JS SDK]:https://github.com/stephenlb/webrtc-sdk