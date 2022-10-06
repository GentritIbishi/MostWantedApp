package fiek.unipr.mostwantedapp.utils;

import android.Manifest;
import android.app.NotificationManager;

public class Constants {
    public static final String PREFS_NAME = "loginPreferences";
    public static final String LOGIN_INFORMER_PREFS = "loginInformerPreferences";
    public static final String ADMIN_ROLE = "Admin";
    public static final String USER_ROLE = "User";
    public static final String ROLE = "role";
    public static final String GRADE_A = "A";
    public static final String GRADE_B = "B";
    public static final String GRADE_C = "C";
    public static final String GRADE_D = "D";
    public static final String GRADE_E = "E";
    public static final String YES = "Yes";
    public static final String NO = "No";
    public static final String INFORMER_ROLE = "Informer";
    public static final String VERIFIED = "VERIFIED";
    public static final String UNVERIFIED = "UNVERIFIED";
    public static final String FAKE = "FAKE";
    public static final String SEEN = "SEEN";
    public static final String MALE = "MALE";
    public static final String FEMALE = "FEMALE";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String NA = "N/A";
    public static final String ADMIN_INFORMER_PREFS = "ADMIN_INFORMER_PREFS";
    public static final String USER_INFORMER_PREFS = "USER_INFORMER_PREFS";
    public static final String PHONE_INFORMER_PREFS = "PHONE_INFORMER_PREFS";
    public static final String PROFILE_USER_PREFS = "PROFILE_USER_PREFS";
    public static final String HOME_USER_PREF = "HOME_USER_PREF";
    public static final String MAIN_TAG = "MAIN_TAG";
    public static final String DATE_TIME = "dd-MM-yyyy HH:mm:ss";
    public static final String DATE_TIME_STYLE = "E, dd MMM yyyy";
    public static final String DATE_TIME_STYLE_2 = "dd MMM yyyy";
    public static final String DATE = "dd-MM-yyyy";
    public static final String KG = "KG";
    public static final String CM = "CM";
    public static final String AGE = "AGE";
    public static final String EURO = "EURO";
    public static final String PHONE_USER = "PHONE_USER";
    public static final String COINS = "COINS";
    public static final Double BALANCE_DEFAULT = 0.00000;
    public static final String COINS_DEFAULT = "0 COINS";
    public static final Double LATITUDE_DEFAULT = 42.667542;
    public static final Double LONGITUDE_DEFAULT = 21.166191;
    public static final float DEFAULT_ZOOM = 15f;
    public static final String KEY = "O9b8jboCKh95zYheMD6Wsc7U";

    //FIREBASE TABLE NAMES
    public static final String USERS = "users";
    public static final String WANTED_PERSONS = "wanted_persons";
    public static final String ASSIGNED_REPORTS = "assigned_reports";
    public static final String ASSIGNED_REPORTS_PDF = "assign_report.pdf";
    public static final String REPORTS_ASSIGNED = "reports_assigned";
    public static final String INVESTIGATORS = "investigators";
    public static final String LOCATION_REPORTS = "location_reports";
    public static final String LOGIN_HISTORY = "loginHistory";
    public static final String NOTIFICATION_HELPER_ADMIN = "notifications_helper_admin";
    public static final String NOTIFICATION_ADMIN = "notifications_admin";
    public static final String NOTIFICATION_USER = "notifications_user";
    public static final String PAYMENT_INFORMATION = "payment_information";
    public static final String PERSONS = "persons";
    public static final String PROFILE_PICTURE = "profile_picture.jpg";
    public static final String DYNAMIC_DOMAIN = "https://fiek.page.link";
    public static final String FREE = "FREE";

    //permissions
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    public static final int PICK_IMAGE = 15;
    public static final int IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT;
    public static final String FOREGROUND_SERVICE = Manifest.permission.FOREGROUND_SERVICE;

    //Notifications
    public static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    public static final String NOTIFICATION_SERVICE = "NOTIFICATION_SERVICE";
    public static final String CHANNEL_ID_ADDED = "NEW_REPORT_ADDED";
    public static final String NOTIFICATION_NUMBER_1 = "notificationNumber1";

    public static final String CHANNEL_ID_MODIFIED = "NEW_REPORT_MODIFIED";
    public static final String NOTIFICATION_NUMBER_2 = "notificationNumber2";

    public static final String CHANNEL_ID_REMOVED = "NEW_REPORT_REMOVED";
    public static final String NOTIFICATION_NUMBER_3 = "notificationNumber3";

    public static final String CHANNEL_ID_USER_MODIFIED = "NEW_REPORT_USER_MODIFIED";
    public static final String NOTIFICATION_NUMBER_4 = "notificationNumber4";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
}
