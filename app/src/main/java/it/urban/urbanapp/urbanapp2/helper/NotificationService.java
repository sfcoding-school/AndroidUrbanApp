package it.urban.urbanapp.urbanapp2.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import it.urban.urbanapp.urbanapp2.EventoActivity;
import it.urban.urbanapp.urbanapp2.R;

/**
 * Created by alexander on 08/03/15.
 */
public class NotificationService extends Service {

    private NotificationManager mManager;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent,flags,startId);

        Log.e("Intent Notification Se",intent.toString());
        Bundle data = intent.getExtras();
        String day = data.getString("day");

        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),EventoActivity.class);
        intent1.putExtra("day", day);
        /*
        Notification notification = new Notification(R.drawable.logourban,"Evento Stasera!!!", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
*/
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        /*notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "Evento Stasera", "Ti ricordo che il nome della lista è Urban App", pendingNotificationIntent);

        mManager.notify(0, notification);*/

        Notification noti = new Notification.Builder(this)
                .setContentTitle("Evento Stasera")
                .setContentText("Ti ricordo che il nome della lista è Ursynchronizedban App")
                .setSmallIcon(R.drawable.logourban)
                .setContentIntent(pendingNotificationIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);



        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
