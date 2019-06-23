package com.scerit.test;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.scerit.test.firestore.pastcode;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BookingActivity extends AppCompatActivity {

    FirebaseFirestore db ;

    TextView nBikes;
    CheckBox timeFrame1;
    CheckBox timeFrame2;
    CheckBox timeFrame3;
    ToggleButton timeFrameSwitch;
    Button bookBtn;
    Spinner bikeListView;
    ImageView genderImage;

    Bookings booking = new Bookings();


    String bookingId;

    Users user = new Users();


    int nBikesCounter;
    int bn;

    ArrayList<Bikes> bike = new ArrayList <Bikes>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String strTime = dateFormat.format(date);
        Log.d("retrieved Time", "onCreate: " + strTime);
        int hour = Integer.parseInt(strTime);



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
        bikeListView = (Spinner) findViewById(R.id.bikeListView);
        genderImage = (ImageView) findViewById(R.id.genderImageView);


        bikeListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(bike.get(position).getFemale())
                {
                    genderImage.setImageResource(R.drawable.girl);
                }
                else
                {
                    genderImage.setImageResource(R.drawable.man);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




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

                        final ProgressDialog progressDialog = new ProgressDialog(BookingActivity.this);

                        progressDialog.show(BookingActivity.this, "Carico" , "Stiamo prenotando la tua bici");
                        booker(new FirestoreCallBack() {
                            @Override
                            public void onCallBack(String string) {
                                Log.d("onCallBack", "onCallBack: " + string);
                                updateUserBookingStatus(new FirestoreCallBack() {
                                    @Override
                                    public void onCallBack(String string) {
                                        Log.d("onCallBack", "onCallBack: " + string);
                                        updateUserBikeStatus(new FirestoreCallBack() {
                                            @Override
                                            public void onCallBack(String string) {
                                                Log.d("onCallBack", "onCallBack: " + string);
                                                updateUserBookingId(new FirestoreCallBack() {
                                                    @Override
                                                    public void onCallBack(String string) {
                                                        Log.d("onCallBack", "onCallBack: " + string);
                                                        updateBikes(bike.get(bikeListView.getSelectedItemPosition()).getId(), new FirestoreCallBack() {
                                                            @Override
                                                            public void onCallBack(String string) {
                                                                setBikeNewCode(new FirestoreCallBack() {
                                                                    @Override
                                                                    public void onCallBack(String string) {
                                                                        Log.d("onCallBack", "onCallBack: " + string);
                                                                        setBikeOldCode(new FirestoreCallBack() {
                                                                            @Override
                                                                            public void onCallBack(String string) {
                                                                                Log.d("onCallBack", "onCallBack: " + string);
                                                                                progressDialog.dismiss();
                                                                                Intent myIntent = new Intent(getApplicationContext(), InfoActivity.class);
                                                                                myIntent.putExtra("myBikeId", bike.get(bikeListView.getSelectedItemPosition()).getId()); //Optional parameters
                                                                                myIntent.putExtra("myBookingId", user.getCbookingid());
                                                                                Log.d("bookingIDValueClick", "onSuccess: " + user.getCbookingid());
                                                                                BookingActivity.this.startActivity(myIntent);
                                                                                finish();
                                                                            }
                                                                        });
                                                                    }
                                                                });

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });




                                    }
                                });

                            }
                        });



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
                if(timeFrame1.isChecked())
                {
                    bookBtn.setEnabled(true);
                }
                else if(timeFrame1.isChecked() == false && timeFrame2.isChecked() == false && timeFrame3.isChecked() == false)
                {
                    bookBtn.setEnabled(false);
                }
                if(timeFrame1.isChecked() && timeFrame3.isChecked())
                {
                    timeFrame2.setChecked(true);
                }
            }
        });

        timeFrame3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timeFrame3.isChecked())
                {
                    bookBtn.setEnabled(true);
                }
                else if(timeFrame1.isChecked() == false && timeFrame2.isChecked() == false && timeFrame3.isChecked() == false)
                {
                    bookBtn.setEnabled(false);
                }
                if (timeFrame1.isChecked() && timeFrame3.isChecked())
                {
                    timeFrame2.setChecked(true);
                }
            }
        });

        timeFrame2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(timeFrame2.isChecked())
                {
                    bookBtn.setEnabled(true);
                }
                else if(timeFrame1.isChecked() == false && timeFrame2.isChecked() == false && timeFrame3.isChecked() == false)
                {
                    bookBtn.setEnabled(false);
                }
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

       getBikesNumber(new FirestoreBikeCallBack() {
           @Override
           public void onCallBack(List<String> list) {
               ArrayAdapter<String> adp1 = new ArrayAdapter<String>(BookingActivity.this,
                       android.R.layout.simple_list_item_1, list);
               adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               bikeListView.setAdapter(adp1);

           }
       });

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

    private void getBikesNumber (final FirestoreBikeCallBack firestoreBikeCallBack)
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

                final List<String> list = new ArrayList<String>();
                int x = 0;
                for (Bikes b : bike)
                {
                    list.add(bike.get(x).getId());
                    x++;
                }

                firestoreBikeCallBack.onCallBack(list);

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

    private void booker(final FirestoreCallBack firestoreCallBack)
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyy-MM-dd");
        String strDate = dateFormat.format(date);

        Log.d("database", FirebaseAuth.getInstance().getUid());



        booking.setBike(bike.get(bikeListView.getSelectedItemPosition()).getId());

        String mTimeFrames = timeFrameBooked();

            booking.setTimeframe(mTimeFrames);
            booking.setToday(strDate);
            booking.setActive(true);

            DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                    .collection("bookings").document();


        Log.d("BookingID", "booker: " + usersDoc.getId());
        user.setCbookingid(usersDoc.getId());
        booking.setId(usersDoc.getId());

            usersDoc.set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("test", "onSuccess:test ");
               firestoreCallBack.onCallBack("1");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        Log.d("databasebookingupdate" , "END");

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

    private void updateBikes(String selectedbike , FirestoreCallBack firestoreCallBack)
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(selectedbike);
        usersDoc.update("istaken", true);
        firestoreCallBack.onCallBack("3");

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

    private void updateUserBookingStatus(final FirestoreCallBack firestoreCallBack)
    {

        Log.d("updateUserBookingStatus", "updateUserBookingStatus: ENTERED");
        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        usersDoc.update("booked" , true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("booking", "onSuccess: booked");
                firestoreCallBack.onCallBack("2");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("booking", "onFailure: NOT booked");
            }
        });


    }

    private void  updateUserBikeStatus (final FirestoreCallBack firestoreCallBack)
    {

        DocumentReference mybikeDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        mybikeDoc.update("mybike" , bike.get(bikeListView.getSelectedItemPosition()).getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("mybike", "onSuccess: updated");
                firestoreCallBack.onCallBack("2.1");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("mybike", "onFailure: NOT updated");
            }
        });
    }

    private void updateUserBookingId(final FirestoreCallBack firestoreCallBack)
    {

        DocumentReference bookingIdDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid());

        Log.d("updatebookingid", "updateUserBookingStatus: " + user.getCbookingid());

        bookingIdDoc.update("cbookingid" , user.getCbookingid()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("bookingid", "onSuccess: updated");
                firestoreCallBack.onCallBack("2.2");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("bookingid", "onFailure: NOT updated");
            }
        });

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
                    myIntent.putExtra("myBookingId", user.getCbookingid());
                    Log.d("bookingIDvalue", "onSuccess: " + user.getCbookingid());
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


    private void setBikeNewCode(final FirestoreCallBack firestoreCallBack)
    {
        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(bike.get(bikeListView.getSelectedItemPosition()).getId());
        String newcode = codeGen();

        usersDoc.update("newcode" , newcode).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firestoreCallBack.onCallBack("4 ");

                Log.d("successUpdate", "onSuccess: NEWCODE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestoreCallBack.onCallBack("4 FAILED");

                Log.d("bookingid", "onFailure: NOT updated");
            }
        });

        CollectionReference codeColl = FirebaseFirestore.getInstance().collection("bikes").document(bike.get(bikeListView.getSelectedItemPosition()).getId()).collection("pastcode");

        pastcode pcode = new pastcode();
        pcode.setCode(newcode);

        codeColl.add(pcode).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful())
                {
                    Log.d("CODECOLLETION", "onComplete: done");
                }
            }
        });

    }


    private  void setBikeOldCode(final FirestoreCallBack firestoreCallBack)
    {

        DocumentReference usersDoc = FirebaseFirestore.getInstance().collection("bikes").document(bike.get(bikeListView.getSelectedItemPosition()).getId());


        usersDoc.update("oldcode", bike.get(bikeListView.getSelectedItemPosition()).getNewcode()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("successUpdate", "onSuccess: OLDCODE ");
                firestoreCallBack.onCallBack("5");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firestoreCallBack.onCallBack("5 FAILED");

            }
        });

    }



    private String codeGen ()
    {
        String newcode = "";
        Random r = new Random();
        for (int i = 0 ; i < 5 ; i++)
        {
            newcode += Integer.toString(r.nextInt(9));
        }
        Log.d("codegen", "codeGen: "+ newcode);
        return newcode;
    }











    private interface FirestoreCallBack
    {
        void onCallBack (String string);



    }

    private interface FirestoreBikeCallBack
    {
        void onCallBack (List<String> list);
    }




}
