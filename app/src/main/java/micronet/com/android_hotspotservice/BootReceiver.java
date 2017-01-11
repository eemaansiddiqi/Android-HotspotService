package micronet.com.android_hotspotservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by eemaan.siddiqi on 1/10/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Wi-Fi Hotspot monitored
        Intent service=new Intent(context,HotspotService.class);
        context.startService(service);
    }
}
