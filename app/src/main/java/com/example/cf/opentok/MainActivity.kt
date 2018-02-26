package com.example.cf.opentok

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import android.support.annotation.NonNull;
import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import android.widget.FrameLayout;


val API_KEY = "46067082"
val SESSION_ID = "1_MX40NjA2NzA4Mn5-MTUxOTY0MDA2MDc2OH4vR0dnc3hVRVk0a1VER2Q4WU1McjQrb2N-UH4"
val TOKEN = "T1==cGFydG5lcl9pZD00NjA2NzA4MiZzaWc9Y2VjZmMyN2NhMGY0NTRiMWM1NTdmMTBjMzQ5NzA3NmJhODA1NmI5NzpzZXNzaW9uX2lkPTFfTVg0ME5qQTJOekE0TW41LU1UVXhPVFkwTURBMk1EYzJPSDR2UjBkbmMzaFZSVmswYTFWRVIyUTRXVTFNY2pRcmIyTi1VSDQmY3JlYXRlX3RpbWU9MTUxOTY0MDA5MiZub25jZT0wLjQzMzgxODc4MDgwMjY0MTQ3JnJvbGU9c3Vic2NyaWJlciZleHBpcmVfdGltZT0xNTE5NjYxNjkxJmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9"
val LOG_TAG = MainActivity::class.java.simpleName
val RC_SETTINGS_SCREEN_PERM = 123

const val RC_VIDEO_APP_PERM = 124
class MainActivity : AppCompatActivity(), Session.SessionListener, PublisherKit.PublisherListener{
    var mSession: Session? = null

    var mPublisherViewContainer: FrameLayout? = null
    var mSubscriberViewContainer: FrameLayout? = null

    var mPublisher: Publisher? = null
    var mSubscriber: Subscriber? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private fun requestPermissions() {
        val perms = arrayOf(Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // initialize view objects from your layout
            mPublisherViewContainer =  findViewById(R.id.publisher_container);
            mSubscriberViewContainer = findViewById(R.id.subscriber_container);


            // initialize and connect to the session
            mSession = Session.Builder(this, API_KEY, SESSION_ID).build()
            mSession?.setSessionListener(this)
            mSession?.connect(TOKEN)


        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, *perms)
        }
    }
    // SessionListener methods

    override fun onConnected(session: Session) {
        Log.i(LOG_TAG, "Session Connected")

        mPublisher = Publisher.Builder(this).build()
        mPublisher?.setPublisherListener(this)

        mPublisherViewContainer?.addView(mPublisher?.view)
        mSession?.publish(mPublisher)
    }

    override fun onDisconnected(session: Session) {
        Log.i(LOG_TAG, "Session Disconnected")
    }

    override fun onStreamReceived(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Received")

        if (mSubscriber == null) {
            mSubscriber = Subscriber.Builder(this, stream).build()
            mSession?.subscribe(mSubscriber)
            mSubscriberViewContainer?.addView(mSubscriber?.view)
        }

    }

    override fun onStreamDropped(session: Session, stream: Stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer?.removeAllViews();
        }
    }

    override fun onError(session: Session, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.message)
    }
    // PublisherListener methods

    override fun onStreamCreated(publisherKit: PublisherKit, stream: Stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated")
    }

    override fun onStreamDestroyed(publisherKit: PublisherKit, stream: Stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed")
    }

    override fun onError(publisherKit: PublisherKit, opentokError: OpentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.message)
    }


}
