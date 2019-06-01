package com.scerit.test;

import android.content.Intent;
import android.icu.text.IDNA;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scerit.test.firestore.Bikes;
import com.scerit.test.firestore.Bookings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InfoActivity extends AppCompatActivity {

    TextView idText;
    TextView oldCodeText;
    TextView newCodeText;
    TextView endingBookingText;
    Button endBookingBtn;

    String bikeid;
    Bikes bookedBike;

    ArrayList<Bookings> booking = new ArrayList<Bookings>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bikeid = getIntent().getStringExtra("myBikeId" );

      //  Toast.makeText(InfoActivity.this, bikeid, Toast.LENGTH_LONG).show();

        idText = (TextView) findViewById(R.id.idView);
        oldCodeText = (TextView) findViewById(R.id.oldCodeText);
        newCodeText = (TextView) findViewById(R.id.newCodeText);
        endingBookingText = (TextView) findViewById(R.id.endingBookingText);
        endBookingBtn = (Button) findViewById(R.id.endBookingBtn);

        maxReturnTime();
        getBookingInfo();



        endBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder confirmAlert = new AlertDialog.Builder(InfoActivity.this);
                View confAlView = getLayoutInflater().inflate(R.layout.alert_confirm, null);
                EditText confPassword = (EditText) confAlView.findViewById(R.id.insertPswText);
                Button pswButton = (Button) confAlView.findViewById(R.id.pswBtn);

                confirmAlert.setView(confAlView);

                final AlertDialog dialog = confirmAlert.create();
                dialog.show();

                pswButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        returnBike();
                        updateUserBookingStatus();
                        dialog.dismiss();

                    }
                });




            }
        });

    }

    private void getBookingInfo()
    {
        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(bikeid);
       usersDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {

               Log.d("GetBike", "onSuccess: Obj retrieved");
               bookedBike = documentSnapshot.toObject(Bikes.class);
             //  Toast.makeText(InfoActivity.this, bookedBike.getId(),Toast.LENGTH_LONG).show();
               idText.setText(bookedBike.getId());
               oldCodeText.setText(bookedBike.getOldcode());
               newCodeText.setText(bookedBike.getNewcode());
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d("GetBike", "onSuccess: Obj failed");
           }
       });



    }

    private void returnBike ()
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(bikeid);
        usersDoc.update("istaken", false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent myIntent = new Intent(getApplicationContext(), BookingActivity.class);
                InfoActivity.this.startActivity(myIntent);
                Toast.makeText(InfoActivity.this , "Bici restituita con successo", Toast.LENGTH_LONG).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(InfoActivity.this , "something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void maxReturnTime ()
    {

        booking.clear();

        CollectionReference bikeCollRef = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid().toString()).collection("bookings");

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);




        Query returnDeadLine = bikeCollRef.whereEqualTo("today",  strDate).whereEqualTo("bike", bikeid);

        returnDeadLine.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                    booking.add(document.toObject(Bookings.class));
                    Log.d("query", "onSuccess: " + booking.get(0).getTimeframe());
                }

          //      Toast.makeText(InfoActivity.this , booking.get(0).getTime().toString(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(InfoActivity.this , "Deadline non trovata", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void sortBookingByDate()
    {

    }

    private void updateUserBookingStatus()
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.update("booked" , false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("booking", "onSuccess: NOT booked");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("booking", "onFailure: booked");
            }
        });

        DocumentReference mybikeDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.update("mybike" , null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("mybike", "onSuccess: updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("mybike", "onFailure: NOT updated");
            }
        });


    }

}
