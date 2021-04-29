package gunveer.codes.staysafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ResetBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        int position = intent.getIntExtra("position", -1);
        boolean stop = intent.getBooleanExtra("stop", false);
        boolean reset = intent.getBooleanExtra("reset", false);

        if(position != -1 && reset){
//            Toast.makeText(context, "In reset", Toast.LENGTH_SHORT).show();
            Intent resetThread = new Intent();
            resetThread.setAction("ThreadFunctions");
            resetThread.putExtra("stopThread", false);
            resetThread.putExtra("resetThread", true);
            context.sendBroadcast(resetThread);
        }else if(position != -1 && stop){
//            Toast.makeText(context, "In stop", Toast.LENGTH_SHORT).show();
            Intent stopThread = new Intent();
            stopThread.setAction("ThreadFunctions");
            stopThread.putExtra("stopThread", true);
            stopThread.putExtra("resetThread", false);
            context.sendBroadcast(stopThread);
        }



    }
}
