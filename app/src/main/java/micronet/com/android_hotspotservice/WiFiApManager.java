package micronet.com.android_hotspotservice;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by eemaan.siddiqi on 1/10/2017.
 */

public class WiFiApManager {

    /* Wifi AP values and intent string from obc_android SDK */
    public static final int WIFI_AP_STATE_DISABLING = 10; // Wi-Fi AP is currently being disabled. The state will change to WIFI_AP_STATE_DISABLED if it finishes successfully.
    public static final int WIFI_AP_STATE_DISABLED = 11; // Wi-Fi AP is disabled.
    public static final int WIFI_AP_STATE_ENABLING = 12; // Wi-Fi AP is currently being enabled. The state will change to WIFI_AP_STATE_ENABLED if it finishes successfully.
    public static final int WIFI_AP_STATE_ENABLED = 13; // Wi-Fi AP is enabled.
    public static final int WIFI_AP_STATE_FAILED = 14;  // Wi-Fi AP is in a failed state. This state will occur when an error occurs during enabling or disabling

    // Broadcast intent action indicating that Wi-Fi AP has been enabled, disabled, enabling, disabling, or failed.
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    public static int getWifiApState(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            Method isWifiApEnabledMethod = wifiManager.getClass().getDeclaredMethod("getWifiApState");
            isWifiApEnabledMethod.setAccessible(true);
            int isWifiAponvalue= (Integer) isWifiApEnabledMethod.invoke(wifiManager);
            return isWifiAponvalue;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return WIFI_AP_STATE_FAILED;
    }
    public static boolean setHotspotName(String MicProdTestAP, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            // if WiFi is on, turn it off
            if(wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = MicProdTestAP;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Start or stop the AccessPoint using the saved configuration.
     * Uses reflection to access System API
     *
     * @param context Activity context
     * @param enabled true to enable and false to disable
     * @return {@code true} if the operation succeeds, {@code false} otherwise
     */
    public static boolean setWiFiApState(Context context, boolean enabled) {

        Boolean result = false;
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            // if WiFi is on, turn it off
            if(wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
            // using reflection to get method access for getWifiApConfiguration and setWifiApEnabled
            Method getWifiApMethod = wifiManager.getClass().getDeclaredMethod("getWifiApConfiguration");
            getWifiApMethod.setAccessible(true);
            WifiConfiguration wifiApConfig = (WifiConfiguration) getWifiApMethod.invoke(wifiManager);
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            // pass in the ObjectReceiver, WifiConfiguration, boolean
            result = (Boolean) setWifiApMethod.invoke(wifiManager, wifiApConfig, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
