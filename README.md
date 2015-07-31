# AndroidRTC
An example of WebRTC natively on Android using PubNub for signaling.

### Coming Soon

PubNub WebRTC API for Android is in the works. Need to clean up callbacks and
modularize the `api` package. Compatible with PubNub Javascript WebRTC SDK. 

### Standard JSON

If you carefully create your JSON packets, you can coordinate web and Android interfaces.

#### Placing A Call

Users in my app layout must be subscribed to `username-stdby`, a standby channel. The Android side
checks if a user is online by checking presence on the standby channel.

In this app, a call can be placed by sending a JSON packet to the user's standby channel:

    {"call_user":"UserName","call_time":currentTimeMillis}

Upon accepting the call, the answerer creates the SDP Offer.