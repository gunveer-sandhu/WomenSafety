package gunveer.codes.womensafety;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;
import static gunveer.codes.womensafety.MainActivity.listOfTimers;
import static gunveer.codes.womensafety.MainActivity.scrollView;
import static gunveer.codes.womensafety.MainActivity.tvHowTo;


public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder>{

    Context context;
    List<Timer> listOfTimers;
    public Thread thread;

    public static Handler mainHandler = new android.os.Handler(Looper.getMainLooper());


    public RecViewAdapter(Context context, List<Timer> listOfTimers) {
        this.context = context;
        this.listOfTimers = listOfTimers;
    }

    @NonNull
    @Override
    public RecViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.timer_layout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecViewAdapter.MyViewHolder holder, int position) {

        holder.tvMinutes.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
        holder.tvSeconds.setText(String.valueOf(((listOfTimers.get(position).getMinutes())%60000)/1000));
        holder.toggleOn.setChecked(listOfTimers.get(position).isToggleOn());
        holder.tvLabel.setText(listOfTimers.get(position).getLabel());
        holder.tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));



        if(holder.toggleOn.isChecked()){
            holder.toggleOn.setText("ON");
            holder.timerOn(position);
        }else{
            holder.toggleOn.setText("OFF");
//            holder.timerOff(position);
        }
    }

    @Override
    public int getItemCount() {
        try{
            return listOfTimers.size();
        }catch (Exception e){
            return 0;
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTimerNum, tvSeconds, tvMinutes, tvLabel, tvMissedTimers;
        Switch toggleOn;
        Button btnEdit, btnDelete, btnReset;
        ConstraintLayout parentLayout;
        Intent serviceIntent = new Intent(context, TimerService.class);
        BroadcastReceiver broadcastReceiver = null;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMinutes = itemView.findViewById(R.id.tvMinutes);
            tvSeconds = itemView.findViewById(R.id.tvSeconds);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvMissedTimers = itemView.findViewById(R.id.tvMissedTimers);

            toggleOn = itemView.findViewById(R.id.toggleOn);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnReset = itemView.findViewById(R.id.btnReset);
            parentLayout = itemView.findViewById(R.id.parentLayout);




            toggleOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(toggleOn.isChecked()){
//                        Toast.makeText(context, "Timer is ON", Toast.LENGTH_SHORT).show();
                        if(verifyOnlyOneOn(listOfTimers.get(getAdapterPosition()).label)){
                            toggleOn.setText("ON");
                            listOfTimers.get(getAdapterPosition()).toggleOn = true;
                            timerOn(getAdapterPosition());
                        }else{
                            toggleOn.setChecked(false);
                        }
                    }
                    else{
//                        Toast.makeText(context, "Timer is OFF", Toast.LENGTH_SHORT).show();
                        toggleOn.setText("OFF");
                        listOfTimers.get(getAdapterPosition()).toggleOn = false;
                        timerOff(getAdapterPosition());
                    }
                    TimerCreater.saving(listOfTimers, context);
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(verifyAllOff()){
                        new AsyncDeleter(getAdapterPosition()).execute();
                    }else{
                        Toast.makeText(context, "Switch off the timers to delete them.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(verifyAllOff()){
                        int position = getAdapterPosition();
                        Intent intent = new Intent(context, EditTimer.class);
                        intent.putExtra("timerNum", position);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "Turn Off the timer to edit.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listOfTimers.get(getAdapterPosition()).isToggleOn()){
                        timerReset(getAdapterPosition());
                    }
                }
            });
        }

        public void timerReset(int position) {
            //code for timer reset
            Intent resetIntent = new Intent(context, ResetBroadcast.class);
            resetIntent.putExtra("position", position);
            resetIntent.putExtra("reset", true);
            context.sendBroadcast(resetIntent);
        }

        public void timerOn(int position) {

            if(!isServiceRunningInForeground(context, TimerService.class)){

                serviceIntent.putExtra("position", position);
                context.startService(serviceIntent);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("updater");

                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int minutes = intent.getIntExtra("minutes", -1);
                        int seconds = intent.getIntExtra("seconds", -1);
                        int missedTimerInt = intent.getIntExtra("missedTimer", -1);
                        boolean toggleOnVal = intent.getBooleanExtra("toggleOn", true);
                        toggleOn.setChecked(toggleOnVal);
                        if(toggleOn.isChecked()){
                            toggleOn.setText("ON");
                        }else{
                            toggleOn.setText("OFF");
                        }
                        tvMinutes.setText(String.valueOf(minutes));
                        tvSeconds.setText(String.valueOf(seconds));
                        tvMissedTimers.setText(String.valueOf(missedTimerInt));
                    }
                };
                context.registerReceiver(broadcastReceiver, intentFilter);
            }else{
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("updater");

                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int minutes = intent.getIntExtra("minutes", -1);
                        int seconds = intent.getIntExtra("seconds", -1);
                        int missedTimerInt = intent.getIntExtra("missedTimer", -1);
                        boolean toggleOnVal = intent.getBooleanExtra("toggleOn", true);
                        toggleOn.setChecked(toggleOnVal);
                        if(toggleOn.isChecked()){
                            toggleOn.setText("ON");
                        }else{
                            toggleOn.setText("OFF");
                        }
                        tvMinutes.setText(String.valueOf(minutes));
                        tvSeconds.setText(String.valueOf(seconds));
                        tvMissedTimers.setText(String.valueOf(missedTimerInt));
                    }
                };
                context.registerReceiver(broadcastReceiver, intentFilter);
            }

//            stopThread = false;
//            Toast.makeText(context, "Timer is on"+listOfTimers.get(position).label, Toast.LENGTH_SHORT).show();
        }

        private void timerExpires(int position) {
            //code for timer Expiry
        }

        public void timerOff(int position) {
            //code for timer off
            Intent stopIntent = new Intent(context, ResetBroadcast.class);
            stopIntent.putExtra("position", position);
            stopIntent.putExtra("stop", true);
            context.sendBroadcast(stopIntent);
            context.unregisterReceiver(broadcastReceiver);
            //this service intent is not being used as internal stop self method is being called
//            Intent serviceIntent = new Intent(context, TimerService.class);
//            context.stopService(serviceIntent);
            resettingToDefaults(position);


        }

        public void resettingToDefaults(int position) {
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvMinutes.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
                    tvSeconds.setText(String.valueOf(((listOfTimers.get(position).getMinutes())%60000)/1000));
                    tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));
                }
            }, 1000);
        }

        private boolean verifyOnlyOneOn(String label) {
            for(int i =0; i<listOfTimers.size(); i++){
                if(listOfTimers.get(i).toggleOn && listOfTimers.get(i).label != label){
                    Toast.makeText(context, "Only One timer can be ON at one time.", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            return true;
        }
        public boolean verifyAllOff() {
            for(int i =0; i<listOfTimers.size(); i++){
                if(listOfTimers.get(i).toggleOn){
                    return false;
                }
            }
            return true;
        }
        public boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }

                }
            }
            return false;
        }
        private class AsyncDeleter extends AsyncTask<Integer, Void, Void>{
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            AlertDialog alertDialog;
            public AlertDialog.Builder getAlertBuilder() {
                alertBuilder.setView(R.layout.timer_delete_layout)
                        .setCancelable(false);
                return alertBuilder;
            }

            Response response = null;
            int position;

            @Override
            protected void onPreExecute() {
                alertDialog = getAlertBuilder().create();
                alertDialog.show();
                super.onPreExecute();
            }

            public AsyncDeleter(int position) {
                this.position = position;
            }

            @Override
            protected void onPostExecute(Void unused) {
                alertDialog.dismiss();
                super.onPostExecute(unused);
            }

            @Override
            protected Void doInBackground(Integer... integers) {
                List<String> imageUrls = new ArrayList<>();
                Map<String, String> imageUriString = listOfTimers.get(position).getLastClickedPhoto();
                Set links = imageUriString.keySet();
                Iterator iterator = links.iterator();

                while(iterator.hasNext()){

                    imageUrls.add(iterator.next().toString());
                }
                for(int i =0; i<imageUrls.size(); i++){
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, "{}");
                    Request request = new Request.Builder()
                            .url("https://api.imgur.com/3/image/"+imageUriString.get(imageUrls.get(i)))
                            .method("DELETE", body)
                            .addHeader("Authorization", "Client-ID 89bf146742231d7")
                            .build();

                    try {
                        response = client.newCall(request).execute();
                        String responser = response.body().string();
                        JSONObject object = new JSONObject(responser);
                        String status = object.getString("status");
                        if(status.contains("200")){

                        }else{
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "SOS cannot be deleted. Check your internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (IOException | JSONException e) {
                        Log.d(TAG, "imgurUpload: catching error " + e);
                        e.printStackTrace();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "SOS may not be deleted. Please try again." +
                                        " Check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                if(response!=null){
                    response.body().close();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listOfTimers.remove(getAdapterPosition());
                            notifyDataSetChanged();
                            TimerCreater.saving(listOfTimers, context);
                            if(listOfTimers.size()==0){
                                tvHowTo.setText("This app sends an SOS when the timer expires, even without an internet connection." +
                                        "\n" +
                                        "The timer would reset everytime you click reset button. But in case something bad happens, it'll trigger an SMS " +
                                        "and only then will send the alert." +
                                        "\n" +
                                        "You can attach up to 3 images, messages and choose to attach location(of when timer expires) with the SOS." +
                                        "\n" +
                                        "You can even set the SOS to be sent after missing multiple timers." +
                                        "\n" +
                                        "This requires no previous set up at the receiver's phone. And they cannot track you. Location will only be sent once if you want." +
                                        "\n" +
                                        "This app is a serverless app. All your data is on your phone safe and sound." +
                                        "\n" +
                                        "So get started add a timer by clicking on the '+'(plus) icon.");
                                tvHowTo.setVisibility(View.VISIBLE);
                                scrollView.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }

                return null;
            }
        }
    }

}
