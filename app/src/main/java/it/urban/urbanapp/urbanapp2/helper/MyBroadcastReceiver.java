package it.urban.urbanapp.urbanapp2.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by alexander on 08/03/15.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, NotificationService.class);
        service1.putExtra("day",intent.getExtras().getString("day"));
        context.startService(service1);

    }
}





