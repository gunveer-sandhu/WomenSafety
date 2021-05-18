package gunveer.codes.staysafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
// client id 796819371137-qojud44cohoknvcdkm3kvoadmsblot0d.apps.googleusercontent.com
    private static final String TAG = "onCreate...";
    private static final int PERMISSION_ID = 44;
    private static final int ACCOUNT_PICKER_CODE = 45;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 46;
    public String fileName = "";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public static GoogleAccountCredential credential;


    public static List<Timer> listOfTimers;
    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.MAIL_GOOGLE_COM,
            GmailScopes.GMAIL_SEND
    };
    

    FloatingActionButton btnAddNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recView);
        btnAddNew = findViewById(R.id.btnAddNew);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

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
        permissionSaga();

        



        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(verifyAllOff() && permissionSaga()){
                    Intent intent = new Intent(MainActivity.this, AddNewTimer.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Turn off running timer to add new.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private boolean permissionSaga() {
        if (!checkPermissions()) {
            requestPermissions();
        }
        if(!checkSmsPermission()){
            requestSmsPermission();
        }
        if(credential.getSelectedAccountName()==null){
            requestGmailPermissions();
        }if(!isGooglePlayServicesAvailable()){
            acquireGooglePlayServices();
        }
            return true;

    }
    // Method for Checking Google Play Service is Available
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    // Method to Show Info, If Google Play Service is Not Available.
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    // Method for Google Play Services Error Info
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                MainActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCOUNT_PICKER_CODE) {
            if(resultCode==RESULT_OK && data!=null && data.getExtras()!=null){
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                if(accountName!=null){
                    SharedPreferences accounts = getSharedPreferences("accountNamePref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = accounts.edit();
                    editor.putString("accountName", accountName);
                    editor.apply();
                    credential.setSelectedAccountName(accountName);
                }
            }
        }if(requestCode==REQUEST_GOOGLE_PLAY_SERVICES){
            if(resultCode!=RESULT_OK){
                Toast.makeText(this, "Google Play services required." +
                        " Please enable Google Play Services and relaunch the app.", Toast.LENGTH_LONG).show();
            }else{

            }
        }
    }

    private void requestGmailPermissions() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    PERMISSION_ID);
        }
        String accountName = getSharedPreferences("accountNamePref", Context.MODE_PRIVATE).getString("accountName", null);
        if(accountName==null){
            credential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());
            startActivityForResult(credential.newChooseAccountIntent(),
                    ACCOUNT_PICKER_CODE);
        }else if(accountName!=null){
            credential.setSelectedAccountName(accountName);
        }
//        permissionSaga();

    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS}, PERMISSION_ID);
//        permissionSaga();
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
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSION_ID);
        }else{
            requestPermissionBackground();
        }
//        permissionSaga();
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
//                requestGmailPermissions();
//            }
//        }
//    }
    private boolean checkPermissions(){
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }else{
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }
    private boolean checkSmsPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

}

