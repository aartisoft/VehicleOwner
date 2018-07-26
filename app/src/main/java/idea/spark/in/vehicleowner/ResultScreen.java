package idea.spark.in.vehicleowner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class ResultScreen extends AppCompatActivity {

    HandleXML obj;

    TextView lblowner, lblclass, lblmodel, lblregno, lblregdate, lblchasis, lblengine, lblfuel, lblrto;
    InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;

    Timer timer;
    MyTimerTask myTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareRegistrationDetails();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_result_ad_unit_id));
        requestNewInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        AdView mBottomAdView = (AdView) findViewById(R.id.bottomBanner);
        AdRequest adBottomRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mBottomAdView.loadAd(adBottomRequest);

        lblowner=(TextView)findViewById(R.id.lblownername);
        lblchasis=(TextView)findViewById(R.id.lblchasisno);
        lblengine=(TextView)findViewById(R.id.lblengineno);
        lblfuel=(TextView)findViewById(R.id.lblfueltype);
        lblmodel=(TextView)findViewById(R.id.lblmodel);
        lblregdate=(TextView)findViewById(R.id.lblregdate);
        lblregno=(TextView)findViewById(R.id.lblregno);
        lblclass=(TextView)findViewById(R.id.lblvehclass);
        lblrto=(TextView)findViewById(R.id.lblrtoname);

        Bundle bundle = getIntent().getExtras();
        obj = bundle.getParcelable("data");

        if(obj!=null) {
            lblowner.setText(obj.getOwner().toUpperCase());
            lblchasis.setText(obj.getChasis().toUpperCase());
            lblengine.setText(obj.getengine().toUpperCase());
            lblfuel.setText(obj.getFuel().toUpperCase());
            lblmodel.setText(obj.getMaker().toUpperCase());
            lblregdate.setText(obj.getRegdt().toUpperCase() + " (" + obj.getVehAge().toUpperCase().replace("&AMP;","AND") + ")");
            lblregno.setText(obj.getRegno().toUpperCase());
            lblclass.setText(obj.getVehClass().toUpperCase());
            lblrto.setText(obj.getRto().toUpperCase() + ", " + obj.getState().toUpperCase());
        }

        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Utils.internetStatus(ResultScreen.this)){
                    showInterstitial();
                }
            }
        }, 6000);*/


        if(timer != null){
            timer.cancel();
        }

        //re-schedule timer here
        //otherwise, IllegalStateException of
        //"TimerTask is scheduled already"
        //will be thrown
        timer = new Timer();
        myTimerTask = new MyTimerTask();

        //delay 1000ms, repeat in 5000ms
        timer.schedule(myTimerTask, 6000, 18000);


        Bundle analyticsBundle = new Bundle();
        analyticsBundle.putString("email",Utils.getPrimaryEmail(this));
        analyticsBundle.putString("ad",Utils.internetStatus(this) ? "yes":"no");
        mFirebaseAnalytics.logEvent("resultScreen", bundle);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }

    private void shareRegistrationDetails(){
        StringBuilder sb = new StringBuilder();

        sb.append(getString(R.string.regdet));
        sb.append("\n");

        sb.append(getString(R.string.ownername));
        sb.append(": ");
        sb.append(obj.getOwner().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.vehclass));
        sb.append(": ");
        sb.append(obj.getVehClass().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.model));
        sb.append(": ");
        sb.append(obj.getMaker().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.regno));
        sb.append(": ");
        sb.append(obj.getRegno().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.regdt));
        sb.append(": ");
        sb.append(obj.getRegdt().toUpperCase());
        sb.append(" (");
        sb.append(obj.getVehAge().toUpperCase().replace("&AMP;","AND"));
        sb.append(")");
        sb.append("\n");

        sb.append(getString(R.string.chasisno));
        sb.append(": ");
        sb.append(obj.getChasis().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.engineno));
        sb.append(": ");
        sb.append(obj.getengine().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.fueltype));
        sb.append(": ");
        sb.append(obj.getFuel().toUpperCase());
        sb.append("\n");

        sb.append(getString(R.string.rtoname));
        sb.append(": ");
        sb.append(obj.getRto().toUpperCase());
        sb.append(", ");
        sb.append(obj.getState().toUpperCase());
        sb.append("\n");

        sb.append("Shared Via Vehicle Owner App, To install this app click following link");
        sb.append("\n");
        sb.append("https://play.google.com/store/apps/details?id=" + getPackageName());


        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void requestNewInterstitial() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    public void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    showInterstitial();
                }});
        }
    }


}
