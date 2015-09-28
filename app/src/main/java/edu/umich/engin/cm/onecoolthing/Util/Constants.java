package edu.umich.engin.cm.onecoolthing.Util;

/**
 * Created by jawad on 20/09/15.
 */
public class Constants {
    // Tags for saving and getting restorable state data
    public static final String KEY_STATE_CURINDEX = "Key_CurrentFragmentIndex";
    // Key for getting tutorial seen boolean from sharedPreferences
    public static final String KEY_SEENTUTORIAL = "seenTutorialYet";
    public static final String KEY_SEENSHAKEDIALOG = "Key_SeenShakeDialog";

    public static final String KEY_ENABLESHAKE = "Key_EnableShake";
    public static final String KEY_ENABLEDAILYDOSE = "Key_DailyDoseNotif";
    public static final String KEY_DAILYNOTIFTIME_HOUR = "Key_DailyNotifTime_Hour";
    public static final String KEY_DAILYNOTIFTIME_MINUTE = "Key_DailyNotifTime_Minute";

    public static final String KEY_NOTIFSETYET = "Key_NotificationSetYet";

    // Default values for the various keys used with SharedPreferences
    public static final int DEFAULTVAL_STATE_CURINDEX = 0;
    public static final boolean DEFAULTVAL_SEENTUTORIAL = false;
    public static final boolean DEFAULTVAL_SEENSHAKEDIALOG = false;
    public static final boolean DEFAULTVAL_ENABLESHAKE = true;
    public static final boolean DEFAULTVAL_ENABLEDAILYDOSE = true;
    public static final int DEFAULTVAL_DAILYNOTIFTIME_HOUR = 8;
    public static final int DEFAULTVAL_DAILYNOTIFTIME_MINUTE = 0;
    public static final boolean DEFAULTVAL_NOTIFSETYET = false;
}
