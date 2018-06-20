package com.tecorb.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tecorb.library.Callbacks.RatingListener;
import com.tecorb.library.databinding.DialogBinding;

import java.util.Date;

/**
 * Created by upasana on 19/6/18.
 */

public class MyAppRating {

    public interface ConditionTrigger {
        boolean shouldShow();

    }

    private final Context mContext;
    private final SharedPreferences mPreferences;
    private ConditionTrigger mCondition;
    private Dialog dialog;

    private static final String PREFS_NAME = "erd_rating";
    private static final String KEY_WAS_RATED = "KEY_WAS_RATED";
    private static final String KEY_NEVER_REMINDER = "KEY_NEVER_REMINDER";
    private static final String KEY_FIRST_REGISTER_DATE = "KEY_FIRST_HIT_DATE";
    private static final String KEY_LAUNCH_TIMES = "KEY_LAUNCH_TIMES";

    DialogBinding binding;
    RatingListener callback;
    int launchTimes;

    public MyAppRating(Context context, RatingListener callback) {
        this.callback = callback;
        mContext = context;
        mPreferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public void onStart() {
        if (didRate() || didNeverReminder()) return;

        launchTimes = mPreferences.getInt(KEY_LAUNCH_TIMES, 0);
        long firstDate = mPreferences.getLong(KEY_FIRST_REGISTER_DATE, -1L);

        if (firstDate == -1L) {
            registerDate();
        }

        registerHitCount(++launchTimes);
    }

    public void showAnyway() {
        //tryShow(mContext);
    }

    public void showIfNeeded(String title, String rateNowText, String reminedMeLaterText,
                             String noThanksText, int backGroundColor) {
        if (mCondition != null) {
            if (mCondition.shouldShow())
                tryShow(mContext,title,rateNowText,reminedMeLaterText,noThanksText,backGroundColor);
        } else {

                if (shouldShow())
                    tryShow(mContext, title, rateNowText, reminedMeLaterText, noThanksText, backGroundColor);
               // ratingDialog(mContext,title,rateNowText,reminedMeLaterText,noThanksText,backGroundColor);
        }
    }

    public void neverReminder() {

        if (callback != null) {
            callback.neverReminder();
            mPreferences.edit().putBoolean(KEY_NEVER_REMINDER, true).apply();
        }
    }

    public void rateNow() {
        if (callback != null) {
            callback.rateNow();

            String appPackage = mContext.getPackageName();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            mPreferences.edit().putBoolean(KEY_WAS_RATED, true).apply();
        }

    }

    public void remindMeLater() {

        if (callback != null) {
            callback.remindMeLater();

            registerHitCount(launchTimes);
            registerDate();
        }

    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public boolean didRate() {
        return mPreferences.getBoolean(KEY_WAS_RATED, false);
    }

    public boolean didNeverReminder() {
        return mPreferences.getBoolean(KEY_NEVER_REMINDER, false);
    }

    public void setConditionTrigger(ConditionTrigger condition) {
        mCondition = condition;
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }

    public void tryShow(Context context,String title, String rateNowText, String reminedMeLaterText,
                        String noThanksText, int backGroundColor) {
        if (isShowing())
            return;
        try {
            dialog = null;
                ratingDialog(context, title, rateNowText, reminedMeLaterText, noThanksText, backGroundColor);

        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }

    private boolean shouldShow() {


        if (mPreferences.getBoolean(KEY_NEVER_REMINDER, false))
            return false;
        if (mPreferences.getBoolean(KEY_WAS_RATED, false))
            return false;

        int launchTimes = mPreferences.getInt(KEY_LAUNCH_TIMES, 0);
        long firstDate = mPreferences.getLong(KEY_FIRST_REGISTER_DATE, 0L);
        long today = new Date().getTime();
        int maxLaunchTimes = mContext.getResources().getInteger(R.integer.launch_times);
        int maxDaysAfter = mContext.getResources().getInteger(R.integer.max_days_after);

        if (launchTimes==1){
            return true;
        }else {
            return daysBetween(firstDate, today) > maxDaysAfter;
        }

        /*if (daysBetween(firstDate, today) > maxDaysAfter || launchTimes > maxLaunchTimes) {
            return true;
        }
        return false;*/

        //return daysBetween(firstDate, today) > maxDaysAfter || launchTimes > maxLaunchTimes;


    }

    private void registerHitCount(int hitCount) {
        mPreferences
                .edit()
                .putInt(KEY_LAUNCH_TIMES, Math.min(hitCount, Integer.MAX_VALUE))
                .apply();
    }

    private void registerDate() {
        Date today = new Date();
        mPreferences
                .edit()
                .putLong(KEY_FIRST_REGISTER_DATE, today.getTime())
                .apply();
    }

    private long daysBetween(long firstDate, long lastDate) {
        return (lastDate - firstDate) / (1000 * 60 * 60 * 24);
    }

    public void ratingDialog(final Context context, String title, String rateNowText, String reminedMeLaterText,
                             String noThanksText, int backGroundColor) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.dialog, null, false);
        dialog.setContentView(binding.getRoot());
        dialog.setCancelable(false);


        setTitleText(title);
        setRateNowThanks(rateNowText);
        setReminedMeLaterText(reminedMeLaterText);
        setNoThanksText(noThanksText);
        setBackGroundColor(backGroundColor);

        binding.tvRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateNow();

            }
        });

        binding.tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                remindMeLater();

            }
        });

        binding.tvNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                neverReminder();

            }
        });

        dialog.show();


    }

    public void dissmissDialog() {

        if (dialog != null || dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private void setBackGroundColor(int color) {
        binding.cardview.setCardBackgroundColor(color);
    }

    private void setImagesResources(int headerImage) {

        binding.ivImage.setImageResource(headerImage);
    }

    private void setNoThanksText(String noThanksText) {

        binding.tvNoThanks.setText(noThanksText);
    }


    private void setReminedMeLaterText(String reminedMeLaterText) {

        binding.tvLater.setText(reminedMeLaterText);
    }


    private void setRateNowThanks(String rateNowText) {
        binding.tvRateNow.setText(rateNowText);
    }

    private void setTitleText(String message) {
        binding.tvTitle.setText(message);

    }
}
