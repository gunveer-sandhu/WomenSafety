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
import static gunveer.codes.womensafety.MainActivity.listOfTimers;
import static gunveer.codes.womensafety.TimerCreater.saving;

public class EditTimer extends AppCompatActivity {

    private static final String TAG = "whatever.......";
    private EditText etMinutesEdit, etLabelEdit;
    private Button btnOkEdit;
    private EditText etMessageEdit;
    private Button btnAddFromGalleryEdit, btnAddFromCameraEdit;
    private EditText etContactNicknameEdit, etContactEmailEdit, etContactNumberEdit;
    private TextView tvContactEdit, tvContact2Edit, tvContact3Edit;
    private Button btnAddContactEdit;
    private EditText missedTimerEdit;
    private CheckBox excludeLocationEdit;
    private ImageView imageViewEdit, imageView2Edit, imageView3Edit;
    public static final int PICK_IMAGE_MULTIPLE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public int missedTimerIntEdit;


    private List<Uri> imageUri = new ArrayList<>();;
    private List<String> imageUriString = new ArrayList<>();;
    private List<Contact> contactListEdit = new ArrayList<>();;
    private int contactNum;

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
        etContactEmailEdit = findViewById(R.id.etContactEmailEdit);
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
        for(int i = 0; i<imageUriString.size(); i++){
            imageUri.add(i,Uri.parse(imageUriString.get(i)));
        }


        etLabelEdit.setText(listOfTimers.get(position).getLabel());
        etMinutesEdit.setText(String.valueOf(listOfTimers.get(position).getMinutes()));
        etMessageEdit.setText(listOfTimers.get(position).getMessage());

        if(imageUri.size()==3){
            imageViewEdit.setImageURI(imageUri.get(0));
            imageView2Edit.setImageURI(imageUri.get(1));
            imageView3Edit.setImageURI(imageUri.get(2));
        }else if(imageUri.size()==2){
            imageViewEdit.setImageURI(imageUri.get(0));
            imageView2Edit.setImageURI(imageUri.get(1));
        }else{
            imageViewEdit.setImageURI(imageUri.get(0));
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
                pickImageIntent();
            }
        });

        btnAddFromCameraEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });

        imageViewEdit.setOnClickListener(new View.OnClickListener() {
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

        imageView2Edit.setOnClickListener(new View.OnClickListener() {
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

        imageView3Edit.setOnClickListener(new View.OnClickListener() {
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
                    imageUriString = imageUriToString(imageUri);

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

    private void onImageDeleteHandler() {
        if(imageUri.size()==3){
            imageViewEdit.setImageURI(imageUri.get(0));
            imageView2Edit.setImageURI(imageUri.get(1));
            imageView3Edit.setImageURI(imageUri.get(2));
        }else if(imageUri.size()==2){
            imageViewEdit.setImageURI(imageUri.get(0));
            imageView2Edit.setImageURI(imageUri.get(1));
            imageView3Edit.setImageURI(null);
        }else if(imageUri.size()==1){
            imageViewEdit.setImageURI(imageUri.get(0));
            imageView2Edit.setImageURI(null);
            imageView3Edit.setImageURI(null);
        }else{
            imageViewEdit.setImageURI(null);
            imageView2Edit.setImageURI(null);
            imageView3Edit.setImageURI(null);
        }
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


    private List<String> imageUriToString(List<Uri> imageUri) {
        List<String> test = new ArrayList<>();
        for(int i=0; i<imageUri.size(); i++){
            String trya = String.valueOf(imageUri.get(i));
            test.add(i, trya);
        }
        return test;
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
        if(validateEmail()==true && validateNickname()==true && validateNumber()==true){
            Toast.makeText(this, "Contact Added.", Toast.LENGTH_LONG).show();

            Contact contact = new Contact(etContactNicknameEdit.getText().toString(),
                    contactNum, etContactEmailEdit.getText().toString());
            if(contactListEdit.size()==0){
                contactListEdit.add(0, contact);
                tvContactEdit.setText(contactListEdit.get(0).contactNickname);

            }else if(contactListEdit.size()==1){
                contactListEdit.add(1, contact);
                tvContact2Edit.setText(contactListEdit.get(1).contactNickname);
            }else if(contactListEdit.size()==2){
                contactListEdit.add(2, contact);
                tvContact3Edit.setText(contactListEdit.get(2).contactNickname);
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
        String phone = etContactNumberEdit.getText().toString();

        if (phone.length() == 10) {
            try{
                contactNum = Integer.parseInt(etContactNumberEdit.getText().toString());
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

    private boolean validateEmail() {
        String email = etContactEmailEdit.getText().toString().trim();

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

                    if(data.getClipData().getItemCount() == 3 && imageUri.size() == 0){
                        for(int i=0; i<3;i++){
                            imageUri.add(i, data.getClipData().getItemAt(i).getUri());
                        }
                        imageViewEdit.setImageURI(imageUri.get(0));
                        imageView2Edit.setImageURI(imageUri.get(1));
                        imageView3Edit.setImageURI(imageUri.get(2));
                    }else if(data.getClipData().getItemCount() == 2 && imageUri.size() == 1){

                        imageUri.add(1, data.getClipData().getItemAt(0).getUri());
                        imageUri.add(2, data.getClipData().getItemAt(1).getUri());

                        imageView2Edit.setImageURI(imageUri.get(1));
                        imageView3Edit.setImageURI(imageUri.get(2));
                    }else{
                        Toast.makeText(this, "You have selected more images than you can add. Delete some to add more.", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //single image selected

                    if (imageUri.size()==0) {
                        imageUri.add(0, data.getData());
                        imageViewEdit.setImageURI(imageUri.get(0));
                    }else if (imageUri.size()==1) {
                        imageUri.add(1, data.getData());
                        imageView2Edit.setImageURI(imageUri.get(1));
                    }else if (imageUri.size()==2) {
                        imageUri.add(2, data.getData());
                        imageView3Edit.setImageURI(imageUri.get(2));
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
                    imageViewEdit.setImageURI(imageUri.get(0));
                }else if(imageUri.size()==1){
                    imageUri.add(1, Uri.fromFile(f));
                    imageView2Edit.setImageURI(imageUri.get(1));
                }else if(imageUri.size()==2){
                    imageUri.add(2, Uri.fromFile(f));
                    imageView3Edit.setImageURI(imageUri.get(2));
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