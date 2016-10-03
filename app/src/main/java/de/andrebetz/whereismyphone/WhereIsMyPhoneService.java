// WhereIsMyPhone@andrebetz.de
package de.andrebetz.whereismyphone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
// https://java.net/projects/javamail/pages/Home
// https://maven.java.net/content/groups/public/
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/*
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
*/
/**
 * Created by mail@AndreBetz.de on 05.04.2016.
 */
public class WhereIsMyPhoneService extends Service {

    private static final String TAG = "WhereIsMyPhoneService";

    private int mDelay = 10000;
    private boolean isRunning = false;

    private String mEmailHost;
    private String mEmailTo;
    private String mEmailAddress;
    private String mEmailUser;
    private String mEmailPass;
    private String mEmailSub;
    private String mEmailPort;
    private String mEmailDelay;

    private SendMyMail mSendMail = null;
    private GPSTracker mGpsTracker = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        isRunning = true;
    }

    public Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                try {

                    if ( !isLocationServiceEnabled() ) {
                        enableLocateionService();
                    }
                    Location position = mGpsTracker.getLocation();
                    if (null != position) {
                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date(position.getTime());
                        String dateFormatted = format.format(date);
                        mSendMail.set_body(
                            "Time:      " + dateFormatted + "\n" +
                            "Latitude:  " + Double.toString(position.getLatitude()) + "\n" +
                            "Longitude: " + Double.toString(position.getLongitude()) + "\n" +
                            "Altitude:  " + Double.toString(position.getAltitude()) + "\n"
                        );

                        if (isOnline()) {
                            mSendMail.send();
                        }
                    }

                    Thread.sleep(mDelay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "Service task ending");
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        mEmailHost = intent.getStringExtra(Constants.emailHostTag);
        mEmailTo = intent.getStringExtra(Constants.emailToTag);
        mEmailAddress = intent.getStringExtra(Constants.emailAddressTag);
        mEmailUser = intent.getStringExtra(Constants.emailUserTag);
        mEmailPass = intent.getStringExtra(Constants.emailPasswordTag);
        mEmailSub = intent.getStringExtra(Constants.emailSubTag);
        mEmailDelay = intent.getStringExtra(Constants.emailUpdateTag);
        mEmailPort = intent.getStringExtra(Constants.emailPortTag);

        Log.i(TAG, mEmailHost);
        Log.i(TAG, mEmailTo);
        Log.i(TAG, mEmailAddress);
        Log.i(TAG, mEmailUser);
        Log.i(TAG, mEmailPass);
        Log.i(TAG, mEmailSub);
        Log.i(TAG, mEmailPort);
        Log.i(TAG, mEmailDelay);

        mSendMail = new SendMyMail(mEmailUser, mEmailPass);
        mSendMail.set_from(mEmailAddress);
        mSendMail.set_to(mEmailTo);
        mSendMail.set_host(mEmailHost);
        mSendMail.set_subject(mEmailSub);
        mSendMail.set_port(mEmailPort);

        mDelay = Integer.parseInt(mEmailDelay) * 1000;

        mGpsTracker = new GPSTracker(this);

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        return Service.START_STICKY;
    }


    public boolean isLocationServiceEnabled() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }

    public void enableLocateionService() {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Intent myIntent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(myIntent);
        }
    }
    
    /*
    private void turnGPSOnOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
            //Toast.makeText(this, "Your GPS is Enabled",Toast.LENGTH_SHORT).show();
        }
    }
    */

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Log.i(TAG, "Service onDestroy");
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}