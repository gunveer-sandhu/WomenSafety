package gunveer.codes.womensafety;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddNewTimer extends AppCompatActivity {

    private static final String TAG = "whatever.......";
    private EditText etMinutes, etLabel;
    private Button btnOk;
    private EditText etMessage;
    private Button btnAddFromGallery, btnAddFromCamera;
    private EditText etContactNickname, etContactNumber;
    private TextView tvContact, tvContact2, tvContact3;
    private Button btnAddContact;
    private EditText missedTimer;
    private CheckBox excludeLocation;
    private ImageView imageView, imageView2, imageView3;
    public static final int PICK_IMAGE_MULTIPLE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public int missedTimerInt;

    private List<Uri> imageUri;
    private List<String> imageUrls = new ArrayList<>();
    public Map<String, String> imageUriString = new HashMap<>();
    private List<Contact> contactList;
    private Long contactNum;
    public Handler mainHandler = new Handler();
    public ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_timer);


        etLabel = findViewById(R.id.etLabelEdit);
        etMinutes = findViewById(R.id.etMinutesEdit);
        btnOk = findViewById(R.id.btnOkEdit);
        etMessage = findViewById(R.id.etMessageEdit);
        btnAddFromGallery = findViewById(R.id.btnAddFromGalleryEdit);
        btnAddFromCamera = findViewById(R.id.btnAddFromCameraEdit);
        imageView = findViewById(R.id.imageViewEdit);
        imageView2 = findViewById(R.id.imageView2Edit);
        imageView3 = findViewById(R.id.imageView3Edit);


        imageUri = new ArrayList<>();
        contactList = new ArrayList<>();


        etContactNickname = findViewById(R.id.etContactNicknameEdit);
        etContactNumber = findViewById(R.id.etContactNumberEdit);
        tvContact = findViewById(R.id.tvContactEdit);
        tvContact2 = findViewById(R.id.tvContact2Edit);
        tvContact3 = findViewById(R.id.tvContact3Edit);
        btnAddContact = findViewById(R.id.btnAddContactEdit);

        missedTimer = findViewById(R.id.missedTimerEdit);
        excludeLocation = findViewById(R.id.excludeLocationEdit);
        progressBar = findViewById(R.id.progressBarDelete);




        btnAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUriString.size() < 3) {
                    pickImageIntent();
                }else{
                    Toast.makeText(AddNewTimer.this, "Only 3 images can be added. Delete some to add more.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUriString.size() < 3) {
                    capturePhoto();
                }else{
                    Toast.makeText(AddNewTimer.this, "Only 3 images can be added. Delete some to add more.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 0).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 1).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 2).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactList.remove(0);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactList.remove(1);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactList.remove(2);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOkBtn();
                if(verifyOkBtn()){

                    TimerCreater timerCreater = new TimerCreater(etLabel.getText().toString(), Integer.valueOf(String.valueOf(etMinutes.getText())), String.valueOf(etMessage.getText()), imageUriString, contactList,
                            missedTimerInt, excludeLocation.isChecked(), getApplicationContext());

                    Intent intent = new Intent(AddNewTimer.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    private void onImageDeleteHandler() {
        if(imageUrls.size()==3){
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageView);
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2);
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(2))
                    .into(imageView3);
        }else if(imageUrls.size()==2){
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageView);
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2);
            imageView3.setImageDrawable(null);
        }else if(imageUrls.size()==1){
            Glide.with(AddNewTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageView);
            imageView2.setImageDrawable(null);
            imageView3.setImageDrawable(null);
        }else{
            imageView.setImageDrawable(null);
            imageView2.setImageDrawable(null);
            imageView3.setImageDrawable(null);
        }
    }

    private String encodeImage(Uri uri) {
            String encImage = "";
            try {
                final InputStream imageStream;
                imageStream = getContentResolver().openInputStream(uri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG,50,baos);
                byte[] b = baos.toByteArray();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    encImage= Base64.getEncoder().encodeToString(b);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return encImage;
    }



    private void onContactDeleteHandler() {
        if(contactList.size()==3){
            tvContact.setText(contactList.get(0).contactNickname);
            tvContact2.setText(contactList.get(1).contactNickname);
            tvContact3.setText(contactList.get(2).contactNickname);
        }else if(contactList.size()==2){
            tvContact.setText(contactList.get(0).contactNickname);
            tvContact2.setText(contactList.get(1).contactNickname);
            tvContact3.setText("");
        }else if(contactList.size()==1){
            tvContact.setText(contactList.get(0).contactNickname);
            tvContact2.setText("");
            tvContact3.setText("");
        }else{
            tvContact.setText("");
            tvContact2.setText("");
            tvContact3.setText("");
        }
    }


    private List<String> imageUriToString(List<Uri> imageUri) {
        List<String> test = new ArrayList<>();
        for(int i=0; i<imageUri.size(); i++){
            String trya = imageUri.get(i).toString();
            test.add(i, trya);
        }
        return test;
    }


    public void missedTimerHandler() {
        if(missedTimer.getText().toString().isEmpty() || missedTimer.getText().toString() == "0"){
            missedTimerInt = 1;
            Toast.makeText(this, "You did'nt set missed timer. Setting it to 1 (one).", Toast.LENGTH_LONG).show();
        }else{
            missedTimerInt = Integer.parseInt(missedTimer.getText().toString());
            if(missedTimerInt==0){
                missedTimerInt = 1;
            }
        }
    }


    private boolean verifyOkBtn() {
        missedTimerHandler();
        if(validateMinutes()){
            if(validateMessage()){
                if(validateImages()){
                    if(validateContacts()){
                        validateLabel();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void validateLabel() {
        if(etLabel.getText().toString().isEmpty()){
            etLabel.setText("Default label");
        }
    }

    private boolean validateContacts() {
        if(contactList.isEmpty()){
            Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(contactList.size() ==3){
            if(contactList.get(0)==null && contactList.get(1)==null && contactList.get(2)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }

        }else if(contactList.size()==2){
            if(contactList.get(0)==null && contactList.get(1)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        }else if(contactList.size()==1){
            if(contactList.get(0)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        }
        else{
            return true;
        }

    }

    //edit validate images according to Async handler
    private boolean validateImages() {
        if(imageUriString.isEmpty()){
            Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validateMessage() {
        if(String.valueOf(etMessage.getText()).isEmpty()){
            Toast.makeText(this, "Message can not be empty.", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean validateMinutes() {
        if(String.valueOf(etMinutes.getText()) == "0" || String.valueOf(etMinutes.getText()).isEmpty()){
            Toast.makeText(this, "Enter Minutes of Timer", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void addContact() {
        if(validateNickname()==true && validateNumber()==true){
            Toast.makeText(this, "Contact Added.", Toast.LENGTH_SHORT).show();
            contactNum = Long.parseLong(String.valueOf(etContactNumber.getText()));
//            Toast.makeText(this, String.valueOf(contactNum), Toast.LENGTH_SHORT).show();
            
            Contact contact = new Contact(etContactNickname.getText().toString(),
                    contactNum);
            if(contactList.size() == 0){
                contactList.add(contact);
                tvContact.setText(contactList.get(0).contactNickname);
                etContactNickname.setText("");
                etContactNumber.setText("");
            }else if(contactList.size() == 1){
                contactList.add(contact);
                tvContact2.setText(contactList.get(1).contactNickname);
                etContactNickname.setText("");
                etContactNumber.setText("");
            }else if(contactList.size() == 2){
                contactList.add(2, contact);
                tvContact3.setText(contactList.get(2).contactNickname);
                etContactNickname.setText("");
                etContactNumber.setText("");
            }else{
                Toast.makeText(this, "You can add only 3 contacts. Delete one to add more.", Toast.LENGTH_SHORT).show();
            }

        }
        else if(validateNickname()==false){
            Toast.makeText(this, "Enter a valid name.", Toast.LENGTH_LONG).show();
        }
        else if(validateNumber()==false){
            Toast.makeText(this, "Enter a valid number.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateNumber() {
        String phone = etContactNumber.getText().toString();

        if (phone.length() == 10) {
            return true;
        }else{
            return false;
        }
    }

    private boolean validateNickname() {
        String nickname = etContactNickname.getText().toString().trim();

        if(nickname.matches("^[A-Za-z]+$")){
            return true;
        }else{
            return false;
        }
    }


    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void capturePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Something went wrong "+ ex, Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "gunveer.codes.android.womensafety",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }



    public void pickImageIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT).setFlags(intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                                                                | intent.FLAG_GRANT_READ_URI_PERMISSION | intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_MULTIPLE_GALLERY){
            if(resultCode == Activity.RESULT_OK){
                if(data.getClipData() != null){
                    //multiple images selected
                    if(data.getClipData().getItemCount() == 3){
                        for(int i=0; i<3;i++){
                            imageUri.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }else if(data.getClipData().getItemCount() == 2){
                        for(int i=0; i<2;i++){
                            imageUri.add(data.getClipData().getItemAt(i).getUri());
                        }
                    }
                }else{
                    //single image selected
                    imageUri.add(data.getData());
                }
                new AsyncUploader().execute(imageUri);
            }
        }else if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){
                File f = new File(currentPhotoPath);
                imageUri.add(Uri.fromFile(f));
                new AsyncUploader().execute(imageUri);

                //This I guess is for adding the image to the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }
    }

    private class AsyncDeleter extends  AsyncTask<String, Integer, String>{

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddNewTimer.this);
        AlertDialog alertDialog;
        String deleteHash;
        int index;
        public AsyncDeleter(String s, int i) {
            this.deleteHash = s;
            this.index = i;
        }

        public AlertDialog.Builder getAlertBuilder() {
            alertBuilder.setView(R.layout.delete_alert_dialog_layout)
                    .setCancelable(false);
            return alertBuilder;
        }

        Response response = null;
        @Override
        protected void onPreExecute() {
            alertDialog = getAlertBuilder().create();
            alertDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            alertDialog.dismiss();
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, "{}");
            Request request = new Request.Builder()
                    .url("https://api.imgur.com/3/image/"+deleteHash)
                    .method("DELETE", body)
                    .addHeader("Authorization", "Client-ID 89bf146742231d7")
                    .build();
            try {
                response = client.newCall(request).execute();
                String responser = response.body().string();
                JSONObject object = new JSONObject(responser);
                String status = object.getString("status");
                if(status.contains("200")){
                    imageUriString.remove(imageUrls.get(index));
                    imageUrls.remove(index);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onImageDeleteHandler();
                        }
                    });
                }else{
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewTimer.this, "Image cannot be deleted. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException | JSONException e) {
                Log.d(TAG, "imgurUpload: catching error " + e);
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddNewTimer.this, "One of the photo may not be deleted. Please try again." +
                                " Check your internet connection.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if(response!=null){
                response.body().close();
            }
            return null;
        }
    }

    private class AsyncUploader extends AsyncTask<List<Uri>, Integer, Map<String, String>>{
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddNewTimer.this);
        AlertDialog alertDialog;
        public AlertDialog.Builder getAlertBuilder() {
            alertBuilder.setView(R.layout.upload_alert_dialog_layout)
                    .setCancelable(false);
            return alertBuilder;
        }

        Response response = null;


        @Override
        protected void onPreExecute() {

            alertDialog = getAlertBuilder().create();
            alertDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            alertDialog.dismiss();
            super.onPostExecute(stringStringMap);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Map<String, String> doInBackground(List<Uri>... lists) {

            for(int i = 0; i< imageUri.size(); i++) {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("image", encodeImage(imageUri.get(i)))
                        .build();
                Request request = new Request.Builder()
                        .url("https://api.imgur.com/3/image")
                        .method("POST", body)
                        .addHeader("Authorization", "Client-ID 89bf146742231d7")
                        .build();

                try {
                    response = client.newCall(request).execute();
                    String responser = response.body().string();
                    JSONObject object = new JSONObject(responser);
                    JSONObject jsonObject = object.getJSONObject("data");
                    String link = jsonObject.getString("link");
                    String deleteHash = jsonObject.getString("deletehash");
                    Log.d(TAG, "onResponse: response is  " + object);
                    String status = object.getString("status");
                    Log.d(TAG, "run: " + link);

                    if (status.contains("200")) {
                        imageUriString.put(link, deleteHash);
                        imageUrls.add(link);
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddNewTimer.this, "Images cannot be uploaded. Check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (IOException | JSONException e) {
                    Log.d(TAG, "imgurUpload: catching error " + e);
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddNewTimer.this, "One of the photo may not be uploaded. Please try again." +
                                    " Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
            if(response!=null){
                response.body().close();
                imageUri.removeAll(imageUri);
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(imageUrls.size()==3){
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageView);
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(1))
                                .into(imageView2);
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(2))
                                .into(imageView3);
                    }else if(imageUrls.size()==2){
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageView);
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(1))
                                .into(imageView2);
                    }else if(imageUrls.size()==1){
                        Glide.with(AddNewTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageView);
                    }
                }
            });
            return imageUriString;
        }
    }
}