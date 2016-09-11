package com.scripton.in.test;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.view.R5VideoView;


public class test_vide extends AppCompatActivity implements R5ConnectionListener {

    protected R5Stream subscribe;
    Thread retryThread;
    public static boolean swapped = false;

    public test_vide() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_vide);
        if (subscribe == null) {

            Resources res = getResources();

            //Create the configuration from the values.xml
            R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP, res.getString(R.string.domain), res.getInteger(R.integer.port), res.getString(R.string.context), 0.5f);
            R5Connection connection = new R5Connection(config);

            //setup a new stream using the connection
            subscribe = new R5Stream(connection);

            subscribe.client = this;
            subscribe.setListener(this);

            //show all logging
            subscribe.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

            //find the view and attach the stream
            R5VideoView r5VideoView = (R5VideoView) findViewById(R.id.video);
            r5VideoView.attachStream(subscribe);
            r5VideoView.showDebugView(res.getBoolean(R.bool.debugView));

            subscribe.play(getStream1());

        }

    }

    protected String getStream1() {
        if (!swapped) return getString(R.string.stream1);
        else return getString(R.string.stream2);
    }


    @Override
    public void onConnectionEvent(R5ConnectionEvent r5ConnectionEvent) {

        if (r5ConnectionEvent == R5ConnectionEvent.CLOSE) {
            retryThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!Thread.interrupted() && subscribe != null) {

                        try {
                            Thread.sleep(8000);

                            subscribe.stop();
                            subscribe.play(getStream1());
                        } catch (Exception e) {
                            System.out.println("failed to reconnect");
                        }
                    }
                }
            });
            retryThread.start();
        }
    }


    @Override
    public void onStop() {

        super.onStop();

        if (subscribe != null)
            subscribe.stop();

        subscribe = null;

    }
}
