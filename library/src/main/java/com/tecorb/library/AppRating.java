package com.tecorb.library;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tecorb.library.Callbacks.RatingListener;
import com.tecorb.library.databinding.DialogBinding;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by upasana on 6/6/18.
 */

public class AppRating {

    private static final String TAG = "AppRating";
    private static final String PREF_NAME = "AppRating";
    private static final String KEY_INSTALL_DATE = "rating_install_date";
    private static final String KEY_LAUNCH_TIMES = "rating_launch_times";
    private static final String NEVER_SHOW_AGAIN = "rating_opt_out";
    private static final String KEY_ASK_LATER_DATE = "rating_ask_later_date";

    private Date mInstallDate = new Date();
    private int mLaunchTimes = 0;
    private boolean mNeverShowAgain = false;
    private Date mAskLaterDate = new Date();


    public static Dialog dialog = null;
    private static Context context;
    private RatingListener callbackListener;

    AppRatingConfig config = new AppRatingConfig();
    public static final boolean K_DEBUG = false;
    Typeface textFont;
    DialogBinding binding;


    public AppRating(Context context, AppRatingConfig config) {
        this.context = context;
        this.config = config;
        ratingSharedPref();

    }

    private void ratingSharedPref() {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        // If it is the first launch, save the date in shared preference.
        if (pref.getLong(KEY_INSTALL_DATE, 0) == 0L) {
            storeInstallDate(context, editor);
        }
        // Increment launch times
        int launchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        launchTimes++;
        editor.putInt(KEY_LAUNCH_TIMES, launchTimes);
        log("Launch times; " + launchTimes);

        editor.apply();
        mInstallDate = new Date(pref.getLong(KEY_INSTALL_DATE, 0));
        mLaunchTimes = pref.getInt(KEY_LAUNCH_TIMES, 0);
        mNeverShowAgain = pref.getBoolean(NEVER_SHOW_AGAIN, false);
        mAskLaterDate = new Date(pref.getLong(KEY_ASK_LATER_DATE, 0));

        printStatus(context);
    }

    private void printStatus(Context context) {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        log("*** NcAppRatingUtil Status ***");
        log("Install Date: " + new Date(pref.getLong(KEY_INSTALL_DATE, 0)));
        log("Launch Times: " + pref.getInt(KEY_LAUNCH_TIMES, 0));
        log("Opt out: " + pref.getBoolean(NEVER_SHOW_AGAIN, false));


    }

    private void log(String message) {
        if (K_DEBUG) {
            Log.d(TAG, message);

        }
    }

    private void storeInstallDate(Context context, SharedPreferences.Editor editor) {

        Date installDate = new Date();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            PackageManager packMan = context.getPackageManager();
            try {
                PackageInfo pkgInfo = packMan.getPackageInfo(context.getPackageName(), 0);
                installDate = new Date(pkgInfo.firstInstallTime);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        editor.putLong(KEY_INSTALL_DATE, installDate.getTime());
        log("First install: " + installDate.toString());


    }


    public boolean appRatingDialog(Context context, String title, String rateNowText, String reminedMeLaterText,
                                   String noThanksText, int backGroundColor) {

        if (showDialogNeeded()) {

            ratingDialog(context, title, rateNowText, reminedMeLaterText, noThanksText, backGroundColor);

            return true;

        } else {

            return false;
        }

    }

    public boolean showDialogNeeded() {


        if (mLaunchTimes <= config.getLaunchedTimes()) {

            if (mNeverShowAgain) {
                return false;
            }

            long threshold = config.getInstalledDays() * 24 * 60 * 60 * 1000L;    // msec
            if (new Date().getTime() - mInstallDate.getTime() >= threshold &&
                    new Date().getTime() - mAskLaterDate.getTime() >= threshold) {
                return true;
            }

        }

        return false;


    }

    private boolean showDialogIfNeeded() {

        if (mNeverShowAgain) {
            return false;
        } else {
            if (mLaunchTimes >= config.getLaunchedTimes()) {
                return true;
            }
            long threshold = config.getInstalledDays() * 24 * 60 * 60 * 1000L;    // msec
            if (new Date().getTime() - mInstallDate.getTime() >= threshold &&
                    new Date().getTime() - mAskLaterDate.getTime() >= threshold) {
                return true;
            }
            return false;
        }

    }

    public void ratingDialog(final Context context, String title, String rateNowText,
                             String reminedMeLaterText, String noThanksText, int color) {

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
        setBackGroundColor(color);

        binding.tvRateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateNow(context);


            }
        });

        binding.tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tvLater(context);

            }
        });

        binding.tvNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noThanks(context);

            }
        });

        dialog.show();


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

    public void dissmissDialog() {

        if (dialog != null || dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    private void rateNow(Context context) {

        if (config.getListener() != null) {
            config.getListener().rateNow();
        }

        rateInPlaystore(context);

    }

    private void rateInPlaystore(Context context) {

        String appPackage = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    private void tvLater(Context context) {


        if (config.getListener() != null) {
            config.getListener().remindMeLater();

            clearSharedPreferences(context);
            storeAskLaterDate(context);
        }

    }

    public void noThanks(Context context) {

        if (config.getListener() != null) {
            config.getListener().noThanks();
        }
        setNeverShowAgain(context, true);

    }

    public void storeAskLaterDate(Context context) {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(KEY_ASK_LATER_DATE, System.currentTimeMillis());
        editor.apply();


    }

    public long getLastUpdateDialogShowTime() {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getLong(KEY_ASK_LATER_DATE, System.currentTimeMillis());
    }


    public void clearSharedPreferences(Context context) {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(KEY_INSTALL_DATE);
        editor.remove(KEY_LAUNCH_TIMES);
        editor.apply();

    }


    public void setNeverShowAgain(Context context, boolean neverShowAgain) {

        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(NEVER_SHOW_AGAIN, neverShowAgain);
        editor.apply();
        mNeverShowAgain = neverShowAgain;

    }


}
