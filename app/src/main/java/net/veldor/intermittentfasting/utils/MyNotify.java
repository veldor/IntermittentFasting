package net.veldor.intermittentfasting.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.R;
import net.veldor.intermittentfasting.receivers.MiscActionsReceiver;

import static net.veldor.intermittentfasting.receivers.MiscActionsReceiver.EXTRA_ACTION_TYPE;

public class MyNotify {
    public static final String TIMER_CHANNEL_ID = "timer";
    private static final String CONGRATS_CHANNEL_ID = "congrats";
    private static final int START_EAT_CODE = 1;
    private static final int START_FASTING_CODE = 2;
    private final App mContext;
    private final NotificationManager mNotificationManager;
    private NotificationCompat.Builder timerNotification;
    private int mLastNotificationId = 100;

    public MyNotify(){
        mContext = App.getInstance();
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // создам каналы уведомлений
        createChannels();
    }

    private void createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                // создам канал уведомлений таймера отсчёта
                NotificationChannel nc = new NotificationChannel(TIMER_CHANNEL_ID, mContext.getString(R.string.timer_channel_description), NotificationManager.IMPORTANCE_DEFAULT);
                nc.setDescription(mContext.getString(R.string.timer_channel_description));
                nc.enableLights(true);
                nc.setLightColor(Color.RED);
                nc.enableVibration(true);
                mNotificationManager.createNotificationChannel(nc);

                // создам канал уведомлений поздравлений
                nc = new NotificationChannel(CONGRATS_CHANNEL_ID, mContext.getString(R.string.congratulations_channel_description), NotificationManager.IMPORTANCE_DEFAULT);
                nc.setDescription(mContext.getString(R.string.timer_channel_description));
                nc.enableLights(true);
                nc.setLightColor(Color.GREEN);
                nc.enableVibration(true);
                mNotificationManager.createNotificationChannel(nc);
            }
        }
    }

    public void setSpendTime(int currentNotification, String hmsTimeFormatter) {
        if(App.getInstance().isFasting){
            timerNotification.setContentText(mContext.getString(R.string.you_fasting_message) + hmsTimeFormatter);
        }
        else{
            timerNotification.setContentText(mContext.getString(R.string.you_eating_message) + hmsTimeFormatter);
        }
        mNotificationManager.notify(currentNotification, timerNotification.build());
    }

    public Notification getTimerNotification(PendingIntent intent) {
        // проверю, какой сейчас период
        boolean isFasting = App.getInstance().isFasting;
        if(isFasting){
            // создам интент, который переключит таймер на пищевое окно
            Intent startEatIntent = new Intent(mContext, MiscActionsReceiver.class);
            startEatIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_START_EATING);
            PendingIntent startEatPendingIntent = PendingIntent.getBroadcast(mContext, START_EAT_CODE, startEatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            timerNotification = new NotificationCompat.Builder(mContext, TIMER_CHANNEL_ID)
                    .setContentTitle(mContext.getString(R.string.its_fasting_time_message))
                    .setTicker(mContext.getString(R.string.its_fasting_time_message))
                    .setContentText(mContext.getString(R.string.still_fasting_message))
                    .setSmallIcon(R.drawable.ic_pan_tool_black_24dp)
                    .setOngoing(true)
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
                    .addAction(R.drawable.ic_pregnant_woman_black_24dp, mContext.getString(R.string.start_eat_message), startEatPendingIntent);
        }
        else{

            // создам интент, который переключит таймер на голодание
            Intent startFastingIntent = new Intent(mContext, MiscActionsReceiver.class);
            startFastingIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_START_FASTING);
            PendingIntent startFastingPendingIntent = PendingIntent.getBroadcast(mContext, START_FASTING_CODE, startFastingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            timerNotification = new NotificationCompat.Builder(mContext, TIMER_CHANNEL_ID)
                    .setContentTitle(mContext.getString(R.string.its_eating_time_message))
                    .setTicker(mContext.getString(R.string.its_eating_time_message))
                    .setContentText(mContext.getString(R.string.still_eating_message))
                    .setSmallIcon(R.drawable.ic_pregnant_woman_black_24dp)
                    .setOngoing(true)
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
                    .addAction(R.drawable.ic_pan_tool_black_24dp, mContext.getString(R.string.start_fasting_message), startFastingPendingIntent);
        }
        return timerNotification.build();
    }

    public void sendCongratulationsNotification(String s) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, CONGRATS_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_battery_full_black_24dp)
                .setContentTitle("Новое достижение")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(s))
                .setAutoCancel(true);
        Notification notification = notificationBuilder.build();
        mNotificationManager.notify(mLastNotificationId, notification);
        mLastNotificationId++;
    }
}
