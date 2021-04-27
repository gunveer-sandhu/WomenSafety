package gunveer.codes.womensafety;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
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
    private static final int PERMISSION_ID = 44;
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
//                Toast.makeText(this, "Creating a new list", Toast.LENGTH_LONG).show();
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
        if (!checkPermissions()) {
            requestPermissions();
        }
        if(!checkSmsPermission()){
            requestSmsPermission();
        }


        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendWhatsapp();
                if(verifyAllOff()){
                    Intent intent = new Intent(MainActivity.this, AddNewTimer.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Turn off running timer to add new.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS}, PERMISSION_ID);
    }


    public boolean verifyAllOff() {
        for(int i =0; i<listOfTimers.size(); i++){
            if(listOfTimers.get(i).toggleOn){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new RecViewAdapter(MainActivity.this, listOfTimers);
        recyclerView.setAdapter(adapter);
//        requestPermissionBackground();
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.SEND_SMS}, PERMISSION_ID);
        requestPermissionBackground();
    }
    private void requestPermissionBackground(){
        Log.d(TAG, "requestPermissionBackground: here");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M || true){
            Log.d(TAG, "requestPermissions: " + shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION));
            if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION) || true){
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                alertDialog.setTitle("Functionality Limited");
                alertDialog.setMessage("We have no access to the location, it saves on your device, and is requested only once when SOS expires. Go to--" +
                        " Permission -> Location -> Allow all the time.");
                alertDialog.setPositiveButton("Give permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                alertDialog.setNegativeButton("Go Unsafe", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Might not include location in SOS", Toast.LENGTH_LONG).show();
                    }
                });
                alertDialog.show();
            }

        }else{
            Log.d(TAG, "requestPermissions: returns false");
        }
    }

//    @Override
//    public void
//    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == PERMISSION_ID) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                getLastLocation();
//            }
//        }
//    }
    private boolean checkPermissions(){
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    ;
        }else{
            boolean a = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
            return a;
        }
    }
    private boolean checkSmsPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

}

