package com.scerit.test;

import android.app.Notification;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.scerit.test.background.BackgroundNotification;
import com.scerit.test.firestore.Users;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.scerit.test.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;
    Button btnLogIn;




    Users user = new Users();

    EditText mailText;
    EditText pswText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*ComponentName componentName = new ComponentName(this , BackgroundNotification.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setPeriodic(15 * 60 * 1000)
                .setPersisted(true)
                .build();

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.schedule(info);
*/
       /* SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if(firstStart)
        {
            Intent myIntent = new Intent(getApplicationContext(), RecoverPasswordActivity.class);
            // myIntent.putExtra("key", value); //Optional parameters
            MainActivity.this.startActivity(myIntent);
            finish();
        }

        */

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();
        if (cUser != null) {

                Intent myIntent = new Intent(getApplicationContext(), BookingActivity.class);
                // myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
                finish();
        }


        setContentView(R.layout.activity_main);

        mailText = (EditText)findViewById(R.id.mailText);
        pswText = (EditText) findViewById(R.id.pswText);

        mailText.addTextChangedListener(loginTextWatcher);
        pswText.addTextChangedListener(loginTextWatcher);

        btnLogIn = (Button) findViewById(R.id.button);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                logInAuth( mailText.getText().toString().trim() , pswText.getText().toString().trim());


            }
        });

    }


    private void logInAuth(String mail, String psw) {
        mAuth.signInWithEmailAndPassword(mail, psw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(MainActivity.this, "Hai eseguito il login", Toast.LENGTH_LONG).show();
                Log.d("database", mAuth.getUid());
                userData();
                Intent myIntent = new Intent(getApplicationContext(), BookingActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
                finish();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Password o Email sono errati", Toast.LENGTH_LONG).show();
            }
        });



    }

    private void userData () {
       Log.d("database", mAuth.getUid());


       user.setEmail(mAuth.getCurrentUser().getEmail());
       user.setId(mAuth.getCurrentUser().getUid());

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("UserUpdate", "onSuccess: user updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("UserUpdate", "onFailure: user NOT updated " + e.getMessage());
            }
        });


    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            btnLogIn.setEnabled(!mailText.getText().toString().trim().isEmpty() && !pswText.getText().toString().trim().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
