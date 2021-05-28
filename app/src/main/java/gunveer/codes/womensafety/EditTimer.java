package gunveer.codes.womensafety;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static gunveer.codes.womensafety.MainActivity.listOfTimers;
import static gunveer.codes.womensafety.TimerCreater.saving;

public class EditTimer extends AppCompatActivity {

    private static final String TAG = "whatever.......";
    private EditText etMinutesEdit, etLabelEdit;
    private Button btnOkEdit;
    private EditText etMessageEdit;
    private Button btnAddFromGalleryEdit, btnAddFromCameraEdit;
    private EditText etContactNicknameEdit, etContactNumberEdit;
    private TextView tvContactEdit, tvContact2Edit, tvContact3Edit;
    private Button btnAddContactEdit;
    private EditText missedTimerEdit;
    private CheckBox excludeLocationEdit;
    private ImageView imageViewEdit, imageView2Edit, imageView3Edit;
    public static final int PICK_IMAGE_MULTIPLE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public int missedTimerIntEdit;


    private List<Uri> imageUri = new ArrayList<>();
    private Map<String, String> imageUriString = new HashMap<>();
    private List<String> imageUrls = new ArrayList<>();
    private List<Contact> contactListEdit = new ArrayList<>();;
    private Long contactNum;
    public Handler mainHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_timer);

        etLabelEdit = findViewById(R.id.etLabelEdit);
        etMinutesEdit = findViewById(R.id.etMinutesEdit);
        etMessageEdit = findViewById(R.id.etMessageEdit);
        btnAddFromGalleryEdit = findViewById(R.id.btnAddFromGalleryEdit);
        btnAddFromCameraEdit = findViewById(R.id.btnAddFromCameraEdit);
        imageViewEdit = findViewById(R.id.imageViewEdit);
        imageView2Edit = findViewById(R.id.imageView2Edit);
        imageView3Edit = findViewById(R.id.imageView3Edit);
        etContactNicknameEdit = findViewById(R.id.etContactNicknameEdit);
        etContactNumberEdit = findViewById(R.id.etContactNumberEdit);
        tvContactEdit = findViewById(R.id.tvContactEdit);
        tvContact2Edit = findViewById(R.id.tvContact2Edit);
        tvContact3Edit = findViewById(R.id.tvContact3Edit);
        btnAddContactEdit = findViewById(R.id.btnAddContactEdit);
        missedTimerEdit = findViewById(R.id.missedTimerEdit);
        excludeLocationEdit = findViewById(R.id.excludeLocationEdit);
        btnOkEdit = findViewById(R.id.btnOkEdit);


        Intent intent = getIntent();
        int position = intent.getIntExtra("timerNum", -1);
//        if(position == -1){
//            Toast.makeText(this, "Timer num cannot be retrieved", Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(this, "position is "+position, Toast.LENGTH_SHORT).show();
//        }

        imageUriString =  listOfTimers.get(position).getLastClickedPhoto();
        Set links = imageUriString.keySet();
        Iterator iterator = links.iterator();

        while(iterator.hasNext()){
            imageUrls.add(iterator.next().toString());
        }

        Log.d(TAG, "onCreate: "+imageUrls.toString());


        etLabelEdit.setText(listOfTimers.get(position).getLabel());
        etMinutesEdit.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
        etMessageEdit.setText(listOfTimers.get(position).getMessage());

        if(imageUrls.size()==3){
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2Edit);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(2))
                    .into(imageView3Edit);
        }else if(imageUrls.size()==2){
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2Edit);
        }else{
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
        }



        contactListEdit = listOfTimers.get(position).contactsToAlert;

        if(contactListEdit.size()==3){
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
            tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
            tvContact3Edit.setText(contactListEdit.get(2).contactNickname);
        }else if(contactListEdit.size()==2){
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
            tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
        }else{
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
        }

        missedTimerEdit.setText(String.valueOf(listOfTimers.get(position).getMissedTimer()));
        excludeLocationEdit.setChecked(listOfTimers.get(position).excludeLocation);

        // code from addNewTimer to be edited..

        btnAddFromGalleryEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUriString.size() < 3) {
                    pickImageIntent();
                }else{
                    Toast.makeText(EditTimer.this, "Only 3 images can be added. Delete some to add more.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddFromCameraEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUriString.size() < 3) {
                    capturePhoto();
                }else{
                    Toast.makeText(EditTimer.this, "Only 3 images can be added. Delete some to add more.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 0).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView2Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 1).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView3Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncDeleter(imageUriString.get(imageUrls.get(0)), 2).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnAddContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact();
            }
        });

        tvContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactListEdit.remove(0);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvContact2Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactListEdit.remove(1);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tvContact3Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    contactListEdit.remove(2);
                    onContactDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        btnOkEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyOkBtn();
                if(verifyOkBtn()){
//                    imageUriString = imageUriToString(imageUri);

//                    TimerCreater timerCreater = new TimerCreater(etLabel.getText().toString(), Integer.valueOf(String.valueOf(etMinutes.getText())), String.valueOf(etMessage.getText()), imageUriString, contactList,
//                            missedTimerInt, excludeLocation.isChecked(), getApplicationContext());

                    Timer timer = new Timer(0, etLabelEdit.getText().toString(), Integer.valueOf(etMinutesEdit.getText().toString()), missedTimerIntEdit, listOfTimers.get(position).isToggleOn(),
                             imageUriString, etMessageEdit.getText().toString(), contactListEdit, null, excludeLocationEdit.isChecked());

                    listOfTimers.set(position, timer);
                    saving(listOfTimers, EditTimer.this);

                    Intent intent = new Intent(EditTimer.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Try using 'Save Edit'.", Toast.LENGTH_SHORT).show();
        return;
    }

    private void onImageDeleteHandler() {
        if(imageUrls.size()==3){
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2Edit);
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(2))
                    .into(imageView3Edit);
        }else if(imageUrls.size()==2){
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(1))
                    .into(imageView2Edit);
            imageView3Edit.setImageDrawable(null);
        }else if(imageUrls.size()==1){
            Glide.with(EditTimer.this)
                    .asBitmap()
                    .load(imageUrls.get(0))
                    .into(imageViewEdit);
            imageView2Edit.setImageDrawable(null);
            imageView3Edit.setImageDrawable(null);
        }else{
            imageViewEdit.setImageDrawable(null);
            imageView2Edit.setImageDrawable(null);
            imageView3Edit.setImageDrawable(null);
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
        if(contactListEdit.size()==3){
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
            tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
            tvContact3Edit.setText(contactListEdit.get(2).contactNickname);
        }else if(contactListEdit.size()==2){
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
            tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
            tvContact3Edit.setText("");
        }else if(contactListEdit.size()==1){
            tvContactEdit.setText(contactListEdit.get(0).contactNickname);
            tvContact2Edit.setText("");
            tvContact3Edit.setText("");
        }else{
            tvContactEdit.setText("");
            tvContact2Edit.setText("");
            tvContact3Edit.setText("");
        }
    }




    public void missedTimerHandler() {
        if(missedTimerEdit.getText().toString().isEmpty()){
            missedTimerIntEdit = 1;
            Toast.makeText(this, "You did'nt set missed timer. Setting it to 1 (one).", Toast.LENGTH_LONG).show();
        }else{
            missedTimerIntEdit = Integer.parseInt(missedTimerEdit.getText().toString());
            if(missedTimerIntEdit==0){
                missedTimerIntEdit = 1;
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
        if(etLabelEdit.getText().toString().isEmpty()){
            etLabelEdit.setText("Default label");
        }
    }

    private boolean validateContacts() {
        if(contactListEdit.isEmpty()){
            Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(contactListEdit.size() ==3){
            if(contactListEdit.get(0)==null && contactListEdit.get(1)==null && contactListEdit.get(2)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }

        }else if(contactListEdit.size()==2){
            if(contactListEdit.get(0)==null && contactListEdit.get(1)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        }else if(contactListEdit.size()==1){
            if(contactListEdit.get(0)==null){
                Toast.makeText(this, "Please add at least one contact to send message to.", Toast.LENGTH_SHORT).show();
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }

    }

    private boolean validateImages() {
        if(imageUriString.isEmpty()){
            Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validateMessage() {
        if(String.valueOf(etMessageEdit.getText()).isEmpty()){
            Toast.makeText(this, "Message can not be empty.", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean validateMinutes() {
        if(String.valueOf(etMinutesEdit.getText()) == "0" || String.valueOf(etMinutesEdit.getText()).isEmpty()){
            Toast.makeText(this, "Enter Minutes of Timer", Toast.LENGTH_LONG).show();
            return false;
        }else{
            return true;
        }
    }

    private void addContact() {
        if(validateNickname()==true && validateNumber()==true){
            Toast.makeText(this, "Contact Added.", Toast.LENGTH_SHORT).show();

            Contact contact = new Contact(etContactNicknameEdit.getText().toString(),
                    contactNum);
            if(contactListEdit.size()==0){
                contactListEdit.add(0, contact);
                tvContactEdit.setText(contactListEdit.get(0).contactNickname);
                etContactNicknameEdit.setText("");
                etContactNumberEdit.setText("");
            }else if(contactListEdit.size()==1){
                contactListEdit.add(1, contact);
                tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
                etContactNicknameEdit.setText("");
                etContactNumberEdit.setText("");
            }else if(contactListEdit.size()==2){
                contactListEdit.add(2, contact);
                tvContact3Edit.setText(contactListEdit.get(2).contactNickname);
                etContactNicknameEdit.setText("");
                etContactNumberEdit.setText("");
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
        String phone = etContactNumberEdit.getText().toString();

        if (phone.length() == 10) {
            try{
                contactNum = Long.parseLong(etContactNumberEdit.getText().toString());
            }catch (Exception e){
                Toast.makeText(this, "Some error occurred during number entry", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else{
            return false;
        }
    }

    private boolean validateNickname() {
        String nickname = etContactNicknameEdit.getText().toString().trim();

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
                Toast.makeText(this, " "+ photoURI, Toast.LENGTH_LONG).show();
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

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditTimer.this);
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
                            Toast.makeText(EditTimer.this, "Image cannot be deleted. Check your internet connection.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException | JSONException e) {
                Log.d(TAG, "imgurUpload: catching error " + e);
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditTimer.this, "One of the photo may not be deleted. Please try again." +
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

    private class AsyncUploader extends AsyncTask<List<Uri>, Integer, Map<String, String>> {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditTimer.this);
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
                                Toast.makeText(EditTimer.this, "Images cannot be uploaded. Check your internet connection.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (IOException | JSONException e) {
                    Log.d(TAG, "imgurUpload: catching error " + e);
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EditTimer.this, "One of the photo may not be uploaded. Please try again." +
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
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageViewEdit);
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(1))
                                .into(imageView2Edit);
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(2))
                                .into(imageView3Edit);
                    }else if(imageUrls.size()==2){
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageViewEdit);
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(1))
                                .into(imageView2Edit);
                    }else{
                        Glide.with(EditTimer.this)
                                .asBitmap()
                                .load(imageUrls.get(0))
                                .into(imageViewEdit);
                    }
                }
            });
            return imageUriString;
        }
    }
}
