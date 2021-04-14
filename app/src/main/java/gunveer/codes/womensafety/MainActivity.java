package gunveer.codes.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;




public class MainActivity extends AppCompatActivity {

    private static final String TAG = "onCreate...";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public static List<Timer> listOfTimers;

    FloatingActionButton btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recView);
        btnAddNew = findViewById(R.id.btnAddNew);

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            Gson gson = new Gson();
            String json = sharedPreferences.getString("listOfTimers", "");
            Type type = new TypeToken<List<Timer>>(){}.getType();
            listOfTimers = gson.fromJson(json, type);
            if(listOfTimers == null){
                listOfTimers = new ArrayList<>();
                Toast.makeText(this, "Creating a new list", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error at try catch on create", Toast.LENGTH_LONG).show();
            Log.d(TAG, "onCreate: " + e);
            listOfTimers = new ArrayList<>();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new RecViewAdapter(MainActivity.this, listOfTimers);
        recyclerView.setAdapter(adapter);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewTimer.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new RecViewAdapter(MainActivity.this, listOfTimers);
        recyclerView.setAdapter(adapter);
    }
}