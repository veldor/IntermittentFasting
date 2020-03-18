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
import net.veldor.intermittentfasting.MainActivity;
import net.veldor.intermittentfasting.R;
import net.veldor.intermittentfasting.db.DbQueries;
import net.veldor.intermittentfasting.db.entity.Drink;
import net.veldor.intermittentfasting.db.entity.Eat;
import net.veldor.intermittentfasting.receivers.MiscActionsReceiver;
import net.veldor.intermittentfasting.workers.TimerWorker;

import java.util.Locale;

import static net.veldor.intermittentfasting.receivers.MiscActionsReceiver.EXTRA_ACTION_TYPE;

public class MyNotify {
    private static final String TIMER_CHANNEL_ID = "timer";
    private static final String CONGRATS_CHANNEL_ID = "congrats";
    private static final int START_EAT_CODE = 1;
    private static final int START_FASTING_CODE = 2;
    private static final int SHOW_STAT_CODE = 3;
    private static final int OPEN_APP_CODE = 4;
    private static final int I_EAT_CODE = 5;
    private static final int I_DRINK_CODE = 6;
    public static final int TIMER_NOTIFICATION = 1;
    public static final int PERIOD_FINISHED_NOTIFICATION = 4;
    private final App mContext;
    public final NotificationManager mNotificationManager;
    private NotificationCompat.Builder timerNotification;
    private int mLastNotificationId = 100;

    public MyNotify() {
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
                nc.enableLights(false);
                nc.setSound(null, null);
                nc.enableVibration(false);
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
        // проверю последний период еды
        Eat lastEat = DbQueries.getLastEat();
        String eatTime = "";
        if (lastEat != null) {
            eatTime = String.format(Locale.ENGLISH, mContext.getString(R.string.last_eat_time_message), TimerWorker.hmsTimeFormatter(System.currentTimeMillis() - lastEat.eatTime));
        }
        // проверю последний период питья
        Drink lastDrink = DbQueries.getLastDrink();
        String drinkTime = "";
        if (lastDrink != null) {
            drinkTime = String.format(Locale.ENGLISH, mContext.getString(R.string.last_drink_time_message), TimerWorker.hmsTimeFormatter(System.currentTimeMillis() - lastDrink.drinkTime));
        }
        if (App.getInstance().isFasting) {
            timerNotification.setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.you_fasting_message) + hmsTimeFormatter + "\n" + eatTime + "\n" + drinkTime));
        } else {
            timerNotification.setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.you_eating_message) + hmsTimeFormatter + "\n" + eatTime + "\n" + drinkTime));
        }
        mNotificationManager.notify(currentNotification, timerNotification.build());
    }

    public Notification getTimerNotification() {
        // добавлю интент, который будет запускать приложение при клике на уведомление
        // добавлю интент для отображения экрана очереди скачивания
        Intent openAppIntent = new Intent(mContext, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(mContext, OPEN_APP_CODE, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // проверю, какой сейчас период
        boolean isFasting = App.getInstance().isFasting;
        if (isFasting) {
            // создам интент, который переключит таймер на пищевое окно
            Intent startEatIntent = new Intent(mContext, MiscActionsReceiver.class);
            startEatIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_START_EATING);
            PendingIntent startEatPendingIntent = PendingIntent.getBroadcast(mContext, START_EAT_CODE, startEatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            timerNotification = new NotificationCompat.Builder(mContext, TIMER_CHANNEL_ID)
                    .setContentTitle(mContext.getString(R.string.its_fasting_time_message))
                    .setTicker(mContext.getString(R.string.its_fasting_time_message))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.still_fasting_message)))
                    .setSmallIcon(R.drawable.ic_pan_tool_black_24dp)
                    .setOngoing(true)
                    // Add the cancel action to the notification which can
                    // be used to cancel the worker
                    .addAction(R.drawable.ic_pregnant_woman_black_24dp, mContext.getString(R.string.start_eat_message), startEatPendingIntent);
        } else {
            // создам интент, который отправит событие о еде
            Intent iEatIntent = new Intent(mContext, MiscActionsReceiver.class);
            iEatIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_I_EAT);
            PendingIntent iEatPendingIntent = PendingIntent.getBroadcast(mContext, I_EAT_CODE, iEatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // создам интент, который отправит событие о питье
            Intent iDrinkIntent = new Intent(mContext, MiscActionsReceiver.class);
            iDrinkIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_I_I_DRINK);
            PendingIntent iDrinkPendingIntent = PendingIntent.getBroadcast(mContext, I_DRINK_CODE, iDrinkIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            // создам интент, который переключит таймер на голодание
            Intent startFastingIntent = new Intent(mContext, MiscActionsReceiver.class);
            startFastingIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_START_FASTING);
            PendingIntent startFastingPendingIntent = PendingIntent.getBroadcast(mContext, START_FASTING_CODE, startFastingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            timerNotification = new NotificationCompat.Builder(mContext, TIMER_CHANNEL_ID)
                    .setContentTitle(mContext.getString(R.string.its_eating_time_message))
                    .setTicker(mContext.getString(R.string.its_eating_time_message))
                    .setVibrate(null)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.still_eating_message)))
                    .setSmallIcon(R.drawable.ic_pregnant_woman_black_24dp)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_free_breakfast_black_24dp, mContext.getString(R.string.i_eat_message), iEatPendingIntent)
                    .addAction(R.drawable.ic_free_breakfast_black_24dp, mContext.getString(R.string.i_drink_message), iDrinkPendingIntent)
                    .addAction(R.drawable.ic_pan_tool_black_24dp, mContext.getString(R.string.start_fasting_message), startFastingPendingIntent);
        }
        timerNotification.setContentIntent(openAppPendingIntent);
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

    public void sendPeriodFinishedNotification() {
        // при нажатии на окно или на экшн- перейду в статистику
        Intent showStatIntent = new Intent(mContext, MiscActionsReceiver.class);
        showStatIntent.putExtra(EXTRA_ACTION_TYPE, MiscActionsReceiver.ACTION_SHOW_STAT);
        PendingIntent openAppPendingIntent = PendingIntent.getBroadcast(mContext, SHOW_STAT_CODE, showStatIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // посчитаю, сколько времени прошло в периоде
        long startTime = App.getPreferences().getLong(App.FASTING_TIMER, 0);
        if (startTime > 0) {
            long difference = System.currentTimeMillis() - startTime;
            String spendTime = TimerWorker.hmsTimeFormatter(difference);
            String message;
            if (App.getInstance().isFasting) {
                message = mContext.getString(R.string.fasting_timing_message) + spendTime;
            } else {
                message = mContext.getString(R.string.eating_timing_message) + spendTime;
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, CONGRATS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_battery_full_black_24dp)
                    .setContentTitle(mContext.getString(R.string.period_finish_message))
                    .setContentIntent(openAppPendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .addAction(R.drawable.ic_pan_tool_black_24dp, mContext.getString(R.string.statistics_message), openAppPendingIntent)
                    .setAutoCancel(true);
            Notification notification = notificationBuilder.build();
            mNotificationManager.notify(PERIOD_FINISHED_NOTIFICATION, notification);
            mLastNotificationId++;
        }
    }
}
