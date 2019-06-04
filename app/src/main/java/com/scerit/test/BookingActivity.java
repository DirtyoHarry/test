package com.scerit.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

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
import com.scerit.test.firestore.Users;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BookingActivity extends AppCompatActivity {

    FirebaseFirestore db ;

    TextView nBikes;
    CheckBox timeFrame1;
    CheckBox timeFrame2;
    CheckBox timeFrame3;
    ToggleButton timeFrameSwitch;
    Button bookBtn;
    Bookings booking = new Bookings();


    String bookingId;

    Users user = new Users();


    int nBikesCounter;
    int bn;

    ArrayList<Bikes> bike = new ArrayList <Bikes>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkForBookings();
        setContentView(R.layout.activity_booking);
        db = FirebaseFirestore.getInstance();

      //  Toast.makeText(BookingActivity.this, FirebaseAuth.getInstance().getUid().toString(), Toast.LENGTH_LONG).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(toolbar);


        nBikes = (TextView) findViewById(R.id.nBikesText);
        timeFrame1 = (CheckBox) findViewById(R.id.timeFrame1);
        timeFrame2 = (CheckBox) findViewById(R.id.timeFrame2);
        timeFrame3 = (CheckBox) findViewById(R.id.timeFrame3);
        timeFrameSwitch = (ToggleButton) findViewById(R.id.timeFrameSwitch);
        bookBtn = (Button) findViewById(R.id.bookBtn);


        timeFrameSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timeFrameSwitch.isChecked())
                {
                    timeFrame1.setChecked(true);
                    timeFrame2.setChecked(true);
                    timeFrame3.setChecked(true);

                }

            }
        });

        timeFrameSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeFrameSwitch.isChecked() == false)
                {
                    timeFrame1.setChecked(false);
                    timeFrame2.setChecked(false);
                    timeFrame3.setChecked(false);
                }
            }
        });


        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                builder.setMessage("Prenotando questa bici ti assumi la resposabilità per eventuali danni e/o smarrimento")
                        .setTitle("ATTENZIONE");

                builder.setPositiveButton("Accetto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        booker();

                        Intent myIntent = new Intent(getApplicationContext(), InfoActivity.class);
                        myIntent.putExtra("myBikeId", bike.get(0).getId()); //Optional parameters
                        myIntent.putExtra("myBookingId", user.getBookingId());
                        Log.d("timeframeValueClick", "onSuccess: " + user.getBookingId());

                        BookingActivity.this.startActivity(myIntent);
                        finish();

                    }
                });
                builder.setNegativeButton("Rifiuto", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

















        });

        timeFrame1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timeFrame1.isChecked() && timeFrame3.isChecked())
                {
                    timeFrame2.setChecked(true);
                }
            }
        });

        timeFrame3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (timeFrame1.isChecked() && timeFrame3.isChecked())
                {
                    timeFrame2.setChecked(true);
                }
            }
        });

        timeFrame2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (timeFrame1.isChecked() && timeFrame3.isChecked())
                {
                    timeFrame2.setChecked(true);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu , menu);
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();

       getBikesNumber ();

    }

    private  void timeFrameSelecter()
    {
        if(timeFrame1.isChecked() && timeFrame2.isChecked() && timeFrame3.isChecked())
        {
            timeFrameSwitch.setChecked(true);
        }

        else
        {
            timeFrameSwitch.setChecked(false);
        }


    }

    private void getBikesNumber ()
    {
        bike.clear();

        CollectionReference bikeCollRef = db.collection("bikes");

        Query bikesNumber = bikeCollRef.whereEqualTo("istaken", false);

        bikesNumber.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("success", queryDocumentSnapshots.toString() );

                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                    bike.add(document.toObject(Bikes.class));

                }

                Log.d("success", bike.get(0).getOldcode().toString() );
                Log.d("success", (Integer.toString(bike.size())));

                nBikes.setText(Integer.toString(bike.size()));


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

    public void isSelected (View view)
    {

        timeFrameSelecter();

    }

    private void booker()
    {
        Date today = Calendar.getInstance().getTime();

     //   today = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(date);

        Log.d("database", FirebaseAuth.getInstance().getUid());



        booking.setBike(bike.get(0).getId());

        String mTimeFrames = timeFrameBooked();

            booking.setTimeframe(mTimeFrames);
            booking.setToday(strDate);
            booking.setActive(true);

            DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                    .collection("bookings").document();

            bookingId = usersDoc.getId();

        Log.d("BookingID", "booker: " + usersDoc.getId());

            usersDoc.set(booking).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Log.d("database" , "OK");
                    }
                    else {
                        Log.d("database" , "BAD");
                    }
                }
            });

            updateBikes(bike.get(0).getId());
            updateUserBookingStatus();
        }




    private String timeFrameBooked ()
    {
        String timeframes = "0";

        if(timeFrame1.isChecked() && timeFrame2.isChecked() && timeFrame3.isChecked())
        {
            timeframes ="4";
        }
        else
        {
            if(timeFrame1.isChecked() && !timeFrame2.isChecked())
            {
                timeframes = "1";
            }
            if(timeFrame2.isChecked() && !timeFrame1.isChecked() && !timeFrame3.isChecked())
            {
                timeframes = "2";
            }
            if(timeFrame3.isChecked() && !timeFrame2.isChecked())
            {
                timeframes = "3";

            }
            if(timeFrame1.isChecked() && timeFrame2.isChecked())
            {
                timeframes = "5";
            }
            if(timeFrame3.isChecked() && timeFrame2.isChecked())
            {
                timeframes = "6";
            }
       }

        return timeframes;
    }

    private void updateBikes(String selectedbike)
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(selectedbike);
        usersDoc.update("istaken", true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(BookingActivity.this, "Sei uscito correttamente", Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        //myIntent.putExtra("key", value); //Optional parameters
        BookingActivity.this.startActivity(myIntent);
        finish();

        return super.onOptionsItemSelected(item);

    }

    private void updateUserBookingStatus()
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.update("booked" , true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("booking", "onSuccess: booked");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("booking", "onFailure: NOT booked");
            }
        });

        DocumentReference mybikeDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.update("mybike" , bike.get(0).getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        DocumentReference bookingIdDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        bookingIdDoc.update("cbookingid" , bookingId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("bookingid", "onSuccess: updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("bookingid", "onFailure: NOT updated");
            }
        });
/*

        CollectionReference bookingCollRef = db.collection("users").document(FirebaseAuth.getInstance().getUid()).collection("bookings");

        Query bookingid = bookingCollRef.whereEqualTo("active", true);

        bookingid.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("success", queryDocumentSnapshots.toString() );

                for (QueryDocumentSnapshot document: queryDocumentSnapshots)
                {
                   bookingId = document.getId();

                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
*/

    }

    private void checkForBookings()
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        usersDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                user = documentSnapshot.toObject(Users.class);

                if(user.getMybike() !=null)

                {
                    Intent myIntent = new Intent(getApplicationContext(), InfoActivity.class);
                    myIntent.putExtra("myBikeId", user.getMybike()); //Optional parameters
                    myIntent.putExtra("myBookingId", user.getBookingId());
                    Log.d("timeframeValue", "onSuccess: " + user.getBookingId());
                    BookingActivity.this.startActivity(myIntent);
                    finish();

                }
                Log.d("userBooking", "onSuccess: BookingRet");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("userBooking", "onSuccess: NOTRet");
            }
        });

    }

}
