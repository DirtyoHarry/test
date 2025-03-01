package com.scerit.test;

import android.content.Intent;
import android.icu.text.IDNA;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.scerit.test.firestore.Bikes;
import com.scerit.test.firestore.Bookings;
import com.scerit.test.firestore.pastcode;
import com.scerit.test.firestore.timeframe;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    TextView idText;
    TextView oldCodeText;
    TextView newCodeText;
    TextView endingBookingText;
    Button endBookingBtn;
    ListView oldCodesList;



    String bikeid;
    Bikes bookedBike;
    String bookingId;

    EditText confPassword;


    FirebaseFirestore db ;

   Bookings booking = new Bookings();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        bikeid = getIntent().getStringExtra("myBikeId" );
        bookingId = getIntent().getStringExtra("myBookingId");

        Log.d("InfoAc bookingId", "onCreate: " + bookingId);

        getBookingInfo();
        getBikeInfo();

      //  Toast.makeText(InfoActivity.this, bikeid, Toast.LENGTH_LONG).show();

        idText = (TextView) findViewById(R.id.idView);
        oldCodeText = (TextView) findViewById(R.id.oldCodeText);
        newCodeText = (TextView) findViewById(R.id.newCodeText);
        endingBookingText = (TextView) findViewById(R.id.endingBookingText);
        endBookingBtn = (Button) findViewById(R.id.endBookingBtn);






        endBookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                AlertDialog.Builder confirmAlert = new AlertDialog.Builder(InfoActivity.this);
                View confAlView = getLayoutInflater().inflate(R.layout.alert_confirm, null);
                confPassword = (EditText) confAlView.findViewById(R.id.insertPswText);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FirebaseAnalytics.getInstance(this).logEvent("oldcode_click" , null);

        final List<String> startingList = new ArrayList<>();
        AlertDialog.Builder codeAlert = new AlertDialog.Builder(InfoActivity.this);
        View confAlView = getLayoutInflater().inflate(R.layout.oldcodes_list, null);
        oldCodesList = (ListView) confAlView.findViewById(R.id.oldCodeList);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, startingList);
        oldCodesList.setAdapter(arrayAdapter);

        codeAlert.setView(confAlView);

        final AlertDialog dialog = codeAlert.create();
        dialog.show();

        oldCodesArray(new FirestoreCallBack() {
            @Override
            public void onCallBack(List<pastcode> pastcodesArray) {

                for (int i = 0; i < pastcodesArray.size(); i++)
                {
                    startingList.add(pastcodesArray.get(i).getCode());
                }

                arrayAdapter.notifyDataSetChanged();
            }

        });





        return super.onOptionsItemSelected(item);
    }

    private void getBikeInfo()
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

    private void getBookingInfo ()
    {
        Log.d("checkuid", "getBookingInfo: " + FirebaseAuth.getInstance().getUid());

        DocumentReference bookingDRef = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getUid()).collection("bookings").document(bookingId);


        bookingDRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                booking = documentSnapshot.toObject(Bookings.class);
                Log.d("getBookingInfo", "onSuccess: " + booking.getToday() + " " + documentSnapshot.getId());
                getTimeframe ();
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

        DocumentReference bookingDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).collection("bookings").document(booking.getId());

        bookingDoc.update("active" , false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("bookingActive", "onSuccess: updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("bookingActive", "onFailure: NOT updated");
            }
        });


    }

    private void getTimeframe ()
    {

        final ArrayList<timeframe> selectedTimeframe = new ArrayList<timeframe>();
        CollectionReference bikeCollRef = db.collection("timeframe");

        Log.d("getTimeFrame", "getTimeframe:" + booking.getTimeframe());

        Query bikesNumber = bikeCollRef.whereEqualTo( "id", booking.getTimeframe());

        bikesNumber.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("success", queryDocumentSnapshots.toString() );

                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                    selectedTimeframe.add(document.toObject(timeframe.class));
                }

                Log.d("success", selectedTimeframe.get(0).getEndtime() );
                Log.d("success", (Integer.toString(selectedTimeframe.size())));

                endingBookingText.setText(selectedTimeframe.get(0).getEndtime());


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void oldCodesArray (final FirestoreCallBack firestoreCallBack)
    {
        final List<pastcode> pastcodeList = new ArrayList<>();

        CollectionReference bikeCollRef = db.collection("bikes").document(bookedBike.getId()).collection("pastcode");

        bikeCollRef.orderBy("time").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override


            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                    pastcodeList.add(document.toObject(pastcode.class));
                }

                firestoreCallBack.onCallBack(pastcodeList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private interface FirestoreCallBack
    {
        void onCallBack (List<pastcode> pastcodesArray);
    }

}
