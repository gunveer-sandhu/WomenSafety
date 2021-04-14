package gunveer.codes.womensafety;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import static gunveer.codes.womensafety.MainActivity.listOfTimers;


public class TimerCreater {

    private static Context context;

    private Timer timer1 = new Timer();

    public String label;
    public String message;
    public int minutesToExpiry;
    public List<String> imageUri;
    public List<Contact> contactList;
    public int missedTimer;
    public boolean excludeLocation;



    public TimerCreater(String label, int minutesToExpiry, String message, List<String> imageUri, List<Contact> contactList, int missedTimer, boolean excludeLocation, Context context) {
        this.label = label;
        this.minutesToExpiry = minutesToExpiry;
        this.message = message;
        this.imageUri = imageUri;
        this.contactList = contactList;
        this.missedTimer = missedTimer;
        this.excludeLocation = excludeLocation;
        this.context = context;
        listOfTimers.add(initTimer(label, minutesToExpiry, message, imageUri, contactList, missedTimer, excludeLocation));
        saving(listOfTimers, context);
    }


    private Timer initTimer(String label, int minutesToExpiry, String message, List<String> imageUri, List<Contact> contactList,
                            int missedTimer, boolean excludeLocation) {
        timer1.hour=0;
        timer1.label = label;
        timer1.minutes = minutesToExpiry;
        timer1.message = message;
        timer1.contactsToAlert = contactList;
        timer1.lastClickedPhoto = imageUri;
        timer1.toggleOn = true;
        timer1.missedTimer = missedTimer;
        timer1.lastLocation = null;
        timer1.excludeLocation = excludeLocation;
        return timer1;
    }

    public static void saving(List<Timer> listOfTimers, Context context){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        Type type = new TypeToken<List<Timer>>(){}.getType();
        String json = gson.toJson(listOfTimers, type);
        editor.putString("listOfTimers", json);
        editor.commit();

    }

}
