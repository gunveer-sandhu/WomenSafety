package gunveer.codes.staysafe;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.sql.DataSource;

import static gunveer.codes.staysafe.MainActivity.listOfTimers;


class MakeRequestTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "MakeRequestTask";
    @SuppressLint("StaticFieldLeak")
    private MainActivity activity;
    private GoogleAccountCredential credential;
    int position;
    int contactPosition;
    String locationLink;
    private com.google.api.services.gmail.Gmail mService = null;

    public MakeRequestTask(GoogleAccountCredential credential,
                           int position, int contactPosition, String locationLink) {
        this.activity = activity;
        this.credential = credential;
        this.position = position;
        this.contactPosition = contactPosition;
        this.locationLink = locationLink;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName(String.valueOf((R.string.app_name)))
                .build();
        Log.d(TAG, "MakeRequestTask: ");
    }

    @Override
    protected String doInBackground(Void... voids) {
        try{
            Log.d(TAG, "doInBackground: not exception");
            return getDataFromApi();

        }catch (Exception e){
            Log.d(TAG, "doInBackground: "+ e);
            cancel(true);
            return null;
        }
    }

    private String getDataFromApi() throws IOException {

        String user = "me";

        String to = listOfTimers.get(position).getContactsToAlert()
                .get(contactPosition).getContactEmail();
        String from  = credential.getSelectedAccountName();
        String subject = listOfTimers.get(position).getLabel().toString()
                + " A SOS sent from Stay Safe App.";
        String body = "SOS Message: " + listOfTimers.get(position).getMessage().toString()
                + "\n"
                + "SOS Location: "+ locationLink
                + "\n"
                + "SOS Images are attached with the mail."
                + "\n"
                + "It is advisory to check up on the sender to ensure their well being."
                + "\n"
                + "This is an automated email by Stay Safe App.";
        String response = "";
        MimeMessage mimeMessage;
        try{
            mimeMessage = createEmail(to, from, subject, body);
            response = sendMessage(mService, user,mimeMessage);
        }catch (MessagingException e){
            e.printStackTrace();
        }
        return response;
    }

    private String sendMessage(Gmail service,
                               String user,
                               MimeMessage email)
                    throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        // GMail's official method to send email with oauth2.0
        message = service.users().messages().send(user, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message.getId();

    }
    private Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        email.writeTo(bytes);
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }


    private MimeMessage createEmail(String to,
                                    String from,
                                    String subject,
                                    String body) throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(fAddress);
        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
        email.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        BodyPart textBody = new MimeBodyPart();
        textBody.setText(body);
        multipart.addBodyPart(textBody);

        if(true){
            MimeBodyPart attachmentBody = new MimeBodyPart();
            String filename = getPathFromURI(
                    Uri.parse(listOfTimers.get(position).getLastClickedPhoto().get(0))
            );
            FileDataSource source = new FileDataSource(filename);
            attachmentBody.setDataHandler(new DataHandler(source));
            attachmentBody.setFileName(filename);
            multipart.addBodyPart(attachmentBody);
        }

        email.setContent(multipart);

        return email;

    }
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(contentUri, proj, "", null, "");
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
