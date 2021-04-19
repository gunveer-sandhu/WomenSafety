package gunveer.codes.womensafety;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.MyViewHolder>{

    Context context;
    List<Timer> listOfTimers;
    public Thread thread;
    public volatile boolean stopThread = false;
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
                        listOfTimers.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        TimerCreater.saving(listOfTimers, context);
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
            if(!stopThread){
                stopThread = true;
                SystemClock.sleep(1000);
                timerOn(position);
                Toast.makeText(context, "Timer Reset: "+listOfTimers.get(position).label, Toast.LENGTH_SHORT).show();
            }else{
                tvLabel.setText(listOfTimers.get(position).getLabel());
                toggleOn.setChecked(false);
                toggleOn.setText("OFF");
                tvMinutes.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
                tvSeconds.setText("00");
                tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));
            }
        }

        public void timerOn(int position) {
            stopThread = false;
            thread = new Thread(){
                @Override
                public void run() {
                    int minutes = listOfTimers.get(position).getMinutes();
                    int seconds = 0;
                    int missedTimerInt = listOfTimers.get(position).getMissedTimer();
                    int totalTime =  minutes*60000 + seconds*1000;
                    int totalTimeRes = totalTime;
                    if(stopThread){
                        Toast.makeText(context, "Hitting the if", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        while(totalTime !=0){
                            if(stopThread){
                                mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvMinutes.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
                                        tvSeconds.setText("00");
                                        tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));
                                    }
                                });
                                return;
                            }
                            SystemClock.sleep(1000);
                            totalTime = totalTime - 1000;
                            minutes = totalTime/60000;
                            seconds = (totalTime%60000)/1000;
                            if(totalTime==0){
                                missedTimerInt = missedTimerInt - 1;
                                if(missedTimerInt==0){
                                    //write code of if timer expires
                                    timerExpires(position);
                                }else{
                                    totalTime = totalTimeRes;
                                }
                            }
                            int finalMinutes = minutes;
                            int finalSeconds = seconds;
                            int finalMissedTimerInt = missedTimerInt;
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvMinutes.setText(String.valueOf(finalMinutes));
                                    tvSeconds.setText(String.valueOf(finalSeconds));
                                    tvMissedTimers.setText(String.valueOf(finalMissedTimerInt));
                                }
                            });

                        }
                    }
                }
            };
            thread.start();


//            Toast.makeText(context, "Timer is on"+listOfTimers.get(position).label, Toast.LENGTH_SHORT).show();
        }

        private void timerExpires(int position) {
            if(!stopThread){
                stopThread = true;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Sending Alert.", Toast.LENGTH_LONG).show();
                        SystemClock.sleep(1000);
                        tvLabel.setText("ALERT SENT");
                        toggleOn.setChecked(false);
                        toggleOn.setText("OFF");
                        tvMinutes.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
                        tvSeconds.setText("00");
                        tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));
                    }
                });

            }
        }

        public void timerOff(int position) {
            if(!stopThread){
                stopThread = true;
//                thread.interrupt();
//                Toast.makeText(context, "Timer is off: "+listOfTimers.get(position).label, Toast.LENGTH_SHORT).show();

            }
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
    }
}
