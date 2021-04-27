package gunveer.codes.womensafety;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import static android.provider.ContactsContract.Intents.Insert.ACTION;
import static gunveer.codes.womensafety.App.CHANNEL_ID;
import static gunveer.codes.womensafety.MainActivity.listOfTimers;

import static gunveer.codes.womensafety.RecViewAdapter.mainHandler;

public class TimerService extends Service {
    public volatile boolean stopThread = false;
    public volatile boolean resetThread = false;
    public static final String TAG = "here";
    public BroadcastReceiver broadcastReceiver = null;
    FusedLocationProviderClient fusedLocationProviderClient;
    int PERMISSION_ID = 44;
    String locationLink = "https://www.google.com/maps/search/?api=1&query=";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        int position = intent.getIntExtra("position", -1);
        final int[] minutes = {listOfTimers.get(position).getMinutes()};
        int minReset = minutes[0];
        final int[] seconds = {0};
        int secReset = seconds[0];
        final int[] missedTimerInt = {listOfTimers.get(position).getMissedTimer()};
        int missedTimerReset = missedTimerInt[0];
        final int[] totalTime = {minutes[0] * 60000 + seconds[0] * 1000};
        int totalTimeRes = totalTime[0];

        //location service initialised
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //intent for buttons in the notifications


        //intent for resetting the timer
        Intent resetIntent = new Intent(this, ResetBroadcast.class);
        resetIntent.putExtra("position", position);
        resetIntent.putExtra("reset", true);
        PendingIntent resetPendingIntent = PendingIntent.getBroadcast(this,
                1, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //intent for stopping the timer
        Intent stopIntent = new Intent(this, ResetBroadcast.class);
        stopIntent.putExtra("position", position);
        stopIntent.putExtra("stop", true);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this,
                2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //notification for foreground service
        Intent notificationIntent = new Intent(TimerService.this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(TimerService.this,
                0, notificationIntent, 0);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(TimerService.this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(TimerService.this, CHANNEL_ID)
                .setContentTitle("Alert Running: "+listOfTimers.get(position).getLabel())
                .setContentText((minutes[0]) + " min : " + (seconds[0]) + " sec. " + "Missed Timers: " + (missedTimerReset - missedTimerInt[0]) + " of " + (missedTimerReset))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "RESET", resetPendingIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "STOP", stopPendingIntent)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();

        startForeground(1, notification);

        Intent intent1 = new Intent();
        intent1.setAction("updater");

        if (position != -1) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    if (stopThread) {
                        return;
                    } else {
                        while (totalTime[0] != 0) {
                            if (stopThread) {
                                stopSelf();
                                intent1.putExtra("minutes", minReset);
                                intent1.putExtra("seconds", secReset);
                                intent1.putExtra("missedTimer", missedTimerReset);
                                intent1.putExtra("toggleOn", false);
                                sendBroadcast(intent1);
//                                Log.d(TAG, "in stop thread here");

                                listOfTimers.get(position).setToggleOn(false);
                                TimerCreater.saving(listOfTimers, TimerService.this);

                                return;
                            }
                            if (resetThread) {
                                //code for resetting the thread
                                intent1.putExtra("minutes", minReset);
                                intent1.putExtra("seconds", secReset);
                                intent1.putExtra("missedTimer", missedTimerReset);
                                sendBroadcast(intent1);
                                totalTime[0] = totalTimeRes;
                                missedTimerInt[0] = missedTimerReset;
                                resetThread = false;
//                                Log.d(TAG, "in reset thread here");
                            }

                            totalTime[0] = totalTime[0] - 10000;
                            minutes[0] = totalTime[0] / 60000;
                            seconds[0] = (totalTime[0] % 60000) / 1000;

                            if (minutes[0] == 0) {
                                builder.setContentText("00 min" + " : " + (seconds[0]) + " sec. " + "Missed Timers: " + (missedTimerReset - missedTimerInt[0]) + " of " + (missedTimerReset));
                            } else {
                                builder.setContentText((minutes[0]) + " min : " + (seconds[0]) + " sec. " + "Missed Timers: " + (missedTimerReset - missedTimerInt[0]) + " of " + (missedTimerReset));
                            }
                            notificationManager.notify(1, builder.build());


                            if (totalTime[0] == 0) {
                                missedTimerInt[0] = missedTimerInt[0] - 1;
                                intent1.putExtra("minutes", minutes[0]);
                                intent1.putExtra("seconds", seconds[0]);
                                intent1.putExtra("missedTimer", missedTimerInt[0]);
                                sendBroadcast(intent1);
                                if (missedTimerInt[0] == 0) {
                                    //write code of if timer expires
                                    codeExpires();
                                    resetTimer();
                                    return;
                                } else {
                                    getLocation();
                                    totalTime[0] = totalTimeRes;
                                }
                            } else {
                                intent1.putExtra("minutes", minutes[0]);
                                intent1.putExtra("seconds", seconds[0]);
                                intent1.putExtra("missedTimer", missedTimerInt[0]);
                                sendBroadcast(intent1);
                            }
                            SystemClock.sleep(1000);
                        }

                    }
                }

                private void resetTimer() {
                    intent1.putExtra("minutes", minReset);
                    intent1.putExtra("seconds", secReset);
                    intent1.putExtra("missedTimer", missedTimerReset);
                    intent1.putExtra("toggleOn", false);
                    sendBroadcast(intent1);
//                                Log.d(TAG, "in stop thread here");

                    listOfTimers.get(position).setToggleOn(false);
                    TimerCreater.saving(listOfTimers, TimerService.this);
//                    stopSelf();
                    builder.setContentText("Alert Sent. Stay Safe. " + "Missed Timers: " + (missedTimerReset - missedTimerInt[0]) + " of " + (missedTimerReset));
                    notificationManager.notify(1, builder.build());
                }

                private void codeExpires() {
                    if (!listOfTimers.get(position).isExcludeLocation()) {
                        //code with location
                        getLocation();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        locationLink = locationLink + String.valueOf(listOfTimers.get(position).getLastLocation().getLatitude()) + "," + String.valueOf(listOfTimers.get(position).getLastLocation().getLongitude());
                    } else {
                        locationLink = "Sorry, the location was not attached.";
                        //code without location
                    }
                    sendSms();
                    stopSelf();
                }

                private void sendSms() {
                    for(int i = 0; i<listOfTimers.get(position).getContactsToAlert().size(); i++){
                        String message = "SOS Label: " + listOfTimers.get(position).getLabel() + "\n"
                                + "SOS Message: " + listOfTimers.get(position).getMessage() + "\n"
                                + "SOS Location: " + locationLink + "\n"
                                + "Check your email for images attached: " + listOfTimers.get(position).getContactsToAlert().get(i).getContactEmail() + "\n"
                                + "You should check up on them. Sent by Stay Safe App.";

                        SmsManager smsManager = SmsManager.getDefault();
                        ArrayList<String> parts = smsManager.divideMessage(message);
//                        mainHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(context, String.valueOf(parts.size()), Toast.LENGTH_LONG).show();
//                            }
//                        }, 1000);

                        if(parts.size()==1){
                            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                            sendIntent.setType("text/plain");
                            sendIntent.setData(Uri.parse("smsto:"));
                            sendIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            sendIntent.putExtra("sms_body", parts.get(0));
                            startActivity(sendIntent);
                        }else{

                            smsManager.sendMultipartTextMessage(String.valueOf(listOfTimers.get(position).getContactsToAlert().get(i).contactNumber), null, parts, null, null);
//                            for(int j =0; j<parts.size(); j++){
//                                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
//                                sendIntent.setType("text/plain");
//                                sendIntent.setData(Uri.parse("smsto:"));
//                                sendIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//                                sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                sendIntent.putExtra("sms_body", parts.get(j));
//                                startActivity(sendIntent);
//                            }
//                            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//                            sendIntent.setType("text/plain");
//                            sendIntent.setData(Uri.parse("smsto:"));
//                            sendIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//                            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            sendIntent.putExtra("sms_body", parts);
//                            startActivity(sendIntent);
                        }
                    }
                }

                private void getLocation() {
                    //checking for permissions
                    if (checkPermissions()) {
                        //checking if location enabled
                        if (isLocationEnabled()) {
                            Log.d(TAG, "getLocation: got location");
                            if (ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                Log.d(TAG, "getLocation: in here 1");
                                return;
                            }
                            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Log.d(TAG, "getLocation: in here 2");
                                    listOfTimers.get(position).setLastLocation(task.getResult());
                                    if (listOfTimers.get(position).getLastLocation() == null) {
                                        Log.d(TAG, "getLocation: in here 3");
                                        requestNewLocationData();
                                    } else {
                                        Log.d(TAG, "onComplete: here"+ listOfTimers.get(position).getLastLocation());
                                        TimerCreater.saving(listOfTimers, TimerService.this);
                                    }
                                }
                            });
                        } else {
                            //Turn on location
                            Log.d(TAG, "getLocation: Location not enabled");
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    } else {
                        Log.d(TAG, "getLocation: Did not got the permissions");
                        //request for permissions
//                        requestPermissions();

                    }
                }

                private void requestNewLocationData() {
                    LocationRequest locationRequest = new LocationRequest()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(5)
                            .setFastestInterval(0)
                            .setNumUpdates(1);

                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TimerService.this);
                    if (ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    }
                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                }

                private LocationCallback locationCallback = new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        Log.d(TAG, "onComplete: "+ location.toString());
                        listOfTimers.get(position).setLastLocation(location);
                        TimerCreater.saving(listOfTimers, TimerService.this);
                    }
                };

                private boolean checkPermissions(){
                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                        return ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    }else{
                        return ActivityCompat.checkSelfPermission(TimerService.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
                    }
                }

                

                private boolean isLocationEnabled(){
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                }


            };
            if(!stopThread){
//                Log.d(TAG, "Starting here.");
                thread.start();
            }else{
//                Log.d(TAG, "starting here else.");
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ThreadFunctions");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopThread = intent.getBooleanExtra("stopThread", false);
                resetThread = intent.getBooleanExtra("resetThread", false);
//                Log.d(TAG, "stop "+stopThread);
//                Log.d(TAG, "reset "+resetThread);
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
//        Log.d(TAG, "before return sticky");

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:");
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
