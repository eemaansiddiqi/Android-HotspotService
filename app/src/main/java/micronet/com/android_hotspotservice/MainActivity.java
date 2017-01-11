package micronet.com.android_hotspotservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // start the auto date/time reset service
        Intent service = new Intent(this, HotspotService.class);
        startService(service);
        finish();
    }
}
