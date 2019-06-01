package com.scerit.test.background;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.scerit.test.R;

import static com.scerit.test.App.CHANNEL_1_ID;

public class BackgroundNotification extends JobService {

    private boolean jobCancelled = false;
    NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {

        notificationManager = NotificationManagerCompat.from(this);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.notifyicon)
                .setContentTitle("Il tempo scorre")
                .setContentText("Devi restituire la tua bici alle" )
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(1 , notification);


        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

   // private string timeFrameConverter()
}
