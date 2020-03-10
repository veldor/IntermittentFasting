package net.veldor.intermittentfasting.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.veldor.intermittentfasting.App;
import net.veldor.intermittentfasting.db.DbQueries;

public class MiscActionsReceiver extends BroadcastReceiver {
    public static final String EXTRA_ACTION_TYPE = "action type";
    public static final String ACTION_START_EATING = "cancel download";
    public static final String ACTION_START_FASTING = "pause download";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra(EXTRA_ACTION_TYPE);
        if(action != null){
            switch (action){
                case ACTION_START_EATING:
                case ACTION_START_FASTING:
                    DbQueries.saveCurrentPeriod();
                    App.getNotifier().sendPeriodFinishedNotification();
                    // запущу период пищевого окна
                    App.getInstance().isFasting = !App.getInstance().isFasting;
                    App.getInstance().startTimer();
                    // покажу уведомление о завершении прогресса
                    break;
            }
        }
    }
}
