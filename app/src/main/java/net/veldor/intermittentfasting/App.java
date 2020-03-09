package net.veldor.intermittentfasting;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import net.veldor.intermittentfasting.utils.MyNotify;
import net.veldor.intermittentfasting.workers.TimerWorker;

import java.util.HashMap;

public class App extends Application {
    public static final String FASTING_TIMER = "fasting timer";
    public static final String TIME_WORKER = "time worker";
    public static final String GREAT_WORK = "great work";
    public static final String GOOD_WORK = "good work";
    public static final String INCREDIBLE_WORK = "incredible work";


    private static App instance;
    private static SharedPreferences mSharedPreferences;
    private static MyNotify mNotify;
    public boolean isFasting;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mNotify = new MyNotify();
    }

    public static App getInstance() {
        return instance;
    }

    public static SharedPreferences getPreferences() {
        return mSharedPreferences;
    }
    public static MyNotify getNotifier(){
        return mNotify;
    }

    public void startTimer() {
        // сохраню время начала процесса
        long date = System.currentTimeMillis();
        mSharedPreferences.edit().putLong(FASTING_TIMER, date).remove(GOOD_WORK).remove(GREAT_WORK).remove(INCREDIBLE_WORK).apply();
        // запущу рабочего, отслеживающего таймер
        OneTimeWorkRequest handleTimerRequest = new OneTimeWorkRequest.Builder(TimerWorker.class).addTag(TIME_WORKER).build();
        WorkManager.getInstance(this).enqueueUniqueWork(TIME_WORKER, ExistingWorkPolicy.REPLACE, handleTimerRequest);
    }

    public void stopTimer() {
        mSharedPreferences.edit().remove(FASTING_TIMER).apply();
    }
}
