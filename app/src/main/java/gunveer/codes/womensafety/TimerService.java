package gunveer.codes.womensafety;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static gunveer.codes.womensafety.App.CHANNEL_ID;
import static gunveer.codes.womensafety.MainActivity.listOfTimers;

public class TimerService extends Service {
    public volatile boolean stopThread = false;
    public volatile boolean resetThread = false;
    public static final String TAG = "here";
    public BroadcastReceiver broadcastReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int position = intent.getIntExtra("position", -1);
        final int[] minutes = {listOfTimers.get(position).getMinutes()};
        int minReset = minutes[0];
        final int[] seconds = {0};
        int secReset = seconds[0];
        final int[] missedTimerInt = {listOfTimers.get(position).getMissedTimer()};
        int missedTimerReset = missedTimerInt[0];
        final int[] totalTime = {minutes[0] * 60000 + seconds[0] * 1000};
        int totalTimeRes = totalTime[0];

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
                .setContentTitle(listOfTimers.get(position).getLabel())
                .setContentText((minutes[0])+" min : " + (seconds[0]) + " sec. "+"Missed Timers yet: "+(missedTimerReset-missedTimerInt[0])+" of "+(missedTimerReset))
                .setSmallIcon(R.drawable.ic_launcher_foreground)

                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "RESET", resetPendingIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_normal, "STOP", stopPendingIntent);
        Notification notification = builder.build();

        startForeground(1, notification);

        Intent intent1 = new Intent();
        intent1.setAction("updater");

        if(position != -1) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    if (stopThread) {
                        return;
                    } else {
                        while (totalTime[0] != 0) {
                            if (stopThread) {
                                intent1.putExtra("minutes", minReset);
                                intent1.putExtra("seconds", secReset);
                                intent1.putExtra("missedTimer", missedTimerReset);
                                intent1.putExtra("toggleOn", false);
                                sendBroadcast(intent1);
                                Log.d(TAG, "in stop thread here");
                                stopSelf();
                                return;
                            }
                            if(resetThread){
                                //code for resetting the thread
                                intent1.putExtra("minutes", minReset);
                                intent1.putExtra("seconds", secReset);
                                intent1.putExtra("missedTimer", missedTimerReset);
                                sendBroadcast(intent1);
                                totalTime[0] = totalTimeRes;
                                missedTimerInt[0]=missedTimerReset;
                                resetThread = false;
                                Log.d(TAG, "in reset thread here");
                            }
                            SystemClock.sleep(1000);
                            totalTime[0] = totalTime[0] - 1000;
                            minutes[0] = totalTime[0] / 60000;
                            seconds[0] = (totalTime[0] % 60000) / 1000;

                            if(minutes[0]==0){
                                builder.setContentText("00 min"+ " : " + (seconds[0]) + " sec. "+"Missed Timers yet: "+(missedTimerReset-missedTimerInt[0])+" of "+(missedTimerReset));
                                notificationManager.notify(1, builder.build());
                            }else{
                                builder.setContentText((minutes[0])+" min : " + (seconds[0]) + " sec. "+"Missed Timers yet: "+(missedTimerReset-missedTimerInt[0])+" of "+(missedTimerReset));
                                notificationManager.notify(1, builder.build());
                            }



                            if (totalTime[0] == 0) {
                                missedTimerInt[0] = missedTimerInt[0] - 1;
                                intent1.putExtra("minutes", minutes[0]);
                                intent1.putExtra("seconds", seconds[0]);
                                intent1.putExtra("missedTimer", missedTimerInt[0]);
                                sendBroadcast(intent1);
                                if (missedTimerInt[0] == 0) {
                                    //write code of if timer expires
                                } else {
                                    totalTime[0] = totalTimeRes;
                                }
                            } else {
                                intent1.putExtra("minutes", minutes[0]);
                                intent1.putExtra("seconds", seconds[0]);
                                intent1.putExtra("missedTimer", missedTimerInt[0]);
                                sendBroadcast(intent1);
                            }

                        }
                    }
                }

            };
            if(!stopThread){
                Log.d(TAG, "Starting here.");
                thread.start();
            }else{
                Log.d(TAG, "starting here else.");
            }
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ThreadFunctions");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                stopThread = intent.getBooleanExtra("stopThread", false);
                resetThread = intent.getBooleanExtra("resetThread", false);
                Log.d(TAG, "stop "+stopThread);
                Log.d(TAG, "reset "+resetThread);
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
