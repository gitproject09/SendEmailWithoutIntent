package com.sopan.sendemail.withoutintent;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMailActivity extends AppCompatActivity {

    EditText subject, text;

    Button sendEmailButton;

    String subjectString, textString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_mail);

        subject = findViewById(R.id.subjectEditText);
        text =  findViewById(R.id.textEditText);

        sendEmailButton = findViewById(R.id.button);

        final SendemailFromBuild sendemailFromBuild = new SendemailFromBuild();

        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subjectString = subject.getText().toString();
                textString = text.getText().toString();

                if (isOnline()) {
                    //sendEmailToAlorSathi();
                    sendemailFromBuild.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public class SendemailFromBuild extends AsyncTask<String, Integer, Integer> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SendMailActivity.this);
            progressDialog.setMessage("Sending, please wait...");
            progressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("your_mail@gmail.com", "YourPassword");
                        }
                    });

            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("alorsathi.mobile@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("spn.ahmed92@gmail.com"));
                //message.setRecipients(Message.RecipientType.TO,InternetAddress.parse("spn.ahmed92@gmail.com,supan.cse09@gmail.com"));
                message.setSubject(subjectString);
                message.setText(textString);

                Transport.send(message);

                System.out.println("Done");

            } catch (MessagingException e) {
                System.out.println("Error sending mail : " + e.getMessage());
                //Toast.makeText(getApplicationContext(), "Mail can not sent : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressDialog.dismiss();

            Toast.makeText(getApplicationContext(), "Mail sent to : spn.ahmed92@gmail.com ", Toast.LENGTH_SHORT).show();
        }
    }

}
