package com.project.autosms.brodcastReceiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.project.autosms.activity.MainActivity;
import com.project.autosms.model.ResponseManager;
import com.project.autosms.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the SMS message passed in
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs;
        Object[] pdus;

        if (bundle != null) {
            // Retrieve the SMS message received
            if (Build.VERSION.SDK_INT >= 19) // For KITKAT
                msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            else {
                // For old versions
                pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                msgs[0] = SmsMessage.createFromPdu((byte[]) pdus[0]);
            }

            // Get information from the SMS
            SmsMessage sms = msgs[0]; // TODO: Check if only using the first SMS impacts functionality
            String sender = sms.getOriginatingAddress();
            String message = sms.getMessageBody();

            // Get the appropriate response
            String response = ResponseManager.getResponse(sender, message, context);

            if (response != null) {
                System.out.println("Responding to " + sender);

                // Notify the user
                NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "1")
                    .setContentTitle("Responded to " + sender)
                    .setContentText("\"" + response + "\"")
                    .setSmallIcon(R.mipmap.transparent_icon)
                    .setColorized(true)
                    .setColor(Color.parseColor("#292f46"));

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                    NotificationChannel notificationChannel = new NotificationChannel("1" , "Response", NotificationManager.IMPORTANCE_DEFAULT) ;
                    mBuilder.setChannelId("1");
                    assert nm != null;
                    nm.createNotificationChannel(notificationChannel) ;
                }

                // If the user clicks on the notification
                Intent notificationIntent = new Intent(context, MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent conPendingIntent = PendingIntent.getActivity(context,0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(conPendingIntent);

                nm.notify(createID(), mBuilder.build());

                // Send the response
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(sender, null, response, null, null);
            }
        }
    }

    public int createID(){
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.getDefault()).format(now));
        return id;
    }
}