package com.example.amansingh.timex;

import android.util.Log;

/**
 * Created by Aman Singh on 5/18/2018.
 */

public class test2
{
    private long startTime = 0;
    private boolean running = false;
    private long currentTime = 0;


    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }


    public void stop()
    {
        this.running = false;
    }

    public void pause()
    {
        this.currentTime = System.currentTimeMillis() - startTime;
        Log.i("mg",""+this.currentTime);
    }
    public void resume()
    {
        this.running = true;
        this.startTime = System.currentTimeMillis() - currentTime;
        Log.i("mg1",""+this.startTime);
    }


    // elaspsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return ((System.currentTimeMillis() - startTime)) % 1000 ;
        }
        return 0;
    }


    // elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            return ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return 0;
    }

    public long getElapsedtimemin()
    {
        if (running) {
            return (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return 0;
    }
}
