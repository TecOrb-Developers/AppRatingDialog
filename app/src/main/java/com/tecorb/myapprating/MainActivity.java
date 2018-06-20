package com.tecorb.myapprating;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.tecorb.library.Callbacks.RatingListener;
import com.tecorb.library.MyAppRating;

public class MainActivity extends AppCompatActivity implements RatingListener{

    Context context;
    MyAppRating dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;
        dialog=new MyAppRating(context,this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dialog.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dialog.showIfNeeded(getString(R.string.title_text),getString(R.string.rate_now_text),
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

        Toast.makeText(context,"We will get back soon after 3 days",Toast.LENGTH_SHORT).show();
        dialog.dissmissDialog();

    }

    @Override
    public void neverReminder() {

        Toast.makeText(context,"Rate app on playstore",Toast.LENGTH_SHORT).show();
        dialog.dissmissDialog();

    }


}
