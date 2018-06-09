package com.tecorb.library;

import com.tecorb.library.Callbacks.RatingListener;



/**
 * Created by upasana on 7/6/18.
 */

public class AppRatingConfig {

    private int installedDays;
    private int launchedTimes;
    private RatingListener listener = null;


    public AppRatingConfig() {
        this(5, 8, null);
    }

    /***
     * @param installedDays Days installed before prompting user to rate
     * @param launchedTimes Number of times launched before prompting user to rate
     * @param listener AppRatingListener
     */
    public AppRatingConfig(int installedDays, int launchedTimes, RatingListener listener) {
        this.installedDays = installedDays;
        this.launchedTimes = launchedTimes;
        this.listener = listener;
    }

    public RatingListener getListener() {
        return listener;
    }

    public int getInstalledDays() {
        return installedDays;
    }

    public void setInstalledDays(int installedDays) {
        this.installedDays = installedDays;
    }

    public int getLaunchedTimes() {
        return launchedTimes;
    }

    public void setLaunchedTimes(int launchedTimes) {
        this.launchedTimes = launchedTimes;
    }

    public void setListener(RatingListener listener) {
        this.listener = listener;
    }
}
