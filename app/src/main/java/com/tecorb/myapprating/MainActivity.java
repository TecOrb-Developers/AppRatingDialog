package com.tecorb.myapprating;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tecorb.library.AppRating;
import com.tecorb.library.AppRatingConfig;
import com.tecorb.library.Callbacks.RatingListener;

import java.sql.Time;

public class MainActivity extends AppCompatActivity implements RatingListener{

    Context context;
    AppRating dialog;

    private static final int INSTALLED_DAYS = 7;
    private static final int LAUNCHED_TIMES = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        AppRatingConfig config=new AppRatingConfig(INSTALLED_DAYS,LAUNCHED_TIMES,this);

        dialog=new AppRating(context,config);

        dialog.appRatingDialog(context,getString(R.string.title_text),getString(R.string.rate_now_text),
                getString(R.string.remined_me_later_text),getString(R.string.no_rhanks_text),
                ContextCompat.getColor(context,R.color.colorWhite));

        

    }

    @Override
    public void rateNow() {

        Toast.makeText(context,"Rate app on playstore",Toast.LENGTH_SHORT).show();
        dialog.dissmissDialog();

    }

    @Override
    public void remindMeLater() {

        Toast.makeText(context,"We will get back soon after 7 days",Toast.LENGTH_SHORT).show();
        dialog.dissmissDialog();


    }

    @Override
    public void noThanks() {

        Toast.makeText(context,"Rate app on playstore",Toast.LENGTH_SHORT).show();
        dialog.dissmissDialog();

    }


}
