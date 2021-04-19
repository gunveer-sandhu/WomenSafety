package gunveer.codes.womensafety;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddNewTimer extends AppCompatActivity {

    private static final String TAG = "whatever.......";
    private EditText etMinutes, etLabel;
    private Button btnOk;
    private EditText etMessage;
    private Button btnAddFromGallery, btnAddFromCamera;
    private EditText etContactNickname, etContactEmail, etContactNumber;
    private TextView tvContact, tvContact2, tvContact3;
    private Button btnAddContact;
    private EditText missedTimer;
    private CheckBox excludeLocation;
    private ImageView imageView, imageView2, imageView3;
    public static final int PICK_IMAGE_MULTIPLE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public int missedTimerInt;


    private List<Uri> imageUri;
    private List<String> imageUriString;
    private List<Contact> contactList;
    private int contactNum;




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
        etContactEmail = findViewById(R.id.etContactEmailEdit);
        tvContact = findViewById(R.id.tvContactEdit);
        tvContact2 = findViewById(R.id.tvContact2Edit);
        tvContact3 = findViewById(R.id.tvContact3Edit);
        btnAddContact = findViewById(R.id.btnAddContactEdit);

        missedTimer = findViewById(R.id.missedTimerEdit);
        excludeLocation = findViewById(R.id.excludeLocationEdit);




        btnAddFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageIntent();
            }
        });

        btnAddFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageUri.remove(0);
                    onImageDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageUri.remove(1);
                    onImageDeleteHandler();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageUri.remove(2);
                    onImageDeleteHandler();
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
                    imageUriString = imageUriToString(imageUri);
                    TimerCreater timerCreater = new TimerCreater(etLabel.getText().toString(), Integer.valueOf(String.valueOf(etMinutes.getText())), String.valueOf(etMessage.getText()), imageUriString, contactList,
                            missedTimerInt, excludeLocation.isChecked(), getApplicationContext());

                    Intent intent = new Intent(AddNewTimer.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void onImageDeleteHandler() {
        if(imageUri.size()==3){
            imageView.setImageURI(imageUri.get(0));
            imageView2.setImageURI(imageUri.get(1));
            imageView3.setImageURI(imageUri.get(2));
        }else if(imageUri.size()==2){
            imageView.setImageURI(imageUri.get(0));
            imageView2.setImageURI(imageUri.get(1));
            imageView3.setImageURI(null);
        }else if(imageUri.size()==1){
            imageView.setImageURI(imageUri.get(0));
            imageView2.setImageURI(null);
            imageView3.setImageURI(null);
        }else{
            imageView.setImageURI(null);
            imageView2.setImageURI(null);
            imageView3.setImageURI(null);
        }
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
        validateMinutes();
        if(validateMinutes()){
            validateMessage();
            if(validateMessage()){
                validateImages();
                if(validateImages()){
                    validateContacts();
                }if(validateContacts()){
                    validateLabel();
                }
            }
        }

        if(validateMinutes() && validateMessage() && validateImages() && validateContacts()){
            return true;
        }else{
            return false;
        }
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

    private boolean validateImages() {
        if(imageUri.isEmpty()){
            Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
            return false;
        }else if(imageUri.size() == 3){
            if(imageUri.get(0)==null && imageUri.get(1)==null && imageUri.get(2)==null){
                Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
                return false;
            }else{
                return true;
            }
        }else if(imageUri.size() == 2){
            if(imageUri.get(0)==null && imageUri.get(1)==null){
                Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
                return false;
            }else{
                return true;
            }
        }else if(imageUri.size() == 1){
            if(imageUri.get(0)==null){
                Toast.makeText(this, "Please attach at least one image.", Toast.LENGTH_LONG).show();
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
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
        if(validateEmail()==true && validateNickname()==true && validateNumber()==true){
            Toast.makeText(this, "Contact Added.", Toast.LENGTH_LONG).show();
            try{
                contactNum = Integer.parseInt(etContactNumber.getText().toString());
            }catch (Exception e){
                Toast.makeText(this, " ", Toast.LENGTH_SHORT).show();
            }
            
            Contact contact = new Contact(etContactNickname.getText().toString(),
                    contactNum, etContactEmail.getText().toString());
            if(contactList.size() == 0){
                contactList.add(contact);
                tvContact.setText(contactList.get(0).contactNickname);

            }else if(contactList.size() == 1){
                contactList.add(contact);
                tvContact2.setText(contactList.get(1).contactNickname);
            }else if(contactList.size() == 2){
                contactList.add(2, contact);
                tvContact3.setText(contactList.get(2).contactNickname);
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
        else if(validateEmail()==false){
            Toast.makeText(getApplicationContext(),"Enter a valid email address", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Contact not added", Toast.LENGTH_LONG).show();
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

    private boolean validateEmail() {
        String email = etContactEmail.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.matches(emailPattern)) {
            return true;
        }
        else {
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

                    if(data.getClipData().getItemCount() == 3 && imageUri.size() == 0){
                        for(int i=0; i<3;i++){
                            imageUri.add(i, data.getClipData().getItemAt(i).getUri());
                        }
                        imageView.setImageURI(imageUri.get(0));
                        imageView2.setImageURI(imageUri.get(1));
                        imageView3.setImageURI(imageUri.get(2));
                    }else if(data.getClipData().getItemCount() == 2 && imageUri.size() == 1){

                        imageUri.add(1, data.getClipData().getItemAt(0).getUri());
                        imageUri.add(2, data.getClipData().getItemAt(1).getUri());

                        imageView2.setImageURI(imageUri.get(1));
                        imageView3.setImageURI(imageUri.get(2));
                    }else{
                        Toast.makeText(this, "You have selected more images than you can add. Delete some to add more.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //single image selected

                    if (imageUri.size()==0) {
                        imageUri.add(0, data.getData());
                        imageView.setImageURI(imageUri.get(0));
                    }else if (imageUri.size()==1) {
                        imageUri.add(1, data.getData());
                        imageView2.setImageURI(imageUri.get(1));
                    }else if (imageUri.size()==2) {
                        imageUri.add(2, data.getData());
                        imageView3.setImageURI(imageUri.get(2));
                    }else{
                        Toast.makeText(this, "You already added 3 images. Delete one to add more.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }else if(requestCode == REQUEST_IMAGE_CAPTURE){
            if(resultCode == RESULT_OK){

                File f = new File(currentPhotoPath);
                if(imageUri.size()==0){
                    imageUri.add(0, Uri.fromFile(f));
                    imageView.setImageURI(imageUri.get(0));
                }else if(imageUri.size()==1){
                    imageUri.add(1, Uri.fromFile(f));
                    imageView2.setImageURI(imageUri.get(1));
                }else if(imageUri.size()==2){
                    imageUri.add(2, Uri.fromFile(f));
                    imageView3.setImageURI(imageUri.get(2));
                }else{
                    Toast.makeText(this, "You already added 3 images. Delete one to add more.", Toast.LENGTH_SHORT).show();
                }

                //This I guess is for adding the image to the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);


            }
        }
    }


}