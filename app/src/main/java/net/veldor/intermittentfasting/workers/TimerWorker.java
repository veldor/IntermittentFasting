package net.veldor.intermittentfasting.workers;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.R;
import net.veldor.intermittentfasting.utils.MyNotify;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimerWorker extends Worker {
    private final App mContext;
    private MyNotify notifier;

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = App.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        notifier = App.getNotifier();
        // помечу рабочего важным
        ForegroundInfo info = createForegroundInfo();
        setForegroundAsync(info);
        SharedPreferences preferences = App.getPreferences();
        long startTime;
        long currentTime;
        while ((startTime = preferences.getLong(App.FASTING_TIMER, 0)) > 0 && !isStopped()){
            // проверю, сколько времени прошло с момента старта таймера
            currentTime = System.currentTimeMillis();
            long difference = currentTime - startTime;
            notifier.setSpendTime(MyNotify.TIMER_NOTIFICATION, hmsTimeFormatter(difference));

            // проверю достижения
            if(!App.getInstance().isFasting && difference > 1000 * 60 * 60 * 16 && !preferences.getBoolean(App.INCREDIBLE_WORK, false)){
                // если прошло больше 16 минут и мы ещё не выдавали поздравления- выдадим
                notifier.sendCongratulationsNotification(mContext.getString(R.string.congratulations_1_message));
                preferences.edit().putBoolean(App.INCREDIBLE_WORK, true).apply();
            }
            else if(!App.getInstance().isFasting && difference > 1000 * 60 * 60 * 14 && !preferences.getBoolean(App.GREAT_WORK, false)){
                // если прошло больше 16 минут и мы ещё не выдавали поздравления- выдадим
                notifier.sendCongratulationsNotification(mContext.getString(R.string.congratulations_2_message));
                preferences.edit().putBoolean(App.GREAT_WORK, true).apply();
            }
            else if(!App.getInstance().isFasting && difference > 1000 * 60 * 60 * 12 && !preferences.getBoolean(App.GOOD_WORK, false)){
                // если прошло больше 16 минут и мы ещё не выдавали поздравления- выдадим
                notifier.sendCongratulationsNotification(mContext.getString(R.string.congratulations_3_message));
                preferences.edit().putBoolean(App.GOOD_WORK, true).apply();
            }
            SystemClock.sleep(1000);
        }
        if(isStopped()){
            notifier.mNotificationManager.cancel(MyNotify.TIMER_NOTIFICATION);
        }
        return Result.retry();
    }

    @NonNull
    private ForegroundInfo createForegroundInfo() {
        // Build a notification
        Context context = getApplicationContext();
        Notification notification = notifier.getTimerNotification();
        return new ForegroundInfo(MyNotify.TIMER_NOTIFICATION, notification);
    }


    /**
     * method to convert millisecond to time format
     */
    public static String hmsTimeFormatter(long milliSeconds) {
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));


    }
}
