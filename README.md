# AppRatingDialog

<img src="http://res.cloudinary.com/duyx74vfd/image/upload/v1529498210/Screenshot_2018-06-14-17-49-19_uwosum.png" width="320px">

Default conditions to show:

1. User opened the app after 3 days of first opening.

* Please Note: The lastest version uses AppCompat. Your application Theme has to be inherited from *Theme.AppCompat*

## How to Use
Create dialog in your main activity or your start activity:

```java
public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  myAppRating=new MyAppRating(context,this);
}
```

after you need to start dialog at:

```java
@Override
protected void onStart() {
  super.onStart();
  myAppRating.onStart();
}
```

this line inc. counters and initialize first app access date if necessary

And to show when needed just call in `onResume`:

```java
@Override
protected void onResume() {
  super.onResume();
  dialog.showIfNeeded(getString(R.string.title_text),getString(R.string.rate_now_text),
                getString(R.string.remined_me_later_text),getString(R.string.no_rhanks_text),
                ContextCompat.getColor(context,R.color.colorWhite));
}
```
### Getting results

Rating and comments can be fetched by listener implemented by activity.

```java
class MyActivity implements RatingListener{

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
```


## Integration
This library is hosted by jitpack.io.

Your project must enable databinding, to enable databinding, add the following to your `build.gradle` file:

```
android {
    ...
    dataBinding {
        enabled = true;
    }
}
```

Root level gradle:
```
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
}
```
```
dependencies {
    implementation 'com.github.TecOrb-Developers:AppRatingDialog:1.1'
}
```
