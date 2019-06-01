package com.scerit.test;

import android.app.ActivityOptions;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scerit.test.firestore.Users;

public class App  extends Application {

    public static final String CHANNEL_1_ID = "endBooking";

    Users user = new Users();



    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        FirebaseUser cUser = FirebaseAuth.getInstance().getCurrentUser();


    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Fine Prenotazioni",
                    NotificationManager.IMPORTANCE_DEFAULT

            );

            channel1.enableVibration(true);
            channel1.setDescription("Notifiche relative alle prenotazioni delle biciclette");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }



}
