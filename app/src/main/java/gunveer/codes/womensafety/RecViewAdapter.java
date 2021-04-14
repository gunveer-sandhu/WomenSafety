package gunveer.codes.womensafety;

import android.content.Context;
import android.content.Intent;
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
        holder.toggleOn.setChecked(listOfTimers.get(position).isToggleOn());
        holder.tvLabel.setText(listOfTimers.get(position).getLabel());
        holder.tvMissedTimers.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));



        if(holder.toggleOn.isChecked()){
            holder.toggleOn.setText("ON");
        }else{
            holder.toggleOn.setText("OFF");
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

    public  class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTimerNum, tvHour, tvMinutes, tvLabel, tvMissedTimers;
        Switch toggleOn;
        Button btnEdit, btnDelete, btnReset;
        ConstraintLayout parentLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMinutes = itemView.findViewById(R.id.tvMinutes);
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
                        toggleOn.setText("ON");
                        listOfTimers.get(getAdapterPosition()).toggleOn = true;

                    }
                    else{
//                        Toast.makeText(context, "Timer is OFF", Toast.LENGTH_SHORT).show();
                        toggleOn.setText("OFF");
                        listOfTimers.get(getAdapterPosition()).toggleOn = false;

                    }
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listOfTimers.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    TimerCreater.saving(listOfTimers, context);
                }
            });
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(context, EditTimer.class);
                    intent.putExtra("timerNum", position);
                    context.startActivity(intent);
                }
            });
            btnReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, listOfTimers.get(getAdapterPosition()).getLastClickedPhoto().toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
