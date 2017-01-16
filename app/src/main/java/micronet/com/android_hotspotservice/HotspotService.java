package micronet.com.android_hotspotservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by eemaan.siddiqi on 1/10/2017.
 */

public class HotspotService extends Service {
    private final BroadcastReceiver wifiApStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // first make sure that action received is wifi ap
            if (WiFiApManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
                // get Wi-Fi Hotspot state
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WiFiApManager.WIFI_AP_STATE_FAILED);
                Log.d(this.toString(), "wifiApState=" + state);
                if (state == WiFiApManager.WIFI_AP_STATE_DISABLED) {
                    enableWiFi();
                }
            }
        }
    };

    private Handler wifiApHandler;
    private int wifiApvalue;
    private final long SIXTY_SECONDS = 60000;
    private final long TEN_SECONDS = 10000;
    private Context context;
    private static final int NOTIFY_ME_ID=1337;

    private void enableWiFi(){
        // re-enable Wifi AP
        WiFiApManager.setWiFiApState(context, true);
        try {
			// wait 5 seconds to ensure that hotspot is in enabled (not enabling) status
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(this.toString(), "getWiFiApState=" + WiFiApManager.getWifiApState(context));
        if(WiFiApManager.getWifiApState(context)==WiFiApManager.WIFI_AP_STATE_ENABLED)
        {
			// hotspot must be in enabled status in order for FTP server to start on network
            context.sendBroadcast(new Intent("be.ppareit.swiftp.ACTION_START_FTPSERVER"));
            Log.d(TAG, "enableWififunc:");
        }
    }
    public void PushNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Hotspot Service")
                .setContentText("Running");
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PushNotification();
        IntentFilter intentFilter = new IntentFilter(WiFiApManager.WIFI_AP_STATE_CHANGED_ACTION);
        registerReceiver(wifiApStatusReceiver, intentFilter);
        context = this;
        WiFiApManager.setHotspotName("MicProdTestAP",context);
        if (wifiApHandler == null) {
            wifiApHandler = new Handler(Looper.myLooper());
            wifiApHandler.post(wifiApCheck);
        }
    }


    final Runnable wifiApCheck = new Runnable() {
        @Override
        public void run() {
            wifiApvalue = WiFiApManager.getWifiApState(context);
            try {
                switch (wifiApvalue) {
                    case WiFiApManager.WIFI_AP_STATE_DISABLING: //WiFi AP is currently disabling
                        //Doing Nothing and Setting post to 10s
                        wifiApHandler.postDelayed(this, TEN_SECONDS);
                    case WiFiApManager.WIFI_AP_STATE_DISABLED: //WiFi AP is currently disabled
                        //Re-enable the WiFi Hotspot state
                        enableWiFi();
                        /* WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                           wifi.setWifiEnabled(true);*/ //To disable Wi-Fi
                        //Seting post to 60s
                        wifiApHandler.postDelayed(this, SIXTY_SECONDS);
                        break;
                    case WiFiApManager.WIFI_AP_STATE_ENABLING: //Wifi AP is currently enabling
                        //Do nothing Setting post to 10s
                        wifiApHandler.postDelayed(this, TEN_SECONDS);
                        break;
                    case WiFiApManager.WIFI_AP_STATE_ENABLED:// WiFi AP is currently enabled
                        // Do nothing
                        wifiApHandler.postDelayed(this, SIXTY_SECONDS);
                        break;
                    case WiFiApManager.WIFI_AP_STATE_FAILED://WiFi Ap failed
                        // Re enable the WiFi Hotspot state
                        enableWiFi();
                        wifiApHandler.postDelayed(this, SIXTY_SECONDS);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Log.d(TAG, "run: bh");
            }
            wifiApHandler.postDelayed(this, 60000);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(wifiApStatusReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return false;
    }
    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
}
